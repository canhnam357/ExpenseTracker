package xyz.erotskoob.expensetracker.specification;

import org.springframework.data.jpa.domain.Specification;
import xyz.erotskoob.expensetracker.entity.expense.Expense;

import java.time.LocalDate;
import java.util.UUID;

public class ExpenseSpecification {
    public static Specification<Expense> hasUserId(UUID userId){
        if (userId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("user").get("id"), userId);
    }
    public static Specification<Expense> hasCategoryId(UUID categoryId) {
        if (categoryId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId);
    }
    public static Specification<Expense> deleted (Boolean deleted) {
        if (deleted == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("isDeleted"), deleted);
    }
    public static Specification<Expense> betweenDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }
        if (startDate == null) {
            return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("expenseDate"), endDate);
        }
        if (endDate == null) {
            return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("expenseDate"), startDate);
        }
        return (root, query, cb) -> cb.between(root.get("expenseDate"), startDate, endDate);
    }
}
