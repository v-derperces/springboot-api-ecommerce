package com.vderperces.ecommerce.dto.product;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.validator.constraints.URL;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request body for creating or updating a product.
 */
@Data
@Schema(description = "Product creation or update request")
public class ProductRequest {

    /** Name of the product. */
    @NotBlank(message = "The product name must be provided.")
    @Size(max = 50, message = "The product name must not exceed 50 characters.")
    @Schema(description = "Product name", example = "Wireless Headphones",
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 50)
    private String name;

    /** Description of the product. Optional, max 200 characters. */
    @Size(max = 200, message = "The product description must not exceed 200 characters.")
    @Schema(description = "Product description", example = "Noise cancelling headphones",
            maxLength = 200)
    private String description;

    /** Unit price of the product. */
    @NotNull(message = "Price must be provided")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    @Schema(description = "Product price", example = "199.99",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;

    /** Quantity available in stock. */
    @PositiveOrZero(message = "The stock cannot be negative.")
    @Schema(description = "Stock quantity", example = "100")
    private int stock;

    /** Active flag to enable/disable product without deleting. */
    @Schema(description = "Whether product is active", example = "true")
    private boolean active = true;

    /** List of image URLs for the product. Optional, max 5 URLs. */
    @Size(max = 5, message = "A maximum of 5 images is allowed")
    @Schema(description = "List of image URLs", example = "[\"https://example.com/img1.jpg\"]")
    private List<@Size(max = 512) @URL String> imageUrls;

    /** List of category IDs associated with the product. */
    @NotNull(message = "The list of category IDs must not be null.")
    @Size(min = 1, message = "At least one category must be provided.")
    @Schema(description = "Category IDs", example = "[1,2]",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Long> categoryIds;
}
