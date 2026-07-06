package Money.Manager.service;

import Money.Manager.dto.IncomeDTO;
import Money.Manager.entity.CategoryEntity;
import Money.Manager.entity.ExpenseEntity;
import Money.Manager.entity.IncomeEntity;
import Money.Manager.entity.ProfileEntity;
import Money.Manager.repository.CategoryRepository;
import Money.Manager.repository.IncomeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {
    private final CategoryService categoryService;
    private final IncomeRepository incomeRepository;
    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    // Adds a new income to the database
    public IncomeDTO addIncome(IncomeDTO dto) {

        ProfileEntity profile = profileService.getCurrentProfile();

        CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        IncomeEntity newIncome = toEntity(dto, profile, category);

        newIncome = incomeRepository.save(newIncome);

        return toDTO(newIncome);
    }

    //delete income by id for current user
    public void deleteIncomeByIdForCurrentUser(Long incomeId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity entity = incomeRepository.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found"));
        if(entity.getProfile().getId().equals(profile.getId())) {
            incomeRepository.deleteById(incomeId);
        } else {
            throw new RuntimeException("Income does not belong to the current user");
        }
//        incomeRepository.deleteByIdAndProfileId(incomeId, profile.getId());
    }

    //get current month incomes
    public List<IncomeDTO> getCurrentMonthIncomesForCurrentUser() {
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.withDayOfMonth(1);
        LocalDate endDate = now.withDayOfMonth(now.lengthOfMonth());
        List<IncomeEntity> incomes = incomeRepository.findByProfileIdAndDateBetween(
                profile.getId(),
                startDate,
                endDate
        );
        return incomes.stream().map(this::toDTO).toList();
    }

    private IncomeEntity toEntity(IncomeDTO dto, ProfileEntity profile, CategoryEntity category){
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .date(dto.getDate())
                .amount(dto.getAmount())
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
