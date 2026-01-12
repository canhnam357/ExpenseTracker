package xyz.erotskoob.expensetracker.dto.category;

import xyz.erotskoob.expensetracker.entity.expense.Category;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        BigDecimal totalAmount,
        String hexColorCode,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt,
        boolean isDeleted
) {
    public CategoryResponse(Category category) {
        this(category.getId(), category.getName(), category.getTotalAmount(), category.getHexColorCode(), category.getCreatedAt(), category.getUpdatedAt(), category.isDeleted());
    }
}
