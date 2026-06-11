package btvn.web_service_qlkh_cdda.exception;

import btvn.web_service_qlkh_cdda.model.dto.response.ApiDataResonse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Bắt lỗi RuntimeException (Ví dụ: "Tên đăng nhập đã tồn tại")
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiDataResonse<?>> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiDataResonse<>(false, ex.getMessage(), null, null, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    // Bắt lỗi Validation Request (Khi người dùng nhập thiếu dữ liệu)
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
}