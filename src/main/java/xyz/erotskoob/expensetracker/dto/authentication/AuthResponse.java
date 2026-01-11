package xyz.erotskoob.expensetracker.dto.authentication;

public record AuthResponse(String accessToken, String tokenType, UserResponse user) {
    public AuthResponse(String accessToken, String tokenType, UserResponse user) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.user = user;
    }
}
