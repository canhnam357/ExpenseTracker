package xyz.erotskoob.expensetracker.dto;

import java.time.Instant;

public record GeneralResponse(
        Instant timestamp,
        String message,
        int status,
        Object data
) { }