package btvn.web_service_qlkh_cdda.service;

import btvn.web_service_qlkh_cdda.model.dto.request.UserDTO;
import btvn.web_service_qlkh_cdda.model.dto.request.UserLogin;
import btvn.web_service_qlkh_cdda.model.dto.response.JWTResponse;
import btvn.web_service_qlkh_cdda.model.entity.Users;

import java.util.List;

public interface UserService {
    // Phải có đủ 4 hàm này thì Impl mới không bị đỏ chữ @Override
    Users registerUser(UserDTO userDTO);
    JWTResponse login(UserLogin userLogin);
    void changePassword(String username, String oldPassword, String newPassword);
    List<Users> getAllUsers();
}