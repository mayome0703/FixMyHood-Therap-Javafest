package com.example.fixmyhood.service;

import com.example.fixmyhood.dto.TaskRequestDTO;
import com.example.fixmyhood.dto.TaskResponseDTO;

import java.util.List;

public interface TaskService {
    List<TaskResponseDTO> getAllTasks();
    TaskResponseDTO createTask(TaskRequestDTO dto, long adminId);
    void updateTask(Long id, TaskRequestDTO dto, Long adminId);
    void deleteTask(Long id);
    Boolean isAdminOwner(Long id, Long adminId);
}

