package btvn.web_service_qlkh_cdda.exception;

import btvn.web_service_qlkh_cdda.model.dto.response.ApiDataResonse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Bắt lỗi Validation Request (400 Bad Request)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiDataResonse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiDataResonse<>(false, "Dữ liệu đầu vào không hợp lệ", null, errors, HttpStatus.BAD_REQUEST));
    }

    /**
     * Bắt lỗi không tìm thấy dữ liệu (404 Not Found)
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiDataResonse<?>> handleNotFoundException(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiDataResonse<>(false, ex.getMessage(), null, null, HttpStatus.NOT_FOUND));
    }

    /**
     * Bắt lỗi xác thực (401 Unauthorized)
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiDataResonse<?>> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiDataResonse<>(false, "Xác thực thất bại: " + ex.getMessage(), null, null, HttpStatus.UNAUTHORIZED));
    }

    /**
     * Bắt lỗi phân quyền (403 Forbidden)
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiDataResonse<?>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiDataResonse<>(false, "Bạn không có quyền thực hiện thao tác này", null, null, HttpStatus.FORBIDDEN));
    }

    /**
     * Bắt lỗi logic nghiệp vụ chung (RuntimeException → 400 hoặc 500 tùy message)
     * Phân loại rõ ràng hơn để tránh trả 500 cho lỗi logic thông thường.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiDataResonse<?>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage();

        // Các lỗi do nghiệp vụ (Bad Request - 400)
        if (message != null && (
                message.contains("đã tồn tại") ||
                        message.contains("không hợp lệ") ||
                        message.contains("chưa nộp") ||
                        message.contains("Điểm số") ||
                        message.contains("Sai username") ||
                        message.contains("Mật khẩu hiện tại")
        )) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiDataResonse<>(false, message, null, null, HttpStatus.BAD_REQUEST));
        }

        // Các lỗi không tìm thấy tài nguyên (404)
        if (message != null && (
                message.contains("Không tìm thấy") ||
                        message.contains("không tồn tại") ||
                        message.contains("không tìm thấy")
        )) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiDataResonse<>(false, message, null, null, HttpStatus.NOT_FOUND));
        }

        // Mặc định: lỗi hệ thống (500)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiDataResonse<>(false, message, null, null, HttpStatus.INTERNAL_SERVER_ERROR));
    }
}