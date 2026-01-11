package xyz.erotskoob.expensetracker.entity.expense;

import jakarta.persistence.*;
import lombok.*;
import xyz.erotskoob.expensetracker.entity.auth.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "name", "budget_id"})
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

    @Column(name = "allocated_amount", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal allocatedAmount = BigDecimal.ZERO;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Expense> expenses = new ArrayList<>();

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
    public BigDecimal getTotalSpent() {
        return expenses.stream()
                .filter(exp -> !exp.isDeleted())
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transient
    public BigDecimal getRemaining() {
        if (allocatedAmount == null) {
            return null;
        }
        return allocatedAmount.subtract(getTotalSpent());
    }

    @Transient
    public Double getSpentPercentage() {
        if (allocatedAmount == null || allocatedAmount.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return getTotalSpent()
                .divide(allocatedAmount, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    @Transient
    public boolean isOverBudget() {
        if (allocatedAmount == null) {
            return false;
        }
        return getTotalSpent().compareTo(allocatedAmount) > 0;
    }
}