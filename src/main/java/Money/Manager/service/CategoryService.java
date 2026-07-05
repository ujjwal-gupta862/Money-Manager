package Money.Manager.service;

import Money.Manager.dto.CategoryDTO;
import Money.Manager.dto.ProfileDTO;
import Money.Manager.entity.CategoryEntity;
import Money.Manager.entity.ProfileEntity;
import Money.Manager.repository.CategoryRepository;
import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProfileService profileService;

    //save category
    public CategoryDTO saveCategory(CategoryDTO categoryDTO){
        ProfileEntity profile = profileService.getCurrentProfile();
        if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category with this name already exists");
        }
        CategoryEntity newCategory = toEntity(categoryDTO, profile);

        newCategory = categoryRepository.save(newCategory);
        return toDto(newCategory);
    }

    //get category for current user
    public List<CategoryDTO> getCategoryForCurrentUser(Long categoryId) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDto).toList();
    }

    public CategoryEntity toEntity(CategoryDTO dto, ProfileEntity profile) {
//        ProfileEntity profile = profileService.getCurrentProfile();
        return CategoryEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .type(dto.getType())
                .profile(profile)
                .build();
    }

    public CategoryDTO toDto(CategoryEntity entity) {
        return CategoryDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .icon(entity.getIcon())
                .type(entity.getType())
                .profileId(entity.getProfile() != null ? entity.getProfile().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    //get categories by type for current user
    public List<CategoryDTO> getCategoryByTypeForCurrentUser(String type) {
        ProfileEntity profile = profileService.getCurrentProfile();
        Optional<CategoryEntity> categories = categoryRepository.findByTypeAndProfileId(type, profile.getId());
        return categories.stream().map(this::toDto).toList();
    }

    //update category
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO dto){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity existingCategory = categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        existingCategory.setName(dto.getName());
        existingCategory.setIcon(dto.getIcon());
        existingCategory.setType(dto.getType());
        existingCategory = categoryRepository.save(existingCategory);
        return toDto(existingCategory);
    }
}
