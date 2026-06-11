package btvn.web_service_qlkh_cdda.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import btvn.web_service_qlkh_cdda.model.entity.Role;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDTO {
    @NotBlank(message = "Tên đăng nhập không được để trống")
    private String username;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 3, message = "Mật khẩu phải từ 3 ký tự trở lên")
    private String password;

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @Email(message = "Email không đúng định dạng")
    @NotBlank(message = "Email không được để trống")
    private String email;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    private List<Role> roles;
}