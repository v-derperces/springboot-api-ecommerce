package fr.afpa.pompey.APIEcommerce.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import fr.afpa.pompey.APIEcommerce.dto.address.AddressResponse;
import fr.afpa.pompey.APIEcommerce.dto.orderItem.OrderItemResponse;
import fr.afpa.pompey.APIEcommerce.dto.user.UserResponse;
import fr.afpa.pompey.APIEcommerce.enums.PaymentMethod;
import fr.afpa.pompey.APIEcommerce.enums.PaymentStatus;
import lombok.Data;

/**
 * Response body for order details.
 */
@Data
public class OrderResponse {

    /** Unique identifier of the order. */
    private Long orderId;

    /** User associated to the order.  */
    private UserResponse userResponse;

    /** Reference code for the order. */
    private String reference;

    /** Total amount for the order. */
    private BigDecimal totalAmount;

    /** Status of the order. */
    private String status;

    /** Payment status of the order. */
    private PaymentStatus paymentStatus;

    /** Payment method used for the order. */
    private PaymentMethod paymentMethod;

    /** Shipping address for the order. */
    private AddressResponse shippingAddress;

    /** Billing address for the order. */
    private AddressResponse billingAddress;

    /** List of items in the order. */
    private List<OrderItemResponse> items;

    /** Date and time when the order was created. */
    private LocalDateTime createdAt;

    /** Date and time when the order was updated */
    private LocalDateTime updatedAt;

    /** Date and time when the order was paid. */
    private LocalDateTime paidAt;
}
