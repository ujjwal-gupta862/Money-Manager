package Money.Manager.controller;

import Money.Manager.dto.CategoryDTO;
import Money.Manager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategory = categoryService.saveCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }

    //read categories
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getCategoriesForCurrentUser() {
        List<CategoryDTO> categories = categoryService.getCategoryForCurrentUser(null);
        return ResponseEntity.ok(categories);
    }

    //get categories by type for current user
    @GetMapping("{type}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByTypeForCurrentUser(@PathVariable String type) {
        List<CategoryDTO> categories = categoryService.getCategoryByTypeForCurrentUser(type);
        return ResponseEntity.ok(categories);
    }

    //update category
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long categoryId,  @RequestBody CategoryDTO dto) {
        CategoryDTO updatedCategoryDto = categoryService.updateCategory(categoryId, dto);
        return ResponseEntity.ok(updatedCategoryDto);
    }
}
