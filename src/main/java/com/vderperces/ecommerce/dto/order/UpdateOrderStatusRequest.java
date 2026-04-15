package com.vderperces.ecommerce.dto.order;

import com.vderperces.ecommerce.enums.OrderStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request body for updating an order's status and payment status (admin only).
 */
@Data
@Schema(description = "Request to update order status (admin only)")
public class UpdateOrderStatusRequest {

    /** New status of the order */
    @NotNull(message = "Status is required (SHIPPED, DELIVERED)")
    @Schema(description = "New order status", requiredMode = Schema.RequiredMode.REQUIRED)
    private OrderStatus status;
}
