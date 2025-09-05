package com.example.fixmyhood.service.implementation;

import com.example.fixmyhood.dto.JwtResponse;
import com.example.fixmyhood.dto.LoginRequest;
import com.example.fixmyhood.dto.RegistrationDTO;
import com.example.fixmyhood.exception.ResourceNotFoundException;
import com.example.fixmyhood.model.AppUser;
import com.example.fixmyhood.repository.AppUserRepository;
import com.example.fixmyhood.security.JwtUtils;
import com.example.fixmyhood.service.AppUserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@AllArgsConstructor
@Service
public class AppUserServiceImpl implements AppUserService {

    @Autowired
    private final AuthenticationManager authenticationManager;

    @Autowired
    private final JwtUtils jwtUtils;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<String> saveUser(RegistrationDTO request) {
        try {
            boolean userByEmail = appUserRepository.findByEmail(request.getEmail()).isPresent();
            boolean userByUsername = appUserRepository.findByUsername(request.getUsername()).isPresent();
            if (userByEmail) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Error: Email is already registered");
            }

            if (userByUsername) {
                return ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body("Error: Username is already taken");
            }

            AppUser user = new AppUser();
            user.setUsername(request.getUsername());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());

            appUserRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (Exception e) {
            e.printStackTrace(); // Print root cause
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<JwtResponse> validateLogin(LoginRequest request) {
        try {
//            System.out.println("Login attempt for email: " + request.getEmail()); // Debug
        
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

//            System.out.println("Authentication successful"); // Debug
        
            SecurityContextHolder.getContext().setAuthentication(authentication);

            AppUser user = appUserRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            String jwt = jwtUtils.generateToken(new UserDetailsImpl(user), "USER");

            return ResponseEntity.ok(new JwtResponse(jwt, user.getEmail(), "Bearer"));
        } catch (Exception e) {
//            System.out.println("Login failed: " + e.getMessage()); // Debug
            e.printStackTrace();
            throw e; // Re-throw to see the actual error
        }
    }
}