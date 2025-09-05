package com.example.fixmyhood.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDTO {
    private Long id;
    private String title;
    private String description;
    private String assignedToUsername;
    private String assignedByUsername;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
}

