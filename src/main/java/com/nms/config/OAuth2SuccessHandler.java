package com.nms.config;

import com.nms.dtos.LoginResponseDto;
import com.nms.services.AuthService;
import com.nms.services.OAuth2Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value; // ✅ यह line जरूर डालो
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

    @Value("${frontend.redirect.url:http://localhost:5173}")
    private String frontendRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = token.getAuthorizedClientRegistrationId();

        ResponseEntity<LoginResponseDto> loginResponseDtoResponseEntity = oAuth2Service
                .handleOAuth2LoginRequest(oAuth2User, registrationId);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        System.out.println(attributes);
        response.setStatus(loginResponseDtoResponseEntity.getStatusCode().value());

        String redirectUrl = frontendRedirectUrl + "/callback?token=" 
                + loginResponseDtoResponseEntity.getBody().getJwt() 
                + "&id=" + loginResponseDtoResponseEntity.getBody().getId() 
                + "&given_name=" + attributes.get("given_name") 
                + "&picture=" + attributes.get("picture") 
                + "&email=" + attributes.get("email");
        
        response.sendRedirect(redirectUrl);
    }
}
