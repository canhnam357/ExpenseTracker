package xyz.erotskoob.expensetracker.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import xyz.erotskoob.expensetracker.dto.GeneralResponse;
import xyz.erotskoob.expensetracker.dto.category.CreateCategoryRequest;
import xyz.erotskoob.expensetracker.dto.category.CategoryResponse;
import xyz.erotskoob.expensetracker.dto.category.UpdateCategoryRequest;
import xyz.erotskoob.expensetracker.entity.expense.Category;
import xyz.erotskoob.expensetracker.entity.auth.User;
import xyz.erotskoob.expensetracker.exception.AuthenticationException;
import xyz.erotskoob.expensetracker.exception.ResourceNotFoundException;
import xyz.erotskoob.expensetracker.repository.CategoryRepository;
import xyz.erotskoob.expensetracker.repository.UserRepository;
import xyz.erotskoob.expensetracker.service.ICategoryService;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResponseEntity<?> createCategory(UUID userId, CreateCategoryRequest createCategoryRequest) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AuthenticationException("User not found"));
        Category category = Category.builder()
                .name(createCategoryRequest.name())
                .user(user)
                .hexColorCode(createCategoryRequest.hexColorCode())
                .build();
        categoryRepository.save(category);

        CategoryResponse categoryResponse = new CategoryResponse(category);

        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Category created successfully", 201, categoryResponse);

        return ResponseEntity.status(201).body(res);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<?> searchCategories(UUID userId, Pageable pageable) {
        Page<Category> categoryList = categoryRepository.findAllByUserId(userId, pageable);
        Page<CategoryResponse> responseList = categoryList.map(CategoryResponse::new);
        GeneralResponse<Page<CategoryResponse>> res = new GeneralResponse<>(Instant.now(), "Categories fetched successfully", 200, responseList);
        return ResponseEntity.ok().body(res);
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteCategory(UUID userId, UUID categoryId) {
        if (!categoryRepository.existsByIdAndUserId(userId, categoryId)) {
            throw new ResourceNotFoundException("Category not found");
        }

        categoryRepository.deleteCategory(categoryId);
        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Category deleted successfully", 204, null);
        return ResponseEntity.status(204).body(res);
    }

    @Override
    @Transactional
    public ResponseEntity<?> updateCategory(UUID userId, UUID categoryId, UpdateCategoryRequest updateCategoryRequest) {
        Category category = categoryRepository.findByUserIdAndId(userId, categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setName(updateCategoryRequest.name());
        category.setHexColorCode(updateCategoryRequest.hexColorCode());
        categoryRepository.save(category);

        CategoryResponse categoryResponse = new CategoryResponse(category);
        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Category updated successfully", 200, categoryResponse);
        return ResponseEntity.ok().body(res);
    }
}
