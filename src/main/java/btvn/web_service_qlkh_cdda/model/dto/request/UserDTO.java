package btvn.web_service_qlkh_cdda.model.dto.request;

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
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private List<Role> roles;
}
