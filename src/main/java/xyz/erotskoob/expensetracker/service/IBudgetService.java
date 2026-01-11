package xyz.erotskoob.expensetracker.service;

import org.springframework.http.ResponseEntity;
import xyz.erotskoob.expensetracker.dto.budget.CreateBudgetRequest;
import xyz.erotskoob.expensetracker.dto.budget.UpdateBudgetRequest;

import java.util.UUID;

public interface IBudgetService {
    ResponseEntity<?> createBudget(CreateBudgetRequest createBudgetRequest, UUID userId);
    ResponseEntity<?> getAllBudgets(UUID userId);
    ResponseEntity<?> getBudgetById(UUID budgetId, UUID userId);
    ResponseEntity<?> deleteBudget(UUID budgetId, UUID userId);
    ResponseEntity<?> updateBudget(UUID budgetId, UUID userId, UpdateBudgetRequest updateBudgetRequest);
}
