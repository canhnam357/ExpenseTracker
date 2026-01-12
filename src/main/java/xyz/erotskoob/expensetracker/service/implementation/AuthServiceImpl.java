package xyz.erotskoob.expensetracker.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.erotskoob.expensetracker.constant.TokenType;
import xyz.erotskoob.expensetracker.dto.GeneralResponse;
import xyz.erotskoob.expensetracker.dto.authentication.*;
import xyz.erotskoob.expensetracker.entity.auth.RefreshToken;
import xyz.erotskoob.expensetracker.entity.auth.User;
import xyz.erotskoob.expensetracker.entity.auth.VerificationToken;
import xyz.erotskoob.expensetracker.exception.AuthenticationException;
import xyz.erotskoob.expensetracker.exception.BadRequestException;
import xyz.erotskoob.expensetracker.exception.ResourceAlreadyExistsException;
import xyz.erotskoob.expensetracker.repository.RefreshTokenRepository;
import xyz.erotskoob.expensetracker.repository.UserRepository;
import xyz.erotskoob.expensetracker.security.JwtService;
import xyz.erotskoob.expensetracker.security.UserDetail;
import xyz.erotskoob.expensetracker.messaging.email.EmailProducer;
import xyz.erotskoob.expensetracker.service.IAuthService;
import xyz.erotskoob.expensetracker.service.ITokenService;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ITokenService tokenService;
    private final EmailProducer emailService;

    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;


    @Override
    @Transactional
    public ResponseEntity<?> login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        if (userDetail == null) {
            throw new BadRequestException("Invalid username or password");
        }
        ZonedDateTime now = ZonedDateTime.now();
        User user = userRepository.findByUsername(request.username()).orElseThrow(() -> new BadRequestException("Invalid username or password"));
        user.setLastLoginAt(now);
        userRepository.save(user);
        GeneralResponse<AuthResponse> res = new GeneralResponse<>(Instant.now(), "Login successfully!", 200, createAccessToken(userDetail, now));
        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(userDetail, now);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(res);
    }

    @Override
    @Transactional
    public ResponseEntity<?> register(RegisterRequest request) {
        if (!request.password().equals(request.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }
        Optional<User> userSameEmail = userRepository.findByEmail(request.email());
        if (userSameEmail.isPresent()) {
            if (!userSameEmail.get().isEnabled()) {
                VerificationToken token = tokenService.createToken(
                        userSameEmail.get(),
                        TokenType.EMAIL_VERIFICATION
                );
                emailService.sendEmailVerificationMessage(userSameEmail.get(), token.getToken(), token.getTokenType().getType());
                GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Account with this email existed, please check email for verification!", 204, null);
                return ResponseEntity.status(HttpStatusCode.valueOf(204)).body(res);
            }
            throw new ResourceAlreadyExistsException("Email already exists");
        }
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new ResourceAlreadyExistsException("Username already exists");
        }
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
        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Registration successfully, please check your email for verification!", 204, null);
        return ResponseEntity.status(HttpStatusCode.valueOf(204)).body(res);
    }

    @Override
    @Transactional
    public ResponseEntity<?> verifyEmail(String tokenString) {
        VerificationToken token = tokenService.validateToken(
                tokenString,
                TokenType.EMAIL_VERIFICATION
        );
        User user = token.getUser();
        user.setEnabled(true);
        user.setEmailVerifiedAt(ZonedDateTime.now());
        userRepository.save(user);
        tokenService.markTokenAsUsed(token);
        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Email verified successfully, now you can login!", 204, null);
        return ResponseEntity.ok().body(res);
    }

    @Override
    @Transactional
    public ResponseEntity<?> forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        if (userRepository.findByEmail(forgotPasswordRequest.email()).isEmpty()) {
            throw new BadRequestException("Email not found");
        }

        User user = userRepository.findByEmail(forgotPasswordRequest.email()).get();
        VerificationToken token = tokenService.createToken(user, TokenType.PASSWORD_RESET);
        emailService.sendEmailVerificationMessage(user, token.getToken(), token.getTokenType().getType());
        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Reset password email sent successfully, please check your email for verification!", 204, null);
        return ResponseEntity.status(HttpStatusCode.valueOf(204)).body(res);
    }

    @Override
    @Transactional
    public ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordRequest, String tokenString) {

        if (!resetPasswordRequest.password().equals(resetPasswordRequest.confirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        VerificationToken token = tokenService.validateToken(tokenString, TokenType.PASSWORD_RESET);
        tokenService.markTokenAsUsed(token);

        ZonedDateTime now = ZonedDateTime.now();

        User user = token.getUser();
        user.setPasswordChangedAt(now);
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.password()));
        userRepository.save(user);

        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Password reset successfully, now you can login!", 204, null);

        return ResponseEntity.status(HttpStatusCode.valueOf(204)).body(res);
    }

    @Override
    @Transactional
    public ResponseEntity<?> refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        RefreshToken RT = refreshTokenRepository.findByUserUsernameAndRevokedIsFalse(username).orElseThrow(() -> new AuthenticationException("Invalid refresh token"));
        if (RT.isRevoked()) {
            throw new AuthenticationException("Refresh token revoked");
        }
        if (RT.getExpiryDate().isBefore(ZonedDateTime.now())) {
            refreshTokenRepository.revokeRefreshToken(RT.getUser().getId(), ZonedDateTime.now());
            throw new AuthenticationException("Refresh token expired");
        }
        UserDetail userDetail = new UserDetail(RT.getUser());
        ZonedDateTime now = ZonedDateTime.now();
        ResponseCookie refreshTokenCookie = createRefreshTokenCookie(userDetail, now);
        GeneralResponse<AuthResponse> res = new GeneralResponse<>(Instant.now(), "Refresh Token successfully!", 200, createAccessToken(userDetail, now));
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(res);
    }

    @Override
    @Transactional
    public ResponseEntity<?> logout(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AuthenticationException("User not found"));
        refreshTokenRepository.revokeRefreshToken(user.getId(), ZonedDateTime.now());
        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Logout successfully!", 204, null);

        ResponseCookie refreshTokenCookie = ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .sameSite("Lax")
                .secure(false)
                .path("/api/auth/refresh-token")
                .maxAge(Duration.ofDays(0))
                .build();

        return ResponseEntity.status(HttpStatusCode.valueOf(204)).header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()).body(res);
    }

    AuthResponse createAccessToken(UserDetail userDetail, ZonedDateTime now) {
        String accessToken = jwtService.generateAccessToken(userDetail, now);
        UserResponse userResponse = new UserResponse(userDetail.getUsername(), userDetail.getEmail(), userDetail.getRole().name());
        return new AuthResponse(accessToken, "Bearer", userResponse);
    }

    ResponseCookie createRefreshTokenCookie(UserDetail userDetail, ZonedDateTime now) {
        refreshTokenRepository.revokeRefreshToken(userDetail.getUserId(), now);
        String refreshToken = jwtService.generateRefreshToken(userDetail, now);
        ZonedDateTime expiryDate = now.plusSeconds(refreshTokenExpiration / 1000);
        RefreshToken refreshToken_db = RefreshToken.builder()
                .token(refreshToken)
                .user(userDetail.getUser())
                .createdDate(now)
                .expiryDate(expiryDate)
                .build();
        refreshTokenRepository.save(refreshToken_db);

        return ResponseCookie
                .from("refreshToken", refreshToken)
                .httpOnly(true)
                .sameSite("Lax")
                .secure(false)
                .path("/api/auth/refresh-token")
                .maxAge(Duration.ofDays(7))
                .build();
    }

}
