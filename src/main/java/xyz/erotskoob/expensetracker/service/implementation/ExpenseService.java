package xyz.erotskoob.expensetracker.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.erotskoob.expensetracker.dto.GeneralResponse;
import xyz.erotskoob.expensetracker.dto.expense.CreateExpenseRequest;
import xyz.erotskoob.expensetracker.dto.expense.ExpenseResponse;
import xyz.erotskoob.expensetracker.dto.expense.UpdateExpenseRequest;
import xyz.erotskoob.expensetracker.entity.auth.User;
import xyz.erotskoob.expensetracker.entity.expense.Category;
import xyz.erotskoob.expensetracker.entity.expense.Expense;
import xyz.erotskoob.expensetracker.exception.ResourceNotFoundException;
import xyz.erotskoob.expensetracker.repository.CategoryRepository;
import xyz.erotskoob.expensetracker.repository.ExpenseRepository;
import xyz.erotskoob.expensetracker.repository.UserRepository;
import xyz.erotskoob.expensetracker.service.IExpenseService;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpenseService implements IExpenseService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ExpenseRepository expenseRepository;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllExpenses(UUID userId, UUID categoryId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found");
        }
        List<Expense> expenseList = expenseRepository.findByUserIdAndCategoryId(userId, categoryId);
        List<ExpenseResponse> expenseResponseList = expenseList.stream().map(ExpenseResponse::new).toList();
        GeneralResponse<List<ExpenseResponse>> res = new GeneralResponse<>(Instant.now(), "Expenses fetched successfully", 200, expenseResponseList);
        return ResponseEntity.ok().body(res);
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteExpense(UUID expenseId, UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        expense.setDeleted(true);
        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Expense deleted successfully", 204, null);
        return ResponseEntity.status(204).body(res);
    }

    @Override
    public ResponseEntity<?> updateExpense(UUID expenseId, UUID userId, UpdateExpenseRequest updateExpenseRequest) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found");
        }
        Expense expense = expenseRepository.findById(expenseId).orElseThrow(() -> new ResourceNotFoundException("Expense not found"));
        expense.setDescription(updateExpenseRequest.description());
        expense.setAmount(updateExpenseRequest.amount());
        expense.setExpenseDate(updateExpenseRequest.expenseDate());
        expenseRepository.save(expense);

        ExpenseResponse expenseResponse = new ExpenseResponse(expense);
        GeneralResponse<ExpenseResponse> res = new GeneralResponse<>(Instant.now(), "Expense updated successfully", 200, expenseResponse);
        return ResponseEntity.ok().body(res);
    }

    @Override
    @Transactional
    public ResponseEntity<?> createExpense(UUID userId, UUID categoryId, CreateExpenseRequest createExpenseRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        Expense expense = Expense.builder()
                .amount(createExpenseRequest.amount())
                .category(category)
                .user(user)
                .description(createExpenseRequest.description())
                .expenseDate(createExpenseRequest.expenseDate())
                .build();
        expenseRepository.save(expense);
        ExpenseResponse expenseResponse = new ExpenseResponse(expense);
        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Expense created successfully", 201, expenseResponse);
        return ResponseEntity.status(201).body(res);
    }
}
