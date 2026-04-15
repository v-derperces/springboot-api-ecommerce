package com.vderperces.ecommerce.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.vderperces.ecommerce.dto.address.AddressResponse;
import com.vderperces.ecommerce.dto.orderitem.OrderItemResponse;
import com.vderperces.ecommerce.dto.user.UserResponse;
import com.vderperces.ecommerce.enums.OrderStatus;
import com.vderperces.ecommerce.enums.PaymentMethod;
import com.vderperces.ecommerce.enums.PaymentStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Response body for order details.
 */
@Data
@Schema(description = "Order response payload")
public class OrderResponse {

    /** Unique identifier of the order. */
    @Schema(description = "Order ID")
    private Long orderId;

    /** User associated to the order. */
    @Schema(description = "User linked to the order")
    private UserResponse userResponse;

    /** Reference code for the order. */
    @Schema(description = "Order reference code")
    private String reference;

    /** Total amount for the order. */
    @Schema(description = "Total order amount")
    private BigDecimal totalAmount;

    /** Status of the order. */
    @Schema(description = "Order status")
    private OrderStatus status;

    /** Payment status of the order. */
    @Schema(description = "Payment status")
    private PaymentStatus paymentStatus;

    /** Payment method used for the order. */
    @Schema(description = "Payment method used")
    private PaymentMethod paymentMethod;

    /** Shipping address for the order. */
    @Schema(description = "Shipping address")
    private AddressResponse shippingAddress;

    /** Billing address for the order. */
    @Schema(description = "Billing address")
    private AddressResponse billingAddress;

    /** List of items in the order. */
    @Schema(description = "Order items")
    private List<OrderItemResponse> items;

    /** Date and time when the order was created. */
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    /** Date and time when the order was updated */
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;

    /** Date and time when the order was paid. */
    @Schema(description = "Payment timestamp")
    private LocalDateTime paidAt;
}
