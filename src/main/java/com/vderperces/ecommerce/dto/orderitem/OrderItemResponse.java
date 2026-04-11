package com.vderperces.ecommerce.dto.orderitem;

import java.math.BigDecimal;

import lombok.Data;

/**
 * Response body for order item details.
 */
@Data
public class OrderItemResponse {

    /** Unique identifier of the order item. */
    private Long orderItemId;

    /** Name of the product. */
    private String productName;

    /** Stock Keeping Unit (SKU) of the product. */
    private String productSku;

    /** Unit price of the product. */
    private BigDecimal unitPrice;

    /** Quantity of the product ordered. */
    private Integer quantity;

    /** Subtotal amount for this order item (unit price * quantity). */
    private BigDecimal subtotal;
}
