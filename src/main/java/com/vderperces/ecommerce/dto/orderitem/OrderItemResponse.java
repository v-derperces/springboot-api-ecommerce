package com.vderperces.ecommerce.dto.orderitem;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Response body for order item details.
 */
@Data
@Schema(description = "Order item response")
public class OrderItemResponse {

    /** Unique identifier of the order item. */
    @Schema(description = "Order item ID", example = "1001")
    private Long orderItemId;

    /** Name of the product. */
    @Schema(description = "Product name", example = "Wireless Mouse")
    private String productName;

    /** Stock Keeping Unit (SKU) of the product. */
    @Schema(description = "Product SKU", example = "MOUSE-123")
    private String productSku;

    /** Unit price of the product. */
    @Schema(description = "Unit price", example = "29.99")
    private BigDecimal unitPrice;

    /** Quantity of the product ordered. */
    @Schema(description = "Quantity", example = "2")
    private Integer quantity;

    /** Subtotal amount for this order item (unit price * quantity). */
    @Schema(description = "Subtotal", example = "59.98")
    private BigDecimal subtotal;
}
