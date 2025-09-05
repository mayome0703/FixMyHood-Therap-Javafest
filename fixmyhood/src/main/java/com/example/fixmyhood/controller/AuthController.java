package com.example.fixmyhood.controller;

import com.example.fixmyhood.dto.RegistrationDTO;
import com.example.fixmyhood.dto.JwtResponse;
import com.example.fixmyhood.dto.LoginRequest;
import com.example.fixmyhood.service.AppUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "User Auth")
public class AuthController {

    @Autowired
    private AppUserService userService;


    @PostMapping("/login")
    @Operation(summary = "User login")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User login successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user do not exists")
    })
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return userService.validateLogin(request);
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    })
    public ResponseEntity<String> register(@Valid @RequestBody RegistrationDTO request) {
        return userService.saveUser(request);
    }

}

