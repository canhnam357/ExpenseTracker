package xyz.erotskoob.expensetracker.dto;

// For validation errors, to be included in a list inside ErrorDetails
public record FieldErrorDetail(
        String field,
        String message,
        String code // e.g., "NotNull", "Size", "Email"
) {}
