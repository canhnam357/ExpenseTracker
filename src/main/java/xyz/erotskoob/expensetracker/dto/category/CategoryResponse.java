package xyz.erotskoob.expensetracker.dto.category;

import xyz.erotskoob.expensetracker.entity.expense.Category;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name,
        BigDecimal allocatedAmount,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt,
        boolean isDeleted
) {
    public CategoryResponse(Category category) {
        this(category.getId(), category.getName(), category.getAllocatedAmount(), category.getCreatedAt(), category.getUpdatedAt(), category.isDeleted());
    }
}
