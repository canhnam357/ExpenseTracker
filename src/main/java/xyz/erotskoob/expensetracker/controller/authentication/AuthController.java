package xyz.erotskoob.expensetracker.controller.authentication;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.erotskoob.expensetracker.dto.authentication.LoginDTO;
import xyz.erotskoob.expensetracker.dto.authentication.RegisterDTO;
import xyz.erotskoob.expensetracker.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @RequestMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO request){
        return authService.login(request);
    }

    @RequestMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO request){
        return authService.register(request);
    }
}