package xyz.erotskoob.expensetracker.dto;

import java.time.Instant;

public record GeneralResponse<T>(Instant timestamp, String message, int status, T data) { }