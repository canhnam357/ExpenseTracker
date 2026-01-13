package xyz.erotskoob.expensetracker.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import xyz.erotskoob.expensetracker.dto.expense.CreateExpenseRequest;
import xyz.erotskoob.expensetracker.dto.expense.UpdateExpenseRequest;

import java.time.LocalDate;
import java.util.UUID;

public interface IExpenseService {

    ResponseEntity<?> searchExpenses(UUID userId, UUID categoryId, Boolean deleted, LocalDate startDate, LocalDate endDate, Pageable pageable);
    ResponseEntity<?> deleteExpense(UUID expenseId, UUID userId);
    ResponseEntity<?> updateExpense(UUID expenseId, UUID userId, UpdateExpenseRequest updateExpenseRequest);
    ResponseEntity<?> createExpense(UUID userId, UUID categoryId, CreateExpenseRequest createExpenseRequest);
}
