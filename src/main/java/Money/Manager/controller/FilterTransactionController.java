package Money.Manager.controller;

import Money.Manager.dto.ExpenseDTO;
import Money.Manager.dto.FilterDTO;
import Money.Manager.dto.IncomeDTO;
import Money.Manager.service.EmailService;
import Money.Manager.service.ExpenseService;
import Money.Manager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/filter")
@RequiredArgsConstructor
public class FilterTransactionController {

    private ExpenseService expenseService;
    private IncomeService incomeService;

    @GetMapping
    public ResponseEntity<?> filterTransaction(@RequestBody FilterDTO filter){
        LocalDate startDate = filter.getStartDate() != null ? filter.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filter.getEndDate() != null ? filter.getEndDate() :LocalDate.MAX;
        String keyword = filter.getKeyword() != null ? filter.getKeyword() : "";
        String sortField = filter.getSortField() !=null ? filter.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filter.getType()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);
        // Implementation for filtering transactions
        if ("income".equals(filter.getType())) {

            List<IncomeDTO> incomes =
                    incomeService.filterIncomes(startDate, endDate, keyword, sort);

            return ResponseEntity.ok(incomes);

        } else if ("expense".equalsIgnoreCase(filter.getType())) {

            List<ExpenseDTO> expenses =
                    expenseService.filterExpenses(startDate, endDate, keyword, sort);

            return ResponseEntity.ok(expenses);

        } else {

            return ResponseEntity.badRequest()
                    .body("Invalid type. Must be 'income' or 'expense'");
        }
    }

}
