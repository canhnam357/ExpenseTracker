package xyz.erotskoob.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import xyz.erotskoob.expensetracker.entity.expense.Expense;

import java.util.List;
import java.util.UUID;

public interface ExpenseRepository extends JpaRepository<Expense, UUID > {

    List<Expense> findByUserIdAndCategoryId(UUID userId, UUID categoryId);

}
