package com.vderperces.ecommerce.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request body for creating or updating a category.
 */
@Data
@Schema(description = "Category request payload")
public class CategoryRequest {

    /** Name of the category. */
    @NotBlank(message = "The category name must be provided.")
    @Size(max = 30, message = "The category name must not exceed 30 characters.")
    @Schema(description = "Category name", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "Electronics")
    private String name;
}
