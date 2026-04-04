package fr.afpa.pompey.APIEcommerce.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request body for creating or updating a category.
 */
@Data
public class CategoryRequest {

    /** Name of the category. */
    @NotBlank(message = "The category name must be provided.")
    @Size(max = 30, message = "The category name must not exceed 30 characters.")
    private String name;
}
