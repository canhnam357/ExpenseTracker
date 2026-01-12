package xyz.erotskoob.expensetracker.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
        @NotBlank(message = "Old password cannot be blank")
        @Size(min = 8, message = "Old password must be at least 8 characters")
        String oldPassword,
        @NotBlank(message = "New password cannot be blank")
        @Size(min = 8, message = "New password must be at least 8 characters")
        String newPassword,
        @NotBlank(message = "Confirm password cannot be blank")
        @Size(min = 8, message = "Confirm password must be at least 8 characters")
        String confirmPassword
) {
}
