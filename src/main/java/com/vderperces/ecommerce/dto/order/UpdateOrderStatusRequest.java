package com.vderperces.ecommerce.dto.order;

import com.vderperces.ecommerce.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request body for updating an order's status and payment status (admin only).
 */
@Data
public class UpdateOrderStatusRequest {

    /** New status of the order */
    @NotNull(message = "Status is required (SHIPPED, DELIVERED)")
    private OrderStatus status;

}
