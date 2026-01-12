package xyz.erotskoob.expensetracker.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "Username/Email cannot be blank")
        @Size(min = 6, max = 255, message = "Username/Email must be at least 6 characters and at most 255 characters")
        String username,

        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {
}