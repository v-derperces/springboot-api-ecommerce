package com.vderperces.ecommerce.dto.orderitem;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request body for adding an item to an order.
 */
@Data
@Schema(description = "Order item request")
public class OrderItemRequest {

    /** The ID of the product */
    @Schema(description = "Product ID", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Product ID must be provided")
    private Long productId;

    /** The quantity of the product */
    @Schema(description = "Quantity", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "Quantity must be provided")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
