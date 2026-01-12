package xyz.erotskoob.expensetracker.dto.user;

import xyz.erotskoob.expensetracker.entity.auth.User;

import java.util.UUID;

public record ProfileResponse(
        UUID id,
        String username,
        String email
) {
    public ProfileResponse(User user) {
        this(user.getId(), user.getUsername(), user.getEmail());
    }
}
