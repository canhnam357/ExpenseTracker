package xyz.erotskoob.expensetracker.dto.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UpdateExpenseRequest(
        @NotBlank(message = "Category cannot be blank")
        @Size(min = 3, max = 255, message = "Category must be at least 3 characters and at most 255 characters")
        String description,
        @NotNull(message = "Amount cannot be blank")
        BigDecimal amount,
        @NotNull(message = "Date cannot be blank")
        LocalDate expenseDate
) {
}
