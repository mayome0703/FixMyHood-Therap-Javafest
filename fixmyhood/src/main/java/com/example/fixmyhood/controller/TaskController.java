package com.example.fixmyhood.controller;

import com.example.fixmyhood.dto.TaskRequestDTO;
import com.example.fixmyhood.dto.TaskResponseDTO;
import com.example.fixmyhood.exception.ResourceNotFoundException;
import com.example.fixmyhood.repository.AdminRepository;
import com.example.fixmyhood.security.AuthUtils;
import com.example.fixmyhood.security.JwtUtils;
import com.example.fixmyhood.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Manage user-assigned tasks")
public class TaskController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private JwtUtils jwtUtils;

    @Operation(summary = "Get all tasks", description = "Returns a list of all tasks assigned to users.")
    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        if (!AuthUtils.isAdmin(request)) throw new AccessDeniedException("Only admins can create tasks");

        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @Operation(summary = "Create a new task")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task created successfully"),
            @ApiResponse(responseCode = "404", description = "User or Admin not found")
    })
    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO dto) {
        if (!AuthUtils.isAdmin(request)) throw new AccessDeniedException("Only admins can create tasks");

        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtils.extractEmail(token);
        Long adminId = adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"))
                .getId();

        TaskResponseDTO createdTask = taskService.createTask(dto, adminId);
        return new ResponseEntity<>(createdTask, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a task by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<String> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestDTO dto) {
        if (!AuthUtils.isAdmin(request)) throw new AccessDeniedException("Only admins can update tasks");

        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtils.extractEmail(token);
        Long adminId = adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"))
                .getId();

        if (taskService.isAdminOwner(id, adminId)) {
            taskService.updateTask(id, dto, adminId);
            return new ResponseEntity<>("Task updated successfully", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Admins can update only the tasks assigned by them", HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Delete a task by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTask(@PathVariable Long id) {
        if (!AuthUtils.isAdmin(request)) throw new AccessDeniedException("Only admins can delete tasks");

        String token = request.getHeader("Authorization").substring(7);
        String email = jwtUtils.extractEmail(token);
        Long adminId = adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"))
                .getId();

        if (taskService.isAdminOwner(id, adminId)) {
            taskService.deleteTask(id);
            return new ResponseEntity<>("Task deleted successfully", HttpStatus.OK);
        }
        else {
            return new ResponseEntity<>("Admins can delete only the tasks assigned by them", HttpStatus.UNAUTHORIZED);
        }
    }
}

