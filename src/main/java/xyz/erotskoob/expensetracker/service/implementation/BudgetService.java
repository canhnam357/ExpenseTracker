package xyz.erotskoob.expensetracker.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.erotskoob.expensetracker.dto.GeneralResponse;
import xyz.erotskoob.expensetracker.dto.budget.BudgetResponse;
import xyz.erotskoob.expensetracker.dto.budget.CreateBudgetRequest;
import xyz.erotskoob.expensetracker.dto.budget.UpdateBudgetRequest;
import xyz.erotskoob.expensetracker.entity.auth.User;
import xyz.erotskoob.expensetracker.entity.expense.Budget;
import xyz.erotskoob.expensetracker.entity.expense.Category;
import xyz.erotskoob.expensetracker.exception.AuthenticationException;
import xyz.erotskoob.expensetracker.exception.BadRequestException;
import xyz.erotskoob.expensetracker.exception.ResourceNotFoundException;
import xyz.erotskoob.expensetracker.repository.BudgetRepository;
import xyz.erotskoob.expensetracker.repository.CategoryRepository;
import xyz.erotskoob.expensetracker.repository.UserRepository;
import xyz.erotskoob.expensetracker.service.IBudgetService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BudgetService implements IBudgetService {

    private final UserRepository userRepository;
    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public ResponseEntity<?> createBudget(CreateBudgetRequest createBudgetRequest, UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AuthenticationException("User not found"));

        if (createBudgetRequest.startDate().isAfter(createBudgetRequest.endDate())) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        List<Category> categories = categoryRepository.findListCategories(createBudgetRequest.categoryIds());

        Budget budget = Budget.builder()
                .name(createBudgetRequest.name())
                .user(user)
                .totalAmount(createBudgetRequest.amount())
                .startDate(createBudgetRequest.startDate())
                .endDate(createBudgetRequest.endDate())
                .build();

        for (Category category : categories) {
            budget.getCategories().add(category);
        }

        for (Category category : categories) {
            category.getBudgets().add(budget);
        }

        budgetRepository.save(budget);

        BudgetResponse budgetResponse = new BudgetResponse(budget);

        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Budget created successfully", 201, budgetResponse);

        return ResponseEntity.status(201).body(res);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllBudgets(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new AuthenticationException("User not found");
        }
        List<Budget> budgetList = budgetRepository.findByUserId(userId);
        List<BudgetResponse> responseList = budgetList.stream().map(BudgetResponse::new).toList();
        GeneralResponse<List<BudgetResponse>> res = new GeneralResponse<>(Instant.now(), "Budgets fetched successfully", 200, responseList);
        return ResponseEntity.ok().body(res);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> getBudgetById(UUID budgetId, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new AuthenticationException("User not found");
        }
        Budget budget = budgetRepository.findById(budgetId).orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        BudgetResponse budgetResponse = new BudgetResponse(budget);
        GeneralResponse<BudgetResponse> res = new GeneralResponse<>(Instant.now(), "Budget fetched successfully", 200, budgetResponse);
        return ResponseEntity.ok().body(res);
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteBudget(UUID budgetId, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new AuthenticationException("User not found");
        }
        Budget budget = budgetRepository.findById(budgetId).orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        budget.setDeleted(true);
        budgetRepository.save(budget);
        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Budget deleted successfully", 204, null);
        return ResponseEntity.status(204).body(res);
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateBudget(UUID budgetId, UUID userId, UpdateBudgetRequest updateBudgetRequest) {
        if (!userRepository.existsById(userId)) {
            throw new AuthenticationException("User not found");
        }
        if (updateBudgetRequest.startDate().isAfter(updateBudgetRequest.endDate())) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        Budget budget = budgetRepository.findById(budgetId).orElseThrow(() -> new ResourceNotFoundException("Budget not found"));
        budget.setName(updateBudgetRequest.name());
        budget.setTotalAmount(updateBudgetRequest.amount());
        budget.setStartDate(updateBudgetRequest.startDate());
        budget.setEndDate(updateBudgetRequest.endDate());

        List<Category> categories = categoryRepository.findListCategories(updateBudgetRequest.categoryIds());
        for (Category category : budget.getCategories()) {
            category.getBudgets().remove(budget);
        }
        budget.getCategories().clear();
        for (Category category : categories) {
            budget.getCategories().add(category);
            category.getBudgets().add(budget);
        }

        budgetRepository.save(budget);
        BudgetResponse budgetResponse = new BudgetResponse(budget);
        GeneralResponse<BudgetResponse> res = new GeneralResponse<>(Instant.now(), "Budget updated successfully", 200, budgetResponse);
        return ResponseEntity.ok().body(res);
    }
}
