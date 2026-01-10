package xyz.erotskoob.expensetracker.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.erotskoob.expensetracker.entity.RefreshToken;

import java.time.ZonedDateTime;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    @Query(value = "UPDATE RefreshToken SET revoked = true, revokedDate = :now WHERE user.id = :userId AND revoked = false")
    @Modifying
    @Transactional
    void revokeRefreshToken(@Param("userId") UUID userId,@Param("now") ZonedDateTime now);
}
