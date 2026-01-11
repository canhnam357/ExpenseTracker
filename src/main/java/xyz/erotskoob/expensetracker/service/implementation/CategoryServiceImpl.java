package xyz.erotskoob.expensetracker.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import xyz.erotskoob.expensetracker.dto.GeneralResponse;
import xyz.erotskoob.expensetracker.dto.category.CreateCategoryRequest;
import xyz.erotskoob.expensetracker.dto.category.CategoryResponse;
import xyz.erotskoob.expensetracker.entity.expense.Category;
import xyz.erotskoob.expensetracker.entity.auth.User;
import xyz.erotskoob.expensetracker.exception.ResourceNotFoundException;
import xyz.erotskoob.expensetracker.repository.CategoryRepository;
import xyz.erotskoob.expensetracker.repository.UserRepository;
import xyz.erotskoob.expensetracker.service.CategoryService;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResponseEntity<?> createCategory(CreateCategoryRequest createCategoryRequest, UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        Category category = Category.builder()
                .name(createCategoryRequest.name())
                .user(user.get())
                .build();
        categoryRepository.save(category);

        CategoryResponse categoryResponse = new CategoryResponse(category);

        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Category created successfully", 201, categoryResponse);

        return ResponseEntity.status(201).body(res);
    }

    @Override
    public ResponseEntity<?> getAllCategories(UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        List<Category> categoryList = categoryRepository.findAll();

        List<CategoryResponse> responseList = categoryList.stream().map(CategoryResponse::new).toList();

        GeneralResponse<List<CategoryResponse>> res = new GeneralResponse<>(Instant.now(), "Categories fetched successfully", 200, responseList);
        return ResponseEntity.ok().body(res);
    }

    @Override
    public ResponseEntity<?> getCategoryById(UUID categoryId, UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        GeneralResponse<CategoryResponse> res = new GeneralResponse<>(Instant.now(), "Category fetched successfully", 200, new CategoryResponse(category));

        return ResponseEntity.ok().body(res);
    }

    @Override
    @Transactional
    public ResponseEntity<?> deleteCategory(UUID categoryId, UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }

        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found");
        }

        categoryRepository.deleteCategory(categoryId);
        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Category deleted successfully", 204, null);
        return ResponseEntity.status(204).body(res);
    }

    @Override
    public ResponseEntity<?> updateCategory(UUID categoryId, UUID userId, CreateCategoryRequest createCategoryRequest) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found");
        }
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        category.setName(createCategoryRequest.name());
        categoryRepository.save(category);

        CategoryResponse categoryResponse = new CategoryResponse(category);

        GeneralResponse<Object> res = new GeneralResponse<>(Instant.now(), "Category updated successfully", 200, categoryResponse);

        return ResponseEntity.ok().body(res);
    }
}
