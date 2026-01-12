package xyz.erotskoob.expensetracker.entity.expense;

import jakarta.persistence.*;
import lombok.*;
import xyz.erotskoob.expensetracker.entity.auth.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "expenses",
        indexes = {
                @Index(name = "idx_expenses_user_id", columnList = "user_id"),
                @Index(name = "idx_expenses_category_id", columnList = "category_id"),
                @Index(name = "idx_expenses_expense_date", columnList = "expense_date"),
                @Index(name = "idx_expenses_is_deleted", columnList = "is_deleted"),
                @Index(name = "idx_expenses_user_date", columnList = "user_id, expense_date"),
                @Index(name = "idx_expenses_user_category", columnList = "user_id, category_id")
        }
)
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "expense_date", nullable = false)
    private LocalDate expenseDate;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }
}