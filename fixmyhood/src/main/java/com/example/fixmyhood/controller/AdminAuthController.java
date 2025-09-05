package com.example.fixmyhood.controller;

import com.example.fixmyhood.dto.JwtResponse;
import com.example.fixmyhood.dto.LoginRequest;
import com.example.fixmyhood.dto.RegistrationDTO;
import com.example.fixmyhood.model.AppUser;
import com.example.fixmyhood.security.AuthUtils;
import com.example.fixmyhood.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Auth")
public class AdminAuthController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private HttpServletRequest request;

    @PostMapping("/auth/login")
    @Operation(summary = "Admin login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return adminService.validateLogin(request.getEmail(), request.getPassword());
    }

    @GetMapping("/users")
    @Operation(summary = "List all users")
    public ResponseEntity<List<AppUser>> getAllUsers() {
        if (!AuthUtils.isAdmin(request)) throw new AccessDeniedException("Only admins can access this endpoint");

        return adminService.getAllUsers();
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete a user by ID")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        if (!AuthUtils.isAdmin(request)) throw new AccessDeniedException("Only admins can access this endpoint");

        adminService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully");
    }
}
