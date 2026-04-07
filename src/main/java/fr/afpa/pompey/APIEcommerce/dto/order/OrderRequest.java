package fr.afpa.pompey.APIEcommerce.dto.order;

import java.util.List;

import fr.afpa.pompey.APIEcommerce.dto.address.AddressRequest;
import fr.afpa.pompey.APIEcommerce.dto.orderItem.OrderItemRequest;
import fr.afpa.pompey.APIEcommerce.enums.PaymentMethod;
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
    private List<OrderItemRequest> items;

    /** Shipping address for the order */
    @NotNull(message = "Shipping address must be provided")
    private AddressRequest shippingAddress;

    /** Billing address for the order */
    @NotNull
    private AddressRequest billingAddress;

    /** Payment method for the order */
    @NotNull(message = "Payment method must be provided")
    private PaymentMethod paymentMethod;

}
