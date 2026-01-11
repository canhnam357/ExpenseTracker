package xyz.erotskoob.expensetracker.dto.authentication;

public record UserResponse(String username, String email, String role) {
    public UserResponse(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
