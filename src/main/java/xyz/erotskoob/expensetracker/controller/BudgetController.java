package xyz.erotskoob.expensetracker.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.erotskoob.expensetracker.dto.budget.CreateBudgetRequest;
import xyz.erotskoob.expensetracker.dto.budget.UpdateBudgetRequest;
import xyz.erotskoob.expensetracker.security.UserDetail;
import xyz.erotskoob.expensetracker.service.IBudgetService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {
    private final IBudgetService budgetService;

    @PostMapping("")
    public ResponseEntity<?> createBudget(@Valid @RequestBody CreateBudgetRequest createBudgetRequest, @AuthenticationPrincipal UserDetail userDetail){
        UUID userId = userDetail.getUser().getId();
        return budgetService.createBudget(createBudgetRequest, userId);
    }

    @GetMapping("")
    public ResponseEntity<?> searchBudgets(@AuthenticationPrincipal UserDetail userDetail, Pageable pageable){
        UUID userId = userDetail.getUser().getId();
        return budgetService.searchBudgets(userId, pageable);
    }

    @DeleteMapping("/{budgetId}")
    public ResponseEntity<?> deleteBudget(@PathVariable UUID budgetId, @AuthenticationPrincipal UserDetail userDetail){
        UUID userId = userDetail.getUser().getId();
        return budgetService.deleteBudget(budgetId, userId);
    }

    @PutMapping("/{budgetId}")
    public ResponseEntity<?> updateBudget(@PathVariable UUID budgetId, @AuthenticationPrincipal UserDetail userDetail, @Valid @RequestBody UpdateBudgetRequest updateBudgetRequest){
        UUID userId = userDetail.getUser().getId();
        return budgetService.updateBudget(budgetId, userId, updateBudgetRequest);
    }


}
