package xyz.erotskoob.expensetracker.entity.auth;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.erotskoob.expensetracker.constant.TokenType;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "verification_tokens",
        indexes = {
                @Index(name = "idx_verification_tokens_token", columnList = "token"),
                @Index(name = "idx_verification_tokens_user_id", columnList = "user_id"),
                @Index(name = "idx_verification_tokens_type", columnList = "token_type")
        }
)
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false, length = 50)
    private TokenType tokenType;

    @Column(name = "created_date", nullable = false, updatable = false)
    private ZonedDateTime createdDate;

    @Column(name = "expiry_date", nullable = false)
    private ZonedDateTime expiryDate;

    @Column(name = "used_date")
    private ZonedDateTime usedDate;

    @Column(nullable = false)
    @Builder.Default
    private boolean used = false;

    @PrePersist
    protected void onCreate() { createdDate = ZonedDateTime.now(); }

    public boolean isExpired() {
        return ZonedDateTime.now().isAfter(expiryDate);
    }
}