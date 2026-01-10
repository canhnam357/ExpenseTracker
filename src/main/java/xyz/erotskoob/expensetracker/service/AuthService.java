package xyz.erotskoob.expensetracker.service;

import org.springframework.http.ResponseEntity;
import xyz.erotskoob.expensetracker.dto.authentication.ForgotPasswordDTO;
import xyz.erotskoob.expensetracker.dto.authentication.LoginDTO;
import xyz.erotskoob.expensetracker.dto.authentication.RegisterDTO;
import xyz.erotskoob.expensetracker.dto.authentication.ResetPasswordDTO;

public interface AuthService {
    ResponseEntity<?> login(LoginDTO loginDTO);
    ResponseEntity<?> register(RegisterDTO registerDTO);
    ResponseEntity<?> verifyEmail(String tokenString);
    ResponseEntity<?> forgotPassword(ForgotPasswordDTO forgotPasswordDTO);
    ResponseEntity<?> resetPassword(ResetPasswordDTO resetPasswordDTO, String tokenString);
}
