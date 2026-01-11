package xyz.erotskoob.expensetracker.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import xyz.erotskoob.expensetracker.dto.category.CreateCategoryRequest;
import xyz.erotskoob.expensetracker.security.UserDetail;
import xyz.erotskoob.expensetracker.service.ICategoryService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;

    @PostMapping("")
    public ResponseEntity<?> createCategory(@Valid @RequestBody CreateCategoryRequest createCategoryRequest,
                                            @AuthenticationPrincipal UserDetail userDetail) {
        UUID userId = userDetail.getUser().getId();
        return categoryService.createCategory(createCategoryRequest, userId);
    }

    @GetMapping("")
    public ResponseEntity<?> getAllCategories(@AuthenticationPrincipal UserDetail userDetail) {
        UUID userId = userDetail.getUser().getId();
        return categoryService.getAllCategories(userId);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getCategoryById(@PathVariable UUID categoryId, @AuthenticationPrincipal UserDetail userDetail) {
        UUID userId = userDetail.getUser().getId();
        return categoryService.getCategoryById(categoryId, userId);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategory(@PathVariable UUID categoryId, @AuthenticationPrincipal UserDetail userDetail) {
        UUID userId = userDetail.getUser().getId();
        return categoryService.deleteCategory(categoryId, userId);
    }

    @PatchMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable UUID categoryId, @AuthenticationPrincipal UserDetail userDetail, @Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        UUID userId = userDetail.getUser().getId();
        return categoryService.updateCategory(categoryId, userId, createCategoryRequest);
    }
}
