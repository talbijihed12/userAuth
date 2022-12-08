package com.project.authentification.controller;

import com.project.authentification.dto.AuthenticationResponse;
import com.project.authentification.payload.request.LoginRequest;
import com.project.authentification.dto.RegisterRequest;
import com.project.authentification.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {
    private final AuthService authService;
    @PostMapping("/signup")
    public ResponseEntity<String> signup (@RequestBody RegisterRequest registerRequest){
        authService.signup(registerRequest);
        return new ResponseEntity<>("User Registration successfully", HttpStatus.OK);

    }
    @GetMapping("accountVerification/{token}")
    public ResponseEntity<String> verifyAccount (@PathVariable String token){
        authService.verifyAccount(token);
        return new ResponseEntity<>("Account Activated Successfully",HttpStatus.OK);
    }
    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody LoginRequest loginRequest){
        return authService.login(loginRequest);

    }
}
