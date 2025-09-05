package com.example.fixmyhood.service.implementation;

import com.example.fixmyhood.dto.JwtResponse;
import com.example.fixmyhood.exception.ResourceNotFoundException;
import com.example.fixmyhood.model.Admin;
import com.example.fixmyhood.model.AppUser;
import com.example.fixmyhood.repository.AdminRepository;
import com.example.fixmyhood.repository.AppUserRepository;
import com.example.fixmyhood.security.JwtUtils;
import com.example.fixmyhood.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private final AdminRepository adminRepository;

    @Autowired
    private final AppUserRepository userRepository;

    @Autowired
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private final JwtUtils jwtUtils;

    @Override
    public ResponseEntity<JwtResponse> validateLogin(String credential, String password) {
        try{
            Admin admin = adminRepository.findByEmailOrUsername(credential, credential)
                    .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

            if (!passwordEncoder.matches(password, admin.getPassword())) {
                throw new BadCredentialsException("Invalid password");
            }

            String jwt = jwtUtils.generateToken(new AdminDetailsImpl(admin), "ADMIN");

            return ResponseEntity.ok(new JwtResponse(jwt, admin.getEmail(), "Bearer"));
        }
        catch(Exception e){
            e.printStackTrace();
            throw e; //new BadCredentialsException("Invalid credentials");
        }
    }

    @Override
    public ResponseEntity<List<AppUser>> getAllUsers() {
        List<AppUser> users = userRepository.findAll();
        return ResponseEntity.ok(users);
    }

    @Override
    public void deleteUser(Long id) {
        AppUser appUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(appUser);
        System.out.println("User deleted successfully");
    }
}
