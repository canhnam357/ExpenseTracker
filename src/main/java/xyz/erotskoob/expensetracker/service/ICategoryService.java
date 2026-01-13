package xyz.erotskoob.expensetracker.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import xyz.erotskoob.expensetracker.dto.category.CreateCategoryRequest;

import java.util.UUID;

public interface ICategoryService {
    ResponseEntity<?> createCategory(UUID userId, CreateCategoryRequest createCategoryRequest);
    ResponseEntity<?> searchCategories(UUID userId, Pageable pageable);
    ResponseEntity<?> deleteCategory(UUID userId, UUID categoryId);
    ResponseEntity<?> updateCategory(UUID userId, UUID categoryId, CreateCategoryRequest createCategoryRequest);
}
