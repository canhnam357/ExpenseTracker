package xyz.erotskoob.expensetracker.dto;

import java.time.Instant;

public record ErrorDetails(Instant timestamp, String detail, int status, String title) {
}