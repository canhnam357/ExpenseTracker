package xyz.erotskoob.expensetracker.entity.expense;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.erotskoob.expensetracker.entity.auth.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
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
        name = "budgets",
        indexes = {
                @Index(name = "idx_budgets_user_id", columnList = "user_id"),
                @Index(name = "idx_budgets_is_deleted", columnList = "is_deleted"),
                @Index(name = "idx_budgets_period", columnList = "start_date, end_date")
        }
)
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private boolean isDeleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = ZonedDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = ZonedDateTime.now();
    }

    @Transient
    public BigDecimal getTotalSpent() {
        return categories.stream()
                .flatMap(cat -> cat.getExpenses().stream())
                .filter(exp -> !exp.isDeleted())
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Transient
    public BigDecimal getRemaining() {
        return totalAmount.subtract(getTotalSpent());
    }

    @Transient
    public double getSpentPercentage() {
        if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
            return 0.0;
        }
        return getTotalSpent()
                .divide(totalAmount, 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    @Transient
    public boolean isActive() {
        LocalDate now = LocalDate.now();
        return !now.isBefore(startDate) && !now.isAfter(endDate);
    }
}