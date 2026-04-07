package fr.afpa.pompey.APIEcommerce.model;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Represents an item within an order, linking a product to an order.
 */
@Data
@Entity
@Table(name = "order_item")
public class OrderItem {

    /** Unique identifier for the order item */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long orderItemId;

    /** Order associated with this order item */
    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private Order order;

    /** Product associated with this order item */
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /** Name of the product at the time of the order (for history) */
    @Column(name = "product_name", nullable = false, length = 50)
    @NotBlank(message = "Product name must be provided")
    @Size(max = 50, message = "Product name must not exceed 50 characters")
    private String productName;

    /** Quantity of the product ordered */
    @Column(nullable = false)
    @NotNull(message = "Quantity must be provided")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    /** Unit price at the time of the order */
    @Column(name = "unit_price", nullable = false)
    @NotNull(message = "Unit price must be provided")
    @DecimalMin(value = "0.0", message = "Unit price must be zero or positive")
    private BigDecimal unitPrice;

    /** SKU of the product */
    @Column(name = "product_sku", nullable = false, length = 30)
    @NotBlank(message = "Product SKU must be provided")
    @Size(max = 30, message = "Product SKU must not exceed 30 characters")
    private String productSku;

    /**
     * Calculates the subtotal for this order item (unit price * quantity).
     * @return Subtotal as BigDecimal
     */
    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
