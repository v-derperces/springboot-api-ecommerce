package fr.afpa.pompey.APIEcommerce.controller;

import fr.afpa.pompey.APIEcommerce.exceptionhandler.CustomHttpException;
import fr.afpa.pompey.APIEcommerce.model.Category;
import fr.afpa.pompey.APIEcommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/category")
    public Category createCategory(@Valid @RequestBody Category category) throws CustomHttpException {
        return categoryService.saveCategory(category);
    }

    @GetMapping("/categories")
    public Iterable<Category> getCategories() {
        return categoryService.getCategories();
    }

    @GetMapping("/category/{id}")
    public Category getCategory(@PathVariable("id") Long id) {
        Optional<Category> category = categoryService.getCategory(id);
        if(category.isPresent()){
            return category.get();
        }
        return null;
    }

    @PutMapping("/category/{id}")
    public Category updateCategory(@Valid @RequestBody Category category, @PathVariable("id") Long id) throws CustomHttpException {
        Optional<Category> p = categoryService.getCategory(id); // Fetches the category from the database.
        if(p.isPresent()){ // Checks if the category already exists.
            Category cat = p.get();
            cat.setName(category.getName());
            categoryService.saveCategory(cat); // Update the category.
            return cat;
        }
        return null;
    }

    @DeleteMapping("/category/{id}")
    public void deleteCategory(@PathVariable("id") Long id) throws CustomHttpException {
        categoryService.deleteCategory(id);
    }
}
