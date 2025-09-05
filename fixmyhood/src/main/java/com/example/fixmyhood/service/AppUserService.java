package com.example.fixmyhood.service;

import com.example.fixmyhood.dto.JwtResponse;
import com.example.fixmyhood.dto.LoginRequest;
import com.example.fixmyhood.dto.RegistrationDTO;
import org.springframework.http.ResponseEntity;

public interface AppUserService {
    ResponseEntity<String> saveUser(RegistrationDTO request);
    ResponseEntity<JwtResponse> validateLogin(LoginRequest request);
}
