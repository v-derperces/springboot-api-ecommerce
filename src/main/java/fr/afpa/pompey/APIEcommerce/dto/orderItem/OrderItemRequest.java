package fr.afpa.pompey.APIEcommerce.dto.orderItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request body for adding an item to an order.
 */
@Data
public class OrderItemRequest {

    /** The ID of the product */
    @NotNull(message = "Product ID must be provided")
    private Long productId;

    /** The quantity of the product */
    @NotNull(message = "Quantity must be provided")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
}
