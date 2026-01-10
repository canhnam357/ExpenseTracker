package xyz.erotskoob.expensetracker.constant;

import lombok.Getter;

import java.time.ZonedDateTime;

public enum TokenType {
    EMAIL_VERIFICATION("EMAIL_VERIFICATION", 24),
    PASSWORD_RESET("PASSWORD_RESET", 1);

    @Getter
    private final String type;
    private final int expiryHours;

    TokenType(String type, int expiryHours) {
        this.type = type;
        this.expiryHours = expiryHours;
    }

    public ZonedDateTime getExpiryDate() {
        return ZonedDateTime.now().plusHours(expiryHours);
    }
}