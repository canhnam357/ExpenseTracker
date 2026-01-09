package xyz.erotskoob.expensetracker.constant;

import lombok.Getter;

import java.time.ZonedDateTime;

public enum TokenType {
    EMAIL_VERIFICATION("Email Verification", 24),
    PASSWORD_RESET("Password Reset", 1),
    FORGOT_PASSWORD("Forgot Password", 1);
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