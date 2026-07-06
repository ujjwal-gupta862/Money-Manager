package Money.Manager.controller;

import Money.Manager.dto.ExpenseDTO;
import Money.Manager.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expenses")
@RequiredArgsConstructor
public class ExpenseController {

    private final ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ExpenseDTO> addExpense(@RequestBody ExpenseDTO dto) {
        // Call the service to add the expense
        ExpenseDTO createdExpense = expenseService.addExpense(dto);
        return ResponseEntity.ok(createdExpense);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getCurrentMonthExpensesForCurrentUser() {
        // Call the service to get current month expenses for the current user
        List<ExpenseDTO> expenses = expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.ok(expenses);
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<Void> deleteExpenseByIdForCurrentUser(@PathVariable Long expenseId) {
        expenseService.deleteExpenseByIdForCurrentUser(expenseId);
        return ResponseEntity.noContent().build();
    }
}
