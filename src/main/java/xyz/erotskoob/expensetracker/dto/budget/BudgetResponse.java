package xyz.erotskoob.expensetracker.dto.budget;

import xyz.erotskoob.expensetracker.entity.expense.Budget;
import xyz.erotskoob.expensetracker.entity.expense.Category;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public record BudgetResponse(
        UUID id,
        String name,
        BigDecimal totalAmount,
        LocalDate startDate,
        LocalDate endDate,
        boolean isDeleted,
        double spentPercentage,
        BigDecimal remaining,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt,
        boolean isActive,
        List<UUID> categoryIds
) {
    public BudgetResponse(Budget budget) {
        this(
            budget.getId(),
            budget.getName(),
            budget.getTotalAmount(),
            budget.getStartDate(),
            budget.getEndDate(),
            budget.isDeleted(),
            budget.getSpentPercentage(),
            budget.getRemaining(),
            budget.getCreatedAt(),
            budget.getUpdatedAt(),
            budget.isActive(),
            budget.getCategories().stream().map(Category::getId).toList());
    }
}
