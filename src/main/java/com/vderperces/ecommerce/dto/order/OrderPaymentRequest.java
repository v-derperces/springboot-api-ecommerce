package com.vderperces.ecommerce.dto.order;

import com.vderperces.ecommerce.enums.PaymentMethod;

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
