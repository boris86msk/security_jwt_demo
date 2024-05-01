package com.example.security_jwt_demo.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.security_jwt_demo.dtos.request.LoginRequestDTO;
import com.example.security_jwt_demo.dtos.request.SignupRequestDTO;
import com.example.security_jwt_demo.dtos.response.JwtResponseDTO;
import com.example.security_jwt_demo.dtos.response.MessageResponseDTO;
import com.example.security_jwt_demo.dtos.response.RegisterDTO;
import com.example.security_jwt_demo.jwt.JwtUtils;
import com.example.security_jwt_demo.services.PersonService;
import com.example.security_jwt_demo.userdetails.UserDetailsImpl;

import java.util.List;

@Tag(name = "AuthController", description = "Контроллер аунтификации и авторизации")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private PersonService personService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Operation(
            summary = "Registration",
            description = "Регистрация нового пользователя в системе")
    @PostMapping("/signup")
    public ResponseEntity<MessageResponseDTO> registerUser(@Valid @RequestBody SignupRequestDTO signUpRequest) {
        RegisterDTO registerDTO = personService.signUp(signUpRequest);
        return ResponseEntity.status(registerDTO.getStatus())
                .body(new MessageResponseDTO(registerDTO.getMessage()));
    }


    @Operation(
            summary = "Authentication",
            description = "Аунтификация по юзернайму и паролю")
    @PostMapping("/signin")
    public ResponseEntity<JwtResponseDTO> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
        return ResponseEntity
                .ok(new JwtResponseDTO(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }


}
