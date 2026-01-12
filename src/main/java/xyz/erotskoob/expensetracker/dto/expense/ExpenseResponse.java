package xyz.erotskoob.expensetracker.dto.expense;

import xyz.erotskoob.expensetracker.entity.expense.Expense;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record ExpenseResponse(
        UUID id,
        UUID userId,
        UUID categoryId,
        BigDecimal amount,
        String description,
        boolean isDeleted,
        ZonedDateTime createdDate,
        ZonedDateTime updatedDate
) {
    public ExpenseResponse(Expense expense) {
        this(expense.getId(), expense.getUser().getId(), expense.getCategory().getId(), expense.getAmount(), expense.getDescription(), expense.isDeleted(), expense.getCreatedAt(), expense.getUpdatedAt());
    }
}
