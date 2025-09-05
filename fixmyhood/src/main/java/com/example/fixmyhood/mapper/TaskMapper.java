package com.example.fixmyhood.mapper;

import com.example.fixmyhood.dto.TaskResponseDTO;
import com.example.fixmyhood.model.Task;

public class TaskMapper {

    public static TaskResponseDTO toDTO(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getAssignedTo().getUsername(),
                task.getAssignedBy().getUsername(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}

