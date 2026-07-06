package Money.Manager.service;

import Money.Manager.dto.ExpenseDTO;
import Money.Manager.dto.IncomeDTO;
import Money.Manager.entity.CategoryEntity;
import Money.Manager.entity.ExpenseEntity;
import Money.Manager.entity.IncomeEntity;
import Money.Manager.entity.ProfileEntity;
import Money.Manager.repository.CategoryRepository;
import Money.Manager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private CategoryService categoryService;
    private IncomeRepository incomeRepository;
    private ProfileService profileService;
    private CategoryRepository categoryRepository;

    // Adds a new income to the database
    public IncomeDTO addIncome(IncomeDTO dto) {

        ProfileEntity profile = profileService.getCurrentProfile();

        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        IncomeEntity newIncome = toEntity(dto, profile, category);

        newIncome = incomeRepository.save(newIncome);

        return toDTO(newIncome);
    }


    private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity category){
        return IncomeEntity.builder()
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
    private IncomeDTO toDTO(IncomeEntity entity) {

        return IncomeDTO.builder()
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
