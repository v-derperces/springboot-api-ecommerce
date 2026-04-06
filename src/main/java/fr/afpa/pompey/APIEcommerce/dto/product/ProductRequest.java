package fr.afpa.pompey.APIEcommerce.dto.product;

import java.util.List;

import org.hibernate.validator.constraints.URL;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request body for creating or updating a product.
 */
@Data
public class ProductRequest {

    /** Name of the product. */
    @NotBlank(message = "The product name must be provided.")
    @Size(max = 50, message = "The product name must not exceed 50 characters.")
    private String name;

    /** Description of the product. Optional, max 200 characters. */
    @Size(max = 200, message = "The product description must not exceed 200 characters.")
    private String description;

    /** Unit price of the product. */
    @PositiveOrZero(message = "The price cannot be negative.")
    private double price;

    /** Quantity available in stock. */
    @PositiveOrZero(message = "The stock cannot be negative.")
    private int stock;

    /** Active flag to enable/disable product without deleting. */
    private boolean active = true;

    /** List of image URLs for the product. Optional, max 5 URLs. */
    @Size(max = 5, message = "A maximum of 5 images is allowed")
    private List<@Size(max = 512) @URL String> imageUrls;

    /** List of category IDs associated with the product. */
    @NotNull(message = "The list of category IDs must not be null.")
    @Size(min = 1, message = "At least one category must be provided.")
    private List<Long> categoryIds;
}
