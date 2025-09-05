package com.example.fixmyhood.service;

import com.example.fixmyhood.dto.JwtResponse;
import com.example.fixmyhood.model.Admin;
import com.example.fixmyhood.model.AppUser;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AdminService {
    ResponseEntity<JwtResponse> validateLogin(String credential, String password);
    ResponseEntity<List<AppUser>> getAllUsers();
    void deleteUser(Long id);
}
