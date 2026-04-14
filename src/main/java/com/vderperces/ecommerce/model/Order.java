package com.vderperces.ecommerce.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.vderperces.ecommerce.enums.OrderStatus;
import com.vderperces.ecommerce.enums.PaymentMethod;
import com.vderperces.ecommerce.enums.PaymentStatus;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Represents an order placed by a user, containing order details and associated
 * items.
 */
@Data
@Entity
@Table(name = "orders")
public class Order {

    /** Unique identifier for the order. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    /** Unique reference code for the order. */
    @Column(nullable = false, unique = true, length = 50)
    @NotBlank(message = "Reference must be provided")
    @Size(max = 50, message = "Reference must not exceed 50 characters")
    private String reference;

    /** Total amount for the order. */
    @Column(name = "total_amount", nullable = false)
    @NotNull(message = "Total amount must be provided")
    @DecimalMin(value = "0.0", message = "Total amount cannot be negative")
    private BigDecimal totalAmount;

    /** Payment method used for the order (e.g., CREDIT_CARD, PAYPAL) */
    @Column(name = "payment_method", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    /** Payment status of the order (e.g., PAID, UNPAID) */
    @Column(name = "payment_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    /** Status of the order (e.g., CREATED, SHIPPED, DELIVERED). */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    /** Shipping address for the order. */
    @Embedded
    @NotNull(message = "Shipping address must be provided")
    private Address shippingAddress;

    /** Billing address for the order. */
    @Embedded
    @AttributeOverride(name = "street", column = @Column(name = "billing_street", nullable = false, length = 100))
    @AttributeOverride(name = "city", column = @Column(name = "billing_city", nullable = false, length = 100))
    @AttributeOverride(name = "zipCode", column = @Column(name = "billing_zip_code", nullable = false, length = 20))
    @AttributeOverride(name = "country", column = @Column(name = "billing_country", nullable = false, length = 100))
    @NotNull(message = "Billing address must be provided")
    private Address billingAddress;

    /** User who placed the order. */
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** List of items in the order. */
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Size(min = 1, message = "Order must have at least one item")
    private List<OrderItem> items = new ArrayList<>();

    /** Timestamps for order creation. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** The last time the order was updated (e.g., status change). */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** Timestamp when the order was paid. */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    /**
     * Adds an item to the order and sets the order reference in the item.
     *
     * @param item the order item to add
     */
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    /**
     * Calculates the total amount for the order by summing the subtotals of all
     * order items.
     *
     * @return the total amount for the order
     */

    public BigDecimal calculateTotalAmount() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Checks if the order is in a final state (DELIVERED or CANCELLED).
     *
     * @return true if the order is in a final state, false otherwise
     */
    public boolean isFinal() {
        return status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED;
    }

    /**
     * Checks if the order is in a state where it can still be cancelled by user
     * (CREATED or PAID).
     *
     * @return true if the order can be cancelled, false otherwise
     */
    public boolean isCancellable() {
        return status == OrderStatus.CREATED || status == OrderStatus.PAID;
    }

}
