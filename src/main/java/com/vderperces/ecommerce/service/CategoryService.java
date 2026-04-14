package com.vderperces.ecommerce.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.vderperces.ecommerce.dto.category.CategoryRequest;
import com.vderperces.ecommerce.dto.category.CategoryResponse;
import com.vderperces.ecommerce.exceptions.ConflictException;
import com.vderperces.ecommerce.exceptions.NotFoundException;
import com.vderperces.ecommerce.mapper.CategoryMapper;
import com.vderperces.ecommerce.model.Category;
import com.vderperces.ecommerce.repository.CategoryRepository;

/**
 * Service responsible for category business logic and persistence operations.
 */
@Service
public class CategoryService {

    /** Repository used to access category data storage. */
    private final CategoryRepository categoryRepository;

    /** Mapper used to convert between category entities and DTOs. */
    private final CategoryMapper categoryMapper;

    public CategoryService(final CategoryRepository categoryRepository, final CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    /**
     * Get all categories.
     *
     * @return list of category responses
     */
    public List<CategoryResponse> getCategories() {
        return this.categoryRepository.findAll().stream().map(this.categoryMapper::toDTO).toList();
    }

    /**
     * Get category by id.
     *
     * @param id category id
     * @return category response
     */
    public CategoryResponse getCategory(final Long id) {
        final Category category = this.categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Cannot get category: No category found with id: " + id));

        return this.categoryMapper.toDTO(category);
    }

    /**
     * Create a category.
     *
     * @param request category request payload
     * @return created category response
     */
    public CategoryResponse createCategory(final CategoryRequest request) {
        try {
            final Category category = this.categoryMapper.toEntity(request);
            return this.categoryMapper.toDTO(this.categoryRepository.save(category));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    "Cannot create category: a category with name '" + request.getName() + "' already exists");
        }
    }

    /**
     * Update a category.
     *
     * @param id      category id
     * @param request category request payload
     * @return updated category response
     */
    public CategoryResponse updateCategory(final Long id, final CategoryRequest request) {
        final Category existingCategory = this.categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Cannot update category: No category found with id: " + id));
        try {
            existingCategory.setName(request.getName());
            return this.categoryMapper.toDTO(this.categoryRepository.save(existingCategory));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    "Cannot update category: a category with name '" + request.getName() + "' already exists");
        }
    }

    /**
     * Delete category by id.
     *
     * @param id category id
     */
    public void deleteCategory(final Long id) {
        try {
            this.categoryRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException(
                    "Category with id " + id + " cannot be deleted because it is associated with a product");
        }
    }

    public List<Category> getCategoriesByIds(final List<Long> categoryIds) {
        return this.categoryRepository.findAllById(categoryIds);
    }
}
