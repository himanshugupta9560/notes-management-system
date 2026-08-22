package com.nms.config;

import com.nms.dtos.LoginResponseDto;
import com.nms.services.AuthService;
import com.nms.services.OAuth2Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper objectMapper;
    private final OAuth2Service oAuth2Service;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        // here we take token from google
        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        // here we extract user from google
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // here we also take registration id jisme google / facebok jisse bhi login kiya
        // hot ahe wo likha hota he
        String registrationId = token.getAuthorizedClientRegistrationId();

        // yaha per ham ek aisa method create kar rahe he jo authe service me OAuth2
        // login ko handler
        ResponseEntity<LoginResponseDto> loginResponseDtoResponseEntity = oAuth2Service
                .handleOAuth2LoginRequest(oAuth2User, registrationId);

        //ab ham yaha per user ki sari information ko get karenge gmail se
        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println(attributes);
        response.setStatus(loginResponseDtoResponseEntity.getStatusCode().value());
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.getWriter().write(objectMapper.writeValueAsString(loginResponseDtoResponseEntity.getBody()));

        // this is different thing
        String redirectUrl = "http://localhost:5173/callback?token=" + loginResponseDtoResponseEntity.getBody().getJwt() + "&id=" + loginResponseDtoResponseEntity.getBody().getId() + "&given_name=" + attributes.get("given_name") + "&picture=" + attributes.get("picture") + "&email=" + attributes.get("email");
        response.sendRedirect(redirectUrl);
    }
}
