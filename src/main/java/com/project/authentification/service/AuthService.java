package com.project.authentification.service;

import com.project.authentification.dto.AuthenticationResponse;
import com.project.authentification.payload.request.LoginRequest;
import com.project.authentification.dto.RegisterRequest;
import com.project.authentification.exceptions.EmailException;
import com.project.authentification.model.NotificationMail;
import com.project.authentification.model.Role;
import com.project.authentification.model.User;
import com.project.authentification.model.VerificationToken;
import com.project.authentification.model.enums.RoleEnum;
import com.project.authentification.payload.request.SignUpRequest;
import com.project.authentification.payload.response.JwtResponse;
import com.project.authentification.repositories.RoleRepository;
import com.project.authentification.repositories.UserRepo;
import com.project.authentification.repositories.VerificationTokenRepo;
import com.project.authentification.security.JwtProvider;
import com.project.authentification.security.service.UserDetailsImpl;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class AuthService implements IAuthService{

    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepo verificationTokenRepo;
    private final UserRepo userRepo;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private RoleRepository roleRepository;
    private PasswordEncoder encoder;
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
    @Override
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());
        return new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);
    }
    @Override
    public void registerUser(SignUpRequest signUpRequest) {
        if (userRepo.existsByNom(signUpRequest.getUsername())) {
            throw new com.communication.plateforme.utils.exceptions.BadRequestException("Error: Username is already taken!");
        }

        if (userRepo.existsByEmail(signUpRequest.getEmail())) {
            throw new com.communication.plateforme.utils.exceptions.BadRequestException("Error: Email is already in use!");
        }

        // Create new user's account
        User user = User.builder().nom(signUpRequest.getUsername()).email(signUpRequest.getEmail()).password(encoder.encode(signUpRequest.getPassword())).build();
        Set roles = new HashSet<>();
        Optional<Role> userRole = roleRepository.findByName(RoleEnum.ROLE_USER);
        if (!userRole.isPresent()) throw new RuntimeException("Error: Role is not found.");
        roles.add(userRole.get());


        user.setRoles(roles);

        userRepo.save(user);
        mailService.sendMail(new NotificationMail("Please Activate your Account",
                user.getEmail(), "Thank you for signing up to Spring Reddit, " +
                "please click on the below url to activate your account : " +
                "http://localhost:8080/api/auth/accountVerification/"));

    }

    public void verifyAccount(String token) {
        Optional<VerificationToken> verificationToken =verificationTokenRepo.findByToken(token);
        verificationToken.orElseThrow(()->new EmailException("Invalid Token"));
        fetchUserAndEnable(verificationToken.get());
    }



    @Override
    public void fetchUserAndEnable(VerificationToken verificationToken) {
        String nom = verificationToken.getUser().getNom();
        User user =userRepo.findByNom(nom).orElseThrow(()->new EmailException("user with name"+nom+" is not found"));
        user.setEnabled(true);
        userRepo.save(user);
    }

    public AuthenticationResponse login(LoginRequest loginRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        String authenticationToken = jwtProvider.generateToken(authenticate);
        return new AuthenticationResponse(authenticationToken, loginRequest.getUsername(),loginRequest.getPassword());

    }
    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByNom(username).orElseThrow(() -> new UsernameNotFoundException("User name not found -" + username));

    }


    @Override
    public boolean isLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return !(authentication instanceof AnonymousAuthenticationToken) && authentication.isAuthenticated();

    }


}
