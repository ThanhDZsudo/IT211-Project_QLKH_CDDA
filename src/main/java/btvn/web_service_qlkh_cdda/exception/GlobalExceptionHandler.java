package btvn.web_service_qlkh_cdda.exception;

import btvn.web_service_qlkh_cdda.model.dto.response.ApiDataResonse;
import btvn.web_service_qlkh_cdda.model.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Lỗi Validation (400 Bad Request) - trả về ApiDataResonse vì có errors map
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiDataResonse<?>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            errors.put(fieldName, error.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiDataResonse<>(false, "Dữ liệu đầu vào không hợp lệ", null, errors, HttpStatus.BAD_REQUEST));
    }

    /**
     * Không tìm thấy tài nguyên (404 Not Found) - dùng ErrorResponse chuẩn SRS
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(
            NoSuchElementException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request));
    }

    /**
     * Lỗi xác thực (401 Unauthorized) - dùng ErrorResponse chuẩn SRS
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(buildError(HttpStatus.UNAUTHORIZED, "Xác thực thất bại: " + ex.getMessage(), request));
    }

    /**
     * Không đủ quyền (403 Forbidden) - dùng ErrorResponse chuẩn SRS
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(buildError(HttpStatus.FORBIDDEN, "Bạn không có quyền thực hiện thao tác này", request));
    }

    /**
     * Lỗi kết nối dịch vụ đám mây (503 Service Unavailable) - theo UC-05
     */
    @ExceptionHandler(CloudinaryUploadException.class)
    public ResponseEntity<ErrorResponse> handleCloudinaryException(
            CloudinaryUploadException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(buildError(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage(), request));
    }

    /**
     * RuntimeException - phân loại HTTP status theo ngữ nghĩa:
     * - "đã tồn tại", "đã đăng ký", "đã nộp" → 409 Conflict (SRS quy định)
     * - "không hợp lệ", "Điểm số", "Định dạng", "Dung lượng" → 400 Bad Request
     * - "Không tìm thấy", "không tồn tại" → 404 Not Found
     * - Còn lại → 500 Internal Server Error
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(
            RuntimeException ex, HttpServletRequest request) {
        String message = ex.getMessage();
        HttpStatus status;

        if (message != null && (
                message.contains("đã tồn tại") ||
                        message.contains("đã đăng ký") ||
                        message.contains("đã nộp")
        )) {
            // 409 Conflict: Xung đột dữ liệu (SRS: "Đã nộp bài rồi, Email đã tồn tại")
            status = HttpStatus.CONFLICT;
        } else if (message != null && (
                message.contains("không hợp lệ") ||
                        message.contains("Điểm số") ||
                        message.contains("Định dạng file") ||
                        message.contains("Dung lượng") ||
                        message.contains("chưa nộp") ||
                        message.contains("Sai username") ||
                        message.contains("Mật khẩu hiện tại") ||
                        message.contains("File không được")
        )) {
            status = HttpStatus.BAD_REQUEST;
        } else if (message != null && (
                message.contains("Không tìm thấy") ||
                        message.contains("không tồn tại") ||
                        message.contains("không tìm thấy")
        )) {
            status = HttpStatus.NOT_FOUND;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return ResponseEntity.status(status)
                .body(buildError(status, message, request));
    }

    /**
     * Helper: tạo ErrorResponse theo đúng chuẩn SRS
     * { timestamp, status, error, message, path }
     */
    private ErrorResponse buildError(HttpStatus status, String message, HttpServletRequest request) {
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();
    }
}