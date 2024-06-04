package org.example.jwt;

import lombok.RequiredArgsConstructor;
import org.example.controllers.AuthResponse;
import org.example.controllers.JwtService;
import org.example.controllers.LoginRequest;
import org.example.controllers.RegisterRequest;
import org.example.models.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private  final UserRepository userRepository;
    private  final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AccesTypeRepository accesTypeRepository;

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
        UserDetails user = userRepository.findByUserName(request.getUsername()).orElseThrow();
        String token = jwtService.getToken(user);
        log.info(token);
        log.info("hola esto deberia mostrarse");
        return AuthResponse.builder()
                .token(token)
                .build();
    }

    public AuthResponse register(RegisterRequest request) {
        User user = User.builder()
                .userName(request.getUsername())
                .userPassword(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .country(request.getCountry())
                .role(Role.USER)
                .accessType(accesTypeRepository.findByDescription(AccesType.WEB_LOGIN))
                .build();

        userRepository.save(user);

        return AuthResponse.builder()
                .token(jwtService.getToken(user))
                .build();
    }
}