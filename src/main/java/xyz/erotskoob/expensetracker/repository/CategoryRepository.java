package xyz.erotskoob.expensetracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import xyz.erotskoob.expensetracker.entity.expense.Category;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    @Modifying
    @Query("UPDATE Category c SET c.isDeleted = true WHERE c.id = :id")
    void deleteCategory(UUID id);
}
