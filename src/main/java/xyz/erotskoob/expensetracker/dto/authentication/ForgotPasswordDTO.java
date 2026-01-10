package xyz.erotskoob.expensetracker.dto.authentication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordDTO(

        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Invalid email address")
        String email
) {
}
