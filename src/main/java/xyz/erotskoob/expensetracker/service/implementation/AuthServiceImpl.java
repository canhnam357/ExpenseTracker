package xyz.erotskoob.expensetracker.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import xyz.erotskoob.expensetracker.dto.authentication.AuthResponse;
import xyz.erotskoob.expensetracker.dto.authentication.LoginDTO;
import xyz.erotskoob.expensetracker.dto.authentication.RegisterDTO;
import xyz.erotskoob.expensetracker.dto.authentication.UserDTO;
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
import java.util.Optional;

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

    @Value("${application.security.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Override
    @Transactional
    public ResponseEntity<?> login(LoginDTO request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetail userDetail = (UserDetail) authentication.getPrincipal();
        assert userDetail != null;

        ZonedDateTime now = ZonedDateTime.now();

        String accessToken = jwtService.generateAccessToken(userDetail, now);
        UserDTO userDTO = new UserDTO(userDetail.getUsername(), userDetail.getEmail(), userDetail.getRole().name());
        AuthResponse authResponse = new AuthResponse(accessToken, "Bearer", userDTO);
        GeneralResponse<AuthResponse> res = new GeneralResponse<>(Instant.now(), "Login successfully", 200, authResponse);

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

        ResponseCookie refreshTokenCookie = ResponseCookie
                .from("refreshToken", refreshToken)
                .httpOnly(true)
                .sameSite("Lax")
                .secure(false)
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

        Optional<User> userSameEmail = userRepository.findByEmail(request.email());

        if (userSameEmail.isPresent()) {
            if (!userSameEmail.get().isEnabled()) {
                VerificationToken token = tokenService.createToken(
                        userSameEmail.get(),
                        TokenType.EMAIL_VERIFICATION
                );

                emailService.sendEmailVerificationMessage(userSameEmail.get(), token.getToken(), token.getTokenType().getType());

                GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Account with this email existed, please check email for verification!", 204, null);
                return ResponseEntity.ok().body(res);
            }
            throw new ResourceExistedException("Email already exists");
        }

        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new ResourceExistedException("Username already exists");
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

        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Email verified successfully, now you can login!", 204, null);
        return ResponseEntity.ok().body(res);
    }
}
