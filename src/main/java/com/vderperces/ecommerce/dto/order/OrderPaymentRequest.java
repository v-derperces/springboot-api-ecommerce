package com.vderperces.ecommerce.dto.order;

import com.vderperces.ecommerce.enums.PaymentMethod;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Request body for processing payment on an existing order.
 */
@Data
@Schema(description = "Order payment request")
public class OrderPaymentRequest {

    /** Payment method for the order */
    @NotNull(message = "Payment method must be provided")
    @Schema(description = "Payment method", example = "CREDIT_CARD",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private PaymentMethod paymentMethod;
}
