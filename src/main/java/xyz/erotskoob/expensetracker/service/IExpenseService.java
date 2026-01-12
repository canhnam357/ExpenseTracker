package xyz.erotskoob.expensetracker.service;

import org.springframework.http.ResponseEntity;
import xyz.erotskoob.expensetracker.dto.expense.CreateExpenseRequest;
import xyz.erotskoob.expensetracker.dto.expense.UpdateExpenseRequest;

import java.util.UUID;

public interface IExpenseService {

    ResponseEntity<?> getAllExpenses(UUID userId, UUID categoryId);
    ResponseEntity<?> deleteExpense(UUID expenseId, UUID userId);
    ResponseEntity<?> updateExpense(UUID expenseId, UUID userId, UpdateExpenseRequest updateExpenseRequest);
    ResponseEntity<?> createExpense(UUID userId, UUID categoryId, CreateExpenseRequest createExpenseRequest);
}
