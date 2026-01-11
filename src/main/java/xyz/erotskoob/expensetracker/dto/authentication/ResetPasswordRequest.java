package xyz.erotskoob.expensetracker.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,
        @NotBlank(message = "Confirm password cannot be blank")
        @Size(min = 8, message = "Confirm password must be at least 8 characters")
        String confirmPassword) {
}
