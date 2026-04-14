package com.vderperces.ecommerce.dto.order;

import java.util.List;
import com.vderperces.ecommerce.dto.address.AddressRequest;
import com.vderperces.ecommerce.dto.orderitem.OrderItemRequest;
import com.vderperces.ecommerce.enums.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request body for creating a new order.
 */
@Data
public class OrderRequest {

    /** List of items in the order */
    @NotEmpty(message = "Order must have at least one item")
    @Valid
    private List<OrderItemRequest> items;

    /** Shipping address for the order */
    @NotNull(message = "Shipping address must be provided")
    @Valid
    private AddressRequest shippingAddress;

    /** Billing address for the order */
    @NotNull(message = "Billing address must be provided")
    @Valid
    private AddressRequest billingAddress;

    /** Payment method for the order */
    @NotNull(message = "Payment method must be provided")
    @Valid
    private PaymentMethod paymentMethod;

}
