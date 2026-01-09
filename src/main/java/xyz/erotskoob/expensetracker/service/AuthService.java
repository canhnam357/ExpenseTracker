package xyz.erotskoob.expensetracker.service;

import org.springframework.http.ResponseEntity;
import xyz.erotskoob.expensetracker.dto.authentication.LoginDTO;
import xyz.erotskoob.expensetracker.dto.authentication.RegisterDTO;

public interface AuthService {
    ResponseEntity<?> login(LoginDTO loginDTO);
    ResponseEntity<?> register(RegisterDTO registerDTO);
    ResponseEntity<?> verifyEmail(String tokenString);
}
