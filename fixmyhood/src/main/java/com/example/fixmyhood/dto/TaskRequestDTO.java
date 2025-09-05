package com.example.fixmyhood.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "User ID is required")  // ✅ Use @NotNull for Long fields
    private Long userId;

//    @NotNull(message = "Admin ID is required") // ✅ Use @NotNull for Long fields
//    private Long adminId;
}
