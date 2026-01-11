package xyz.erotskoob.expensetracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.erotskoob.expensetracker.dto.authentication.ForgotPasswordRequest;
import xyz.erotskoob.expensetracker.dto.authentication.LoginRequest;
import xyz.erotskoob.expensetracker.dto.authentication.RegisterRequest;
import xyz.erotskoob.expensetracker.dto.authentication.ResetPasswordRequest;
import xyz.erotskoob.expensetracker.service.IAuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request){
        return authService.login(request);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request){
        return authService.register(request);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestParam String token){
        return authService.verifyEmail(token);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request){
        return authService.forgotPassword(request);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request, @Valid @RequestParam String token){
        return authService.resetPassword(request, token);
    }
}