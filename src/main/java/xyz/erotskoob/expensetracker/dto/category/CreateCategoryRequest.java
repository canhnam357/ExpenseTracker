package xyz.erotskoob.expensetracker.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 3, max = 255, message = "Name must be at least 3 characters and at most 255 characters")
        String name
) {
}
