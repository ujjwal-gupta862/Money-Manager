package Money.Manager.service;

import Money.Manager.dto.ExpenseDTO;
import Money.Manager.dto.IncomeDTO;
import Money.Manager.entity.CategoryEntity;
import Money.Manager.entity.ExpenseEntity;
import Money.Manager.entity.IncomeEntity;
import Money.Manager.entity.ProfileEntity;
import Money.Manager.repository.CategoryRepository;
import Money.Manager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final ExpenseRepository expenseRepository;
    private final ProfileService profileService;

    // Adds a new expense to the database
    public ExpenseDTO addExpense(ExpenseDTO dto) {

        ProfileEntity profile = profileService.getCurrentProfile();

        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ExpenseEntity newExpense = toEntity(dto, profile, category);

        newExpense = expenseRepository.save(newExpense);

        return toDTO(newExpense);
    }

    //get current month expenses
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<ExpenseEntity> expenses = expenseRepository.findByProfileIdAndDateBetween(
                profile.getId(),
                startDate,
                endDate
        );
        return expenses.stream().map(this::toDTO).toList();
    }

    //delete expense by id for current user
    public void deleteExpenseByIdForCurrentUser(Long expenseId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        ExpenseEntity entity = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if(entity.getProfile().getId().equals(profile.getId())) {
            expenseRepository.deleteById(expenseId);
        } else {
            throw new RuntimeException("Expense does not belong to the current user");
        }
//        expenseRepository.deleteByIdAndProfileId(expenseId, profile.getId());
    }

    //get latest 5 expenses
    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> expenses = expenseRepository.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return expenses.stream().map(this::toDTO).toList();
    }

    public BigDecimal getTotalExpenseByCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal total = expenseRepository.findTotalExpenseByProfileId(profile.getId());
        return total != null ? total : BigDecimal.ZERO;
    }

    private ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile, CategoryEntity category){
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .date(dto.getDate())
                .amount(dto.getAmount())
                .category(category)
                .profile(profile)
                .build();
    }

    private ExpenseDTO toDTO(ExpenseEntity entity) {

        return ExpenseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
                .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A")
                .amount(entity.getAmount())
                .date(entity.getDate())
                .build();
    }
}
