package btvn.web_service_qlkh_cdda.security.principal;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import btvn.web_service_qlkh_cdda.model.entity.Role;
import btvn.web_service_qlkh_cdda.model.entity.Users;
import btvn.web_service_qlkh_cdda.repository.UserRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users users = userRepository.findByUsername(username).orElseThrow(() -> new NoSuchElementException("Không tồn tại user: " + username));

        return  CustomUserDetails.builder()
                .username(users.getUsername())
                .password(users.getPassword())
                .fullName(users.getFullName())
                .email(users.getEmail())
                .phone(users.getPhone())
                .enabled(users.getEnabled())
                .authorities(mapRoleToAuthority(users.getRoles()))
                .build();
    }

    private List<? extends GrantedAuthority> mapRoleToAuthority(List<Role> roles) {
        return roles.stream().map(r -> new SimpleGrantedAuthority(r.getRoleName())).toList();
    }
}
