package btvn.web_service_qlkh_cdda.service.impl;

import btvn.web_service_qlkh_cdda.exception.CloudinaryUploadException;
import btvn.web_service_qlkh_cdda.service.CloudinaryService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    // Danh sách định dạng file được phép (PDF và Word)
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    );

    private static final long MAX_FILE_SIZE = 15 * 1024 * 1024; // 15MB

    @Override
    public String uploadFile(MultipartFile file) {
        // Kiểm tra file có rỗng không
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File không được để trống!");
        }

        // Kiểm tra định dạng file (chỉ cho phép PDF/Word)
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new RuntimeException("Định dạng file không hợp lệ! Chỉ chấp nhận PDF hoặc Word (.doc, .docx)");
        }

        // Kiểm tra dung lượng file (tối đa 15MB theo SRS)
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("Dung lượng file vượt quá giới hạn cho phép (tối đa 15MB)!");
        }

        try {
            // Gọi Cloudinary SDK để upload file
            // resource_type = "raw" để upload file PDF/Word (không phải ảnh/video)
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "resource_type", "raw",
                            "folder", "bao_cao_do_an"
                    )
            );

            // Lấy secure_url từ kết quả trả về của Cloudinary
            String secureUrl = (String) uploadResult.get("secure_url");
            if (secureUrl == null || secureUrl.isBlank()) {
                throw new RuntimeException("Cloudinary không trả về URL hợp lệ");
            }

            return secureUrl;

        } catch (IOException e) {
            // Lỗi kết nối đám mây → trả về 503 Service Unavailable (xử lý ở GlobalExceptionHandler)
            throw new CloudinaryUploadException("Lỗi kết nối dịch vụ lưu trữ đám mây: " + e.getMessage());
        }
    }
}