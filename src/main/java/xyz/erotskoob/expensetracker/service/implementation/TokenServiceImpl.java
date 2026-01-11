package xyz.erotskoob.expensetracker.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import xyz.erotskoob.expensetracker.constant.TokenType;
import xyz.erotskoob.expensetracker.entity.auth.User;
import xyz.erotskoob.expensetracker.entity.auth.VerificationToken;
import xyz.erotskoob.expensetracker.exception.BadRequestException;
import xyz.erotskoob.expensetracker.repository.VerificationTokenRepository;
import xyz.erotskoob.expensetracker.service.TokenService;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

    private final VerificationTokenRepository tokenRepository;

    @Transactional
    public VerificationToken createToken(User user, TokenType tokenType) {
        tokenRepository.deleteByUserAndTokenType(user, tokenType);

        String tokenString = generateSecureToken();

        VerificationToken token = VerificationToken.builder()
                .token(tokenString)
                .user(user)
                .tokenType(tokenType)
                .createdDate(ZonedDateTime.now())
                .expiryDate(tokenType.getExpiryDate())
                .build();

        return tokenRepository.save(token);
    }

    @Override
    public VerificationToken validateToken(String tokenString, TokenType expectedType) {
        VerificationToken token = tokenRepository.findByToken(tokenString)
                .orElseThrow(() -> new BadRequestException("Invalid token"));

        if (token.getTokenType() != expectedType) {
            throw new BadRequestException(
                    "Invalid token type. Expected: " + expectedType +
                            ", Got: " + token.getTokenType()
            );
        }

        if (token.isUsed()) {
            throw new BadRequestException("Token already used");
        }

        if (token.isExpired()) {
            throw new BadRequestException("Token expired");
        }

        return token;
    }
    @Override
    public void markTokenAsUsed(VerificationToken token) {
        token.setUsed(true);
        token.setUsedDate(ZonedDateTime.now());
        tokenRepository.save(token);
    }

    private String generateSecureToken() {
        return UUID.randomUUID() + "-" + System.currentTimeMillis();
    }
}
