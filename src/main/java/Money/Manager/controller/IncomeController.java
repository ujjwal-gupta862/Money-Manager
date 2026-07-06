package Money.Manager.controller;

import Money.Manager.dto.ExpenseDTO;
import Money.Manager.dto.IncomeDTO;
import Money.Manager.service.ExpenseService;
import Money.Manager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/incomes")
@RequiredArgsConstructor
public class IncomeController {
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<IncomeDTO> addIncome(@RequestBody IncomeDTO dto) {
        // Call the service to add the income
        IncomeDTO createdIncome = incomeService.addIncome(dto);
        return ResponseEntity.ok(createdIncome);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getCurrentMonthIncomesForCurrentUser() {
        // Call the service to get current month incomes for the current user
        List<IncomeDTO> incomes = incomeService.getCurrentMonthIncomesForCurrentUser();
        return ResponseEntity.ok(incomes);
    }
    @DeleteMapping("/{incomeId}")
    public ResponseEntity<Void> deleteIncomeByIdForCurrentUser(@PathVariable Long incomeId) {
        incomeService.deleteIncomeByIdForCurrentUser(incomeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/latest5")
    public ResponseEntity<List<IncomeDTO>> getLatest5IncomesForCurrentUser() {
        List<IncomeDTO> incomes = incomeService.getLatest5IncomesForCurrentUser();
        return ResponseEntity.ok(incomes);
    }

    @GetMapping("/total")
    public ResponseEntity<BigDecimal> getTotalIncomeByCurrentUser() {
        BigDecimal total = incomeService.getTotalIncomeByCurrentUser();
        return ResponseEntity.ok(total);
    }
}
