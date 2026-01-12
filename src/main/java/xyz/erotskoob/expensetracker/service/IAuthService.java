package xyz.erotskoob.expensetracker.service;

import org.springframework.http.ResponseEntity;
import xyz.erotskoob.expensetracker.dto.authentication.ForgotPasswordRequest;
import xyz.erotskoob.expensetracker.dto.authentication.LoginRequest;
import xyz.erotskoob.expensetracker.dto.authentication.RegisterRequest;
import xyz.erotskoob.expensetracker.dto.authentication.ResetPasswordRequest;

public interface IAuthService {
    ResponseEntity<?> login(LoginRequest loginRequest);
    ResponseEntity<?> register(RegisterRequest registerRequest);
    ResponseEntity<?> verifyEmail(String tokenString);
    ResponseEntity<?> forgotPassword(ForgotPasswordRequest forgotPasswordRequest);
    ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordRequest, String tokenString);
    ResponseEntity<?> refreshToken(String refreshToken);
}
