package com.vderperces.ecommerce.dto.category;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Response body for category details.
 */
@Data
@Schema(description = "Category response payload")
public class CategoryResponse {

    /** Unique identifier of the category. */
    @Schema(description = "Category ID")
    private Long categoryId;

    /** Name of the category. */
    @Schema(description = "Category name")
    private String name;
}
