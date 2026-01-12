package xyz.erotskoob.expensetracker.entity.expense;

import jakarta.persistence.*;
import lombok.*;
import xyz.erotskoob.expensetracker.entity.auth.User;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "name"})
        },
        indexes = {
                @Index(name = "idx_categories_user_id", columnList = "user_id"),
                @Index(name = "idx_categories_is_deleted", columnList = "is_deleted")
        }
)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany(mappedBy = "categories")
    private List<Budget> budgets = new ArrayList<>();

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private String hexColorCode;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<Expense> expenses = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }

    // Business methods
    @Transient
    public BigDecimal getTotalAmount() {
        return new ArrayList<>(expenses).stream()
                .filter(exp -> !exp.isDeleted())
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}