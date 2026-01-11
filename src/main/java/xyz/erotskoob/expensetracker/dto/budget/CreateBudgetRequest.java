package xyz.erotskoob.expensetracker.dto.budget;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateBudgetRequest(
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 3, max = 255, message = "Name must be at least 3 characters and at most 255 characters")
        String name,
        @NotNull(message = "Amount cannot be blank")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,
        @NotNull(message = "Start date cannot be blank")
        LocalDate startDate,
        @NotNull(message = "End date cannot be blank")
        LocalDate endDate,
        List<UUID> categoryIds
) {
}
