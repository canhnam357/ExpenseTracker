package xyz.erotskoob.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.erotskoob.expensetracker.constant.TokenType;
import xyz.erotskoob.expensetracker.entity.auth.User;
import xyz.erotskoob.expensetracker.entity.auth.VerificationToken;

import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    Optional<VerificationToken> findByToken(String token);
    void deleteByUserAndTokenType(User user, TokenType tokenType);
}
