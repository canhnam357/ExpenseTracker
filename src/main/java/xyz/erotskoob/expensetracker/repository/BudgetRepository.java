package xyz.erotskoob.expensetracker.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import xyz.erotskoob.expensetracker.entity.expense.Budget;

import java.util.List;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {
    Page<Budget> findByUserId(UUID userId, Pageable pageable);

    boolean existsByUserIdAndId(UUID userId, UUID budgetId);
}
