package com.nms.services;

import com.nms.config.AuthUtil;
import com.nms.dtos.LoginResponseDto;
import com.nms.dtos.SignupRequestDto;
import com.nms.entities.AuthProviderType;
import com.nms.entities.Role;
import com.nms.entities.User;
import com.nms.repositories.UserRepositories;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final AuthUtil authUtil;
    private final UserRepositories userRepository;
    private final UserService userService;

    public ResponseEntity<LoginResponseDto> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId) {
        AuthProviderType providerType = authUtil.getProviderTypeFromRegistrationId(registrationId);
        String providerId = authUtil.determineProviderIdFromAuth2User(oAuth2User, registrationId);

        User user = userRepository.findByProviderIdAndAuthProviderType(providerId, providerType).orElse(null);
        String email = oAuth2User.getAttribute("email");
        User userWithEmail = userRepository.findByUsername(email).orElse(null);

        if (user == null && userWithEmail == null) {
            // signup the user
            String username = authUtil.determineUsernameFromOAuth2User(oAuth2User, registrationId, providerId);
            user = User.builder()
                    .username(username)
                    .providerId(providerId)
                    .role(Role.ROLE_USER)
                    .authProviderType(providerType)
                    .build();
            user = userService.registerUser(user);

        } else if (user != null) {
            if (email != null && !email.isBlank() && email.equals(user.getUsername())) {
                user.setUsername(email);
                userService.saveUser(user);
            }
        } else {
            throw new BadCredentialsException(
                    "this user is already registered with " + userWithEmail.getAuthProviderType());
        }

        LoginResponseDto loginResponseDto = new LoginResponseDto(authUtil.generateAccessToken(user), user.getId());
        return new ResponseEntity<>(loginResponseDto, HttpStatus.OK);
    }
}
