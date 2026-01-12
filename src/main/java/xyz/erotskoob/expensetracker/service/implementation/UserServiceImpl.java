package xyz.erotskoob.expensetracker.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.erotskoob.expensetracker.dto.GeneralResponse;
import xyz.erotskoob.expensetracker.dto.user.ChangePasswordRequest;
import xyz.erotskoob.expensetracker.dto.user.ProfileResponse;
import xyz.erotskoob.expensetracker.entity.auth.User;
import xyz.erotskoob.expensetracker.exception.AuthenticationException;
import xyz.erotskoob.expensetracker.exception.BadRequestException;
import xyz.erotskoob.expensetracker.repository.UserRepository;
import xyz.erotskoob.expensetracker.service.IUserService;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public ResponseEntity<?> getProfile(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AuthenticationException("User not found"));
        ProfileResponse profileResponse = new ProfileResponse(user);
        GeneralResponse<ProfileResponse> res = new GeneralResponse<>(Instant.now(), "Profile fetched successfully", 200, profileResponse);
        return ResponseEntity.ok().body(res);
    }

    @Override
    public ResponseEntity<?> changePassword(UUID userId, ChangePasswordRequest changePasswordRequest) {
        if (!changePasswordRequest.newPassword().equals(changePasswordRequest.confirmPassword())) {
            throw new BadRequestException("New password and confirm password should be same");
        }
        User user = userRepository.findById(userId).orElseThrow(() -> new AuthenticationException("User not found"));
        if (!passwordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }
        ZonedDateTime now = ZonedDateTime.now();
        user.setPasswordChangedAt(now);
        user.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
        userRepository.save(user);
        return ResponseEntity.ok(new GeneralResponse<>(Instant.now(), "Password changed successfully", 200, null));
    }
}
