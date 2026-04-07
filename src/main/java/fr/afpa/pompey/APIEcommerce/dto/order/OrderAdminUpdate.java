package fr.afpa.pompey.APIEcommerce.dto.order;

import fr.afpa.pompey.APIEcommerce.enums.OrderStatus;
import fr.afpa.pompey.APIEcommerce.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request body for updating an order's status and payment status (admin only).
 */
@Data
public class OrderAdminUpdate {

    /** New status of the order*/
    @NotNull(message = "Status is required (CREATED, PAID, SHIPPED, DELIVERED, CANCELLED)")
    private OrderStatus status;

    /** New payment status of the order*/
    @NotNull(message = "Payment status must be provided")
    private PaymentStatus paymentStatus;
}
