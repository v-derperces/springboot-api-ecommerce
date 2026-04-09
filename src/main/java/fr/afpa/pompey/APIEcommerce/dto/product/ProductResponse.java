package fr.afpa.pompey.APIEcommerce.dto.product;

import java.math.BigDecimal;
import java.util.List;

import fr.afpa.pompey.APIEcommerce.dto.category.CategoryResponse;
import lombok.Data;

/**
 * Response body for product details.
 */
@Data
public class ProductResponse {

    /** Unique identifier of the product. */
    private Long productId;

    /** Name of the product. */
    private String name;

    /** Description of the product. */
    private String description;

    /** Unit price of the product. */
    private BigDecimal price;

    /** Quantity available in stock. */
    private int stock;

    /** Whether the product is active and available for purchase. */
    private boolean active;

    /** List of image URLs for the product. */
    private List<String> imageUrls;

    /** List of category IDs associated with the product. */
    private List<CategoryResponse> categories;
}
