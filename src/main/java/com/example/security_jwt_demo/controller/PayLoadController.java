package com.example.security_jwt_demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Tag(name = "PayLoadController", description = "Контроллер тестирования доступа по ролям")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class PayLoadController {
    @Operation(
            description = "метод, доступный всем ролям и не зарегистрированным пользователям")
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }

    @Operation(
            description = "метод, доступный для USER/MODERATOR/ADMIN")
    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String userAccess(Principal principal) {
        return "User Content. Username %s".formatted(principal.getName());
    }

    @Operation(
            description = "метод, доступный только для MODERATOR")
    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR')")
    public String moderatorAccess() {
        return "Moderator Board.";
    }

    @Operation(
            description = "метод, доступный только для ADMIN")
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
}
