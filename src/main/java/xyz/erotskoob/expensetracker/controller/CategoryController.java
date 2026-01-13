package xyz.erotskoob.expensetracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.erotskoob.expensetracker.dto.category.CreateCategoryRequest;
import xyz.erotskoob.expensetracker.security.UserDetail;
import xyz.erotskoob.expensetracker.service.ICategoryService;
import xyz.erotskoob.expensetracker.service.IExpenseService;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;
    private final IExpenseService expenseService;

    @PostMapping("")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest,
                                            @AuthenticationPrincipal UserDetail userDetail) {
        UUID userId = userDetail.getUser().getId();
        return categoryService.createCategory(userId, createCategoryRequest);
    }

    @GetMapping("")
    public ResponseEntity<?> getAllCategories(@AuthenticationPrincipal UserDetail userDetail, Pageable pageable) {
        UUID userId = userDetail.getUser().getId();
        return categoryService.searchCategories(userId, pageable);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable UUID categoryId, @AuthenticationPrincipal UserDetail userDetail) {
        UUID userId = userDetail.getUser().getId();
        return categoryService.deleteCategory(userId, categoryId);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable UUID categoryId, @Valid @RequestBody CreateCategoryRequest createCategoryRequest, @AuthenticationPrincipal UserDetail userDetail) {
        UUID userId = userDetail.getUser().getId();
        return categoryService.updateCategory(userId, categoryId, createCategoryRequest);
    }

    @GetMapping("/{categoryId}/expenses")
    public ResponseEntity<?> searchExpenses(@AuthenticationPrincipal UserDetail userDetail,
                                            @PathVariable UUID categoryId,
                                            @RequestParam(required = false) Boolean deleted,
                                            @RequestParam(required = false) LocalDate startDate,
                                            @RequestParam(required = false) LocalDate endDate,
                                            Pageable pageable){
        UUID userId = userDetail.getUser().getId();
        return expenseService.searchExpenses(userId, categoryId, deleted, startDate, endDate, pageable);
    }
}
