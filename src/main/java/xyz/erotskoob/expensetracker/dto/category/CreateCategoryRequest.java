package xyz.erotskoob.expensetracker.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(
        @NotBlank(message = "Name cannot be blank")
        @Size(min = 3, max = 255, message = "Name must be at least 3 characters and at most 255 characters")
        String name,
        @NotBlank(message = "Hex color code cannot be blank")
        @Pattern(regexp = "^#[0-9a-fA-F]{6}$", message = "Invalid hex color code")
        String hexColorCode
) {
}
