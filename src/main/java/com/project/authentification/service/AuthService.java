package com.project.authentification.service;

import com.project.authentification.dto.AuthenticationResponse;
import com.project.authentification.dto.LoginRequest;
import com.project.authentification.dto.RegisterRequest;
import com.project.authentification.exceptions.EmailException;
import com.project.authentification.model.NotificationMail;
import com.project.authentification.model.User;
import com.project.authentification.model.VerificationToken;
import com.project.authentification.repositories.UserRepo;
import com.project.authentification.repositories.VerificationTokenRepo;
import com.project.authentification.security.JwtProvider;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;


@Service
@AllArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepo verificationTokenRepo;
    private final UserRepo userRepo;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    @Transactional
    public void signup(RegisterRequest registerRequest){
        User user=new User();
        user.setNom(registerRequest.getNom());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEnabled(false);
        userRepo.save(user);
        String token = generateVerificationToken(user);
        mailService.sendMail(new NotificationMail("Please Activate your account",
                user.getEmail(),"Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/auth/accountVerification/" +token));
    }
    private String generateVerificationToken(User user){
        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken= new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationTokenRepo.save(verificationToken);
        return token;
    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken =verificationTokenRepo.findByToken(token);
        verificationToken.orElseThrow(()->new EmailException("Invalid Token"));
        fetchUserAndEnable(verificationToken.get());
    }

    @Transactional
    void fetchUserAndEnable(VerificationToken verificationToken) {
        String nom = verificationToken.getUser().getNom();
        User user =userRepo.findByNom(nom).orElseThrow(()->new EmailException("user with name"+nom+" is not found"));
        user.setEnabled(true);
        userRepo.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getNom(),
                loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String authenticationToken = jwtProvider.generateToken(authenticate);
        return new AuthenticationResponse(authenticationToken, loginRequest.getNom(),loginRequest.getEmail());

    }
}
