package xyz.erotskoob.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import xyz.erotskoob.expensetracker.entity.expense.Expense;

import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID>, JpaSpecificationExecutor<Expense> {

    List<Expense> findByUserIdAndCategoryId(UUID userId, UUID categoryId);

    boolean existsByUserIdAndId(UUID userId, UUID expenseId);
}
