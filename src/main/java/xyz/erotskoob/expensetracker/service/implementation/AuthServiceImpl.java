package xyz.erotskoob.expensetracker.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.erotskoob.expensetracker.constant.Role;
import xyz.erotskoob.expensetracker.dto.GeneralResponse;
import xyz.erotskoob.expensetracker.dto.authentication.LoginDTO;
import xyz.erotskoob.expensetracker.dto.authentication.RegisterDTO;
import xyz.erotskoob.expensetracker.entity.RefreshToken;
import xyz.erotskoob.expensetracker.entity.User;
import xyz.erotskoob.expensetracker.exception.BadRequestException;
import xyz.erotskoob.expensetracker.exception.ResourceExistedException;
import xyz.erotskoob.expensetracker.repository.RefreshTokenRepository;
import xyz.erotskoob.expensetracker.repository.UserRepository;
import xyz.erotskoob.expensetracker.security.JwtService;
import xyz.erotskoob.expensetracker.security.UserDetail;
import xyz.erotskoob.expensetracker.service.AuthService;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    @Transactional
    public ResponseEntity<?> login(LoginDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        assert userDetail != null;
        refreshTokenRepository.revokeRefreshToken(userDetail.getUserId());

        String accessToken = jwtService.generateAccessToken(userDetail);

        GeneralResponse<String> res = new GeneralResponse<>(Instant.now(), "Login successfully", 200, accessToken);

        String refreshToken = jwtService.generateRefreshToken(userDetail);

        RefreshToken refreshToken_db = RefreshToken.builder()
                .token(refreshToken)
                .revoked(false)
                .user(userDetail.getUser())
                .build();

        refreshTokenRepository.save(refreshToken_db);

        ResponseCookie refreshTokenCookie = ResponseCookie
                .from("refreshToken", refreshToken)
                .httpOnly(true)
                .sameSite("Lax")      // dev localhost
                .secure(false)        // dev HTTP
                .path("/api/auth/refresh-token/reset")
                .maxAge(Duration.ofDays(7))
                .build();

        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(res);
    }

    @Override
    @Transactional
    public ResponseEntity<?> register(RegisterDTO request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new ResourceExistedException("User with username: " + request.username() + " already exists");
        }
        if (!request.password().equals(request.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .enabled(true)
                .build();
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
