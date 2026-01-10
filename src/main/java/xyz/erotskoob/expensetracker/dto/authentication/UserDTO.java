package xyz.erotskoob.expensetracker.dto.authentication;

public record UserDTO(String username, String email, String role) {
    public UserDTO(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }
}
