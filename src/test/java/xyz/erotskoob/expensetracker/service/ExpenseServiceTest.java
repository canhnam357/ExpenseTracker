package xyz.erotskoob.expensetracker.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import xyz.erotskoob.expensetracker.entity.expense.Expense;
import xyz.erotskoob.expensetracker.repository.ExpenseRepository;
import xyz.erotskoob.expensetracker.service.implementation.ExpenseService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    ExpenseRepository expenseRepository;

    @InjectMocks
    ExpenseService expenseService;
//
//    @Test
//    void createExpense() {
//
//        Expense e = new Expense();
//
//        when(expenseRepository.save(any()))
//                .thenReturn(e);
//
//        Expense result = expenseService.createExpense(e);
//
//        verify(expenseRepository).save(e);
//    }
}
