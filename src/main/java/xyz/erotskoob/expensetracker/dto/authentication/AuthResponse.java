package xyz.erotskoob.expensetracker.dto.authentication;

public record AuthResponse(String accessToken, String tokenType, UserResponse user) {
}
