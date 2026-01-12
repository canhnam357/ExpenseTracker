package xyz.erotskoob.expensetracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.erotskoob.expensetracker.dto.expense.CreateExpenseRequest;
import xyz.erotskoob.expensetracker.dto.expense.UpdateExpenseRequest;
import xyz.erotskoob.expensetracker.security.UserDetail;
import xyz.erotskoob.expensetracker.service.implementation.ExpenseService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getAllExpenses(@AuthenticationPrincipal UserDetail userDetail, @PathVariable UUID categoryId){
        UUID userId = userDetail.getUser().getId();
        return expenseService.getAllExpenses(userId, categoryId);
    }

    @PostMapping("/{categoryId}")
    public ResponseEntity<?> createExpense(@AuthenticationPrincipal UserDetail userDetail, @PathVariable UUID categoryId, @Valid @RequestBody CreateExpenseRequest createExpenseRequest){
        UUID userId = userDetail.getUser().getId();
        return expenseService.createExpense(userId, categoryId, createExpenseRequest);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<?> deleteExpense(@AuthenticationPrincipal UserDetail userDetail, @PathVariable UUID expenseId){
        UUID userId = userDetail.getUser().getId();
        return expenseService.deleteExpense(expenseId, userId);
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<?> updateExpense(@AuthenticationPrincipal UserDetail userDetail, @PathVariable UUID expenseId, @Valid @RequestBody UpdateExpenseRequest updateExpenseRequest){
        UUID userId = userDetail.getUser().getId();
        return expenseService.updateExpense(expenseId, userId, updateExpenseRequest);
    }
}
