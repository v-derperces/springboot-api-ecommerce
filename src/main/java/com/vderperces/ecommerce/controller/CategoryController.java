package com.vderperces.ecommerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vderperces.ecommerce.dto.category.CategoryRequest;
import com.vderperces.ecommerce.dto.category.CategoryResponse;
import com.vderperces.ecommerce.service.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * REST controller for category-based endpoints.
 *
 * Provides operations for creating, reading, updating, and deleting categories.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {

    /** Service for category business operations. */
    private final CategoryService categoryService;

    public CategoryController(final CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Create a new category.
     *
     * @param request the category request payload
     * @return the created category response with HTTP 201
     */
    @PostMapping("/admin/categories")
    @Operation(summary = "Create category", description = "Create a new category (admin only)")
    @ApiResponse(responseCode = "201", description = "Category created successfully")
    public ResponseEntity<CategoryResponse> createCategory(
            @Valid @RequestBody final CategoryRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.categoryService.createCategory(request));
    }

    /**
     * Get all categories.
     *
     * @return list of category responses with HTTP 200
     */
    @GetMapping("/categories")
    @Operation(summary = "Get all categories", description = "Retrieve all categories")
    @ApiResponse(responseCode = "200", description = "Categories retrieved successfully")
    public ResponseEntity<List<CategoryResponse>> getCategories() {
        return ResponseEntity.ok(this.categoryService.getCategories());
    }

    /**
     * Get a category by id.
     *
     * @param id the category id
     * @return category response with HTTP 200
     */
    @GetMapping("/categories/{id}")
    @Operation(summary = "Get category by id", description = "Retrieve a category by its id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Category found"),
            @ApiResponse(responseCode = "404", description = "Category not found")})
    public ResponseEntity<CategoryResponse> getCategory(
            @Parameter(description = "Category id", example = "1") @PathVariable final Long id) {

        return ResponseEntity.ok(this.categoryService.getCategory(id));
    }

    /**
     * Update a category by id.
     *
     * @param request the new category values
     * @param id the category id
     * @return updated category response with HTTP 200
     */
    @PutMapping("/admin/categories/{id}")
    @Operation(summary = "Update category", description = "Update category by id (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Category updated successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"),
            @ApiResponse(responseCode = "409", description = "Conflict (duplicate category name)")})
    public ResponseEntity<CategoryResponse> updateCategory(
            @Valid @RequestBody final CategoryRequest request, @PathVariable final Long id) {

        return ResponseEntity.ok(this.categoryService.updateCategory(id, request));
    }

    /**
     * Delete a category by id.
     *
     * @param id the category id
     * @return no content response with HTTP 204
     */
    @DeleteMapping("/admin/categories/{id}")
    @Operation(summary = "Delete category", description = "Delete category by id (admin only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Category deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Category not found"), @ApiResponse(
                    responseCode = "409", description = "Conflict (category linked to product)")})
    public ResponseEntity<Void> deleteCategory(@PathVariable final Long id) {
        this.categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
