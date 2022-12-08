package com.project.authentification.service;


import com.project.authentification.model.User;
import com.project.authentification.model.VerificationToken;
import com.project.authentification.payload.request.LoginRequest;
import com.project.authentification.payload.request.SignUpRequest;
import com.project.authentification.payload.response.JwtResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

public interface IAuthService {


    void fetchUserAndEnable(VerificationToken verificationToken);


    JwtResponse authenticateUser(LoginRequest loginRequest);

    void registerUser(SignUpRequest signUpRequest);

    void verifyAccount(String token);

    User getCurrentUser();

    boolean isLoggedIn();
}
