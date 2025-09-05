package com.example.fixmyhood.repository;

import com.example.fixmyhood.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedTo_Id(Long userId); // For filtering tasks by user if needed
}
