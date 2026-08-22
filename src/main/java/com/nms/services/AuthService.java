package com.nms.services;

import com.nms.config.AuthUtil;
import com.nms.dtos.LoginRequestDto;
import com.nms.dtos.LoginResponseDto;
import com.nms.dtos.SignupRequestDto;
import com.nms.dtos.SignupResponseDto;
import com.nms.entities.AuthProviderType;
import com.nms.entities.Role;
import com.nms.entities.User;
import com.nms.repositories.UserRepositories;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
//import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthUtil authUtil;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getUsername(),
                        loginRequestDto.getPassword()));

        User user = (User) authentication.getPrincipal();
        String token = authUtil.generateAccessToken(user);
        return new LoginResponseDto(token, user.getId());
    }

    public SignupResponseDto signup(SignupRequestDto signupRequestDto) {
        Role role = signupRequestDto.getRole() != null ? signupRequestDto.getRole() : Role.ROLE_USER;
        User user = User.builder()
                .username(signupRequestDto.getUsername())
                .role(role)
                .authProviderType(AuthProviderType.EMAIL)
                .password(signupRequestDto.getPassword())
                .build();

        user = userService.registerUser(user);
        return new SignupResponseDto(user.getUsername(), "User registered successfully", user.getId());
    }
}
