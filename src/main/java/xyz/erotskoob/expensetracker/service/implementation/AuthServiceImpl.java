package xyz.erotskoob.expensetracker.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import xyz.erotskoob.expensetracker.constant.TokenType;
import xyz.erotskoob.expensetracker.dto.GeneralResponse;
import xyz.erotskoob.expensetracker.dto.authentication.LoginDTO;
import xyz.erotskoob.expensetracker.dto.authentication.RegisterDTO;
import xyz.erotskoob.expensetracker.entity.RefreshToken;
import xyz.erotskoob.expensetracker.entity.User;
import xyz.erotskoob.expensetracker.entity.VerificationToken;
import xyz.erotskoob.expensetracker.exception.BadRequestException;
import xyz.erotskoob.expensetracker.exception.ResourceExistedException;
import xyz.erotskoob.expensetracker.repository.RefreshTokenRepository;
import xyz.erotskoob.expensetracker.repository.UserRepository;
import xyz.erotskoob.expensetracker.security.JwtService;
import xyz.erotskoob.expensetracker.security.UserDetail;
import xyz.erotskoob.expensetracker.service.AuthService;
import xyz.erotskoob.expensetracker.service.EmailProducer;
import xyz.erotskoob.expensetracker.service.TokenService;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenService tokenService;
    private final EmailProducer emailService;

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
        if (!request.password().equals(request.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new ResourceExistedException("User with username: " + request.username() + " already exists");
        }

        // Case when email existed but not verified: NOT IMPLEMENTED

        User newUser = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        userRepository.save(newUser);

        VerificationToken token = tokenService.createToken(
                newUser,
                TokenType.EMAIL_VERIFICATION
        );

        emailService.sendEmailVerificationMessage(newUser, token.getToken(), token.getTokenType().getType());

        GeneralResponse<String> res = new GeneralResponse<>(Instant.now(), "Registration successful", 200, "Please check your email to verify your account");

        return ResponseEntity.ok().body(res);
    }

    @Override
    public ResponseEntity<?> verifyEmail(String tokenString) {
        log.info("Verifying email with token: {}", tokenString);
        VerificationToken token = tokenService.validateToken(
                tokenString,
                TokenType.EMAIL_VERIFICATION
        );

        User user = token.getUser();
        user.setEnabled(true);
        user.setEmailVerifiedAt(ZonedDateTime.now());
        userRepository.save(user);

        tokenService.markTokenAsUsed(token);

        log.info("User {} verified email successfully", user.getUsername());
        GeneralResponse<String> res = new GeneralResponse<>(Instant.now(), "Email verified successfully", 200, "You can now login");
        return ResponseEntity.ok().body(res);
    }
}
