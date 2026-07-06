package Money.Manager.service;

import Money.Manager.dto.ExpenseDTO;
import Money.Manager.entity.CategoryEntity;
import Money.Manager.entity.ExpenseEntity;
import Money.Manager.entity.ProfileEntity;
import Money.Manager.repository.CategoryRepository;
import Money.Manager.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseService {
    private CategoryService categoryService;
    private CategoryRepository categoryRepository;
    private ExpenseRepository expenseRepository;
    private ProfileService profileService;

    // Adds a new expense to the database
    public ExpenseDTO addExpense(ExpenseDTO dto) {

        ProfileEntity profile = profileService.getCurrentProfile();

        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        ExpenseEntity newExpense = toEntity(dto, profile, category);

        newExpense = expenseRepository.save(newExpense);

        return toDTO(newExpense);
    }

    private ExpenseEntity toEntity(ExpenseDTO dto, ProfileEntity profile, CategoryEntity category){
        return ExpenseEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .icon(dto.getIcon())
                .date(dto.getDate())
                .amount(dto.getAmount())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
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
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
