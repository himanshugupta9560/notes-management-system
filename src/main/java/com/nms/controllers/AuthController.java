package com.nms.controllers;

import com.nms.dtos.LoginRequestDto;
import com.nms.dtos.LoginResponseDto;
import com.nms.dtos.SignupRequestDto;
import com.nms.dtos.SignupResponseDto;
import com.nms.services.AuthService;
import lombok.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        return new ResponseEntity<>(authService.login(loginRequestDto), HttpStatus.ACCEPTED);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto signupRequestDto){
        System.out.println("username1 is : "+signupRequestDto.getUsername());
        System.out.println("password2 is : "+signupRequestDto.getPassword());
        return  new ResponseEntity<>(authService.signup(signupRequestDto),HttpStatus.CREATED);
    }

}
