package fr.afpa.pompey.APIEcommerce.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.URL;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Represents a product in the e-commerce system. */
@Data
@Entity
@Table(name = "product")
public class Product {

    /** Unique identifier of the product. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    /** Stock Keeping Unit, must be unique and not null. */
    @Column(name = "sku", unique = true, nullable = false, length = 30)
    @NotBlank(message = "The SKU must be provided.")
    @Size(max = 30, message = "The SKU must not exceed 30 characters.")
    private String sku;

    /** Name of the product. */
    @Column(name = "name", nullable = false, length = 50)
    @NotBlank(message = "The product name must be provided.")
    @Size(max = 50, message = "The product name must not exceed 50 characters.")
    private String name;

    /** Description of the product. Optional, max 200 characters. */
    @Column(name = "description", length = 200)
    @Size(max = 200, message = "The product description must not exceed 200 characters.")
    private String description;

    /** Unit price of the product. */
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Price must be provided")
    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    private BigDecimal price;

    /** Quantity available in stock. */
    @Column(name = "stock", nullable = false)
    @PositiveOrZero(message = "The stock cannot be negative.")
    private int stock = 0;

    /** Active flag to enable/disable product without deleting. */
    @Column(name = "active", nullable = false)
    private boolean active = true;

    /** List of image URLs for the product. Optional, max 5 URLs. */
    @ElementCollection
    @CollectionTable(name = "product_image_urls", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @Size(max = 5, message = "A maximum of 5 images is allowed")
    private List<@Size(max = 512) @URL String> imageUrls;

    /** List of categories to which the product belongs. */
    @ManyToMany
    @NotNull(message = "The list of categories must not be empty.")
    @Size(min = 1, message = "At least one category must be provided.")
    @Valid
    @JoinTable(name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "category_id"))
    private List<Category> categories = new ArrayList<>();

}
