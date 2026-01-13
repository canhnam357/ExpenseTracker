package xyz.erotskoob.expensetracker.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import xyz.erotskoob.expensetracker.entity.expense.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @Modifying
    @Query("UPDATE Category c SET c.isDeleted = true WHERE c.id = :id")
    void deleteCategory(UUID id);

    @Query("SELECT c FROM Category c WHERE c.id IN :listCategoryID AND c.isDeleted = false")
    List<Category> findListCategories(@Param("listCategoryID") List<UUID> listCategoryID);

    Page<Category> findAllByUserId(UUID userId, Pageable pageable);

    boolean existsByIdAndUserId(UUID userId, UUID categoryId);

    Optional<Category> findByUserIdAndId(UUID userId, UUID categoryId);
}
