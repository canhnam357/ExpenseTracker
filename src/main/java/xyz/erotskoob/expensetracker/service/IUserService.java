package xyz.erotskoob.expensetracker.service;

import org.springframework.http.ResponseEntity;
import xyz.erotskoob.expensetracker.dto.user.ChangePasswordRequest;

import java.util.UUID;

public interface IUserService {
    ResponseEntity<?> getProfile(UUID userId);
    ResponseEntity<?> changePassword(UUID userId, ChangePasswordRequest changePasswordRequest);
}
