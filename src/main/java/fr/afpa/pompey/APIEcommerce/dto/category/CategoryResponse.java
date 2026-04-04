package fr.afpa.pompey.APIEcommerce.dto.category;

import lombok.Data;

/**
 * Response body for category details.
 */
@Data
public class CategoryResponse {

    /** Unique identifier of the category. */
    private Long categoryId;

    /** Name of the category. */
    private String name;
}
