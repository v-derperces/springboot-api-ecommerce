package fr.afpa.pompey.APIEcommerce.dto.order;

import fr.afpa.pompey.APIEcommerce.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request body for processing payment on an existing order.
 */
@Data
public class OrderPaymentRequest {

    /** Payment method for the order */
    @NotNull(message = "Payment method must be provided")
    private PaymentMethod paymentMethod;

}
