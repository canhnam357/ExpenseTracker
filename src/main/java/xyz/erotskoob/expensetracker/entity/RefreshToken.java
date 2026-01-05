package xyz.erotskoob.expensetracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "refresh_tokens",
        indexes = {
                @Index(name = "idx_refresh_token_token", columnList = "token"),
                @Index(name = "idx_refresh_token_user", columnList = "user_id")
        }
)
public class RefreshToken {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(nullable = false)
    private boolean revoked = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
