package btvn.web_service_qlkh_cdda.exception;

/**
 * Ném ra khi lỗi kết nối/upload lên dịch vụ lưu trữ đám mây (Cloudinary/S3).
 * GlobalExceptionHandler sẽ bắt exception này và trả về HTTP 503 Service Unavailable.
 */
public class CloudinaryUploadException extends RuntimeException {
    public CloudinaryUploadException(String message) {
        super(message);
    }
}