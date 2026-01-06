package xyz.erotskoob.expensetracker.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import xyz.erotskoob.expensetracker.entity.RefreshToken;

import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    @Query(value = """
            UPDATE RefreshToken SET revoked = true WHERE user.id = :userId AND revoked = false
            """)
    @Modifying
    @Transactional
    void revokeRefreshToken(UUID userId);
}
