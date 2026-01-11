package xyz.erotskoob.expensetracker.service;

import org.springframework.http.ResponseEntity;
import xyz.erotskoob.expensetracker.dto.category.CreateCategoryRequest;

import java.util.UUID;

public interface CategoryService {
    ResponseEntity<?> createCategory(CreateCategoryRequest createCategoryRequest, UUID userId);
    ResponseEntity<?> getAllCategories(UUID userId);
    ResponseEntity<?> getCategoryById(UUID categoryId, UUID userId);
    ResponseEntity<?> deleteCategory(UUID categoryId, UUID userId);
    ResponseEntity<?> updateCategory(UUID categoryId, UUID userId, CreateCategoryRequest createCategoryRequest);
}
