package com.example.security_jwt_demo.services;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.security_jwt_demo.dtos.request.SignupRequestDTO;
import com.example.security_jwt_demo.dtos.response.RegisterDTO;
import com.example.security_jwt_demo.models.ERole;
import com.example.security_jwt_demo.models.Person;
import com.example.security_jwt_demo.models.Role;
import com.example.security_jwt_demo.repository.PersonRepository;
import com.example.security_jwt_demo.repository.RoleRepository;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class PersonService {
    private PasswordEncoder encoder;
    private final PersonRepository personRepository;
    private final RoleRepository roleRepository;

    public RegisterDTO signUp(SignupRequestDTO signUpRequest) {
        if (Boolean.TRUE.equals(personRepository.existsByUsername(signUpRequest.getUsername()))
                || Boolean.TRUE.equals(personRepository.existsByEmail(signUpRequest.getEmail()))) {
            return new RegisterDTO(HttpStatus.BAD_REQUEST, "Error: Username or Email is already taken!" );
        }

        Person person = new Person(signUpRequest.getUsername(), signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();
        Supplier<RuntimeException> supplier = () -> new RuntimeException("Error: Role is not found.");

        if (strRoles == null) {
            roles.add(roleRepository.findByName(ERole.ROLE_USER).orElseThrow(supplier));
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin" -> roles.add(roleRepository.findByName(ERole.ROLE_ADMIN).orElseThrow(supplier));
                    case "mod" -> roles.add(roleRepository.findByName(ERole.ROLE_MODERATOR).orElseThrow(supplier));
                    default -> roles.add(roleRepository.findByName(ERole.ROLE_USER).orElseThrow(supplier));
                }
            });
        }
        person.setRoles(roles);
        personRepository.save(person);
        return new RegisterDTO(HttpStatus.OK, "Person registered successfully!" );
    }
}
