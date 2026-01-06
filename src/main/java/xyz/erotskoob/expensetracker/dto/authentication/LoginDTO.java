package xyz.erotskoob.expensetracker.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginDTO(
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 6, max = 20, message = "Username must be between 6 and 20 characters")
        String username,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password) {
}