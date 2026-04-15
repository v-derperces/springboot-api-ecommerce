package com.vderperces.ecommerce.dto.product;

import java.math.BigDecimal;
import java.util.List;

import com.vderperces.ecommerce.dto.category.CategoryResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Response body for product details.
 */
@Data
@Schema(description = "Product response")
public class ProductResponse {

    /** Unique identifier of the product. */
    @Schema(description = "Product ID", example = "1")
    private Long productId;

    /** Name of the product. */
    @Schema(description = "Product name", example = "Wireless Headphones")
    private String name;

    /** Description of the product. */
    @Schema(description = "Product description", example = "Noise cancelling headphones")
    private String description;

    /** Unit price of the product. */
    @Schema(description = "Product price", example = "199.99")
    private BigDecimal price;

    /** Quantity available in stock. */
    @Schema(description = "Stock quantity", example = "100")
    private int stock;

    /** Whether the product is active and available for purchase. */
    @Schema(description = "Active status", example = "true")
    private boolean active;

    /** List of image URLs for the product. */
    @Schema(description = "Image URLs")
    private List<String> imageUrls;

    /** List of categories associated with the product. */
    @Schema(description = "Associated categories")
    private List<CategoryResponse> categories;
}
