package fr.afpa.pompey.APIEcommerce.model;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "name", unique = true, nullable = false)
    @NotBlank(message = "The product name must be provided.")
    @Size(max = 50, message = "The product name must not exceed 50 characters.")
    private String name;

    @Column(name = "unit_price")
    @PositiveOrZero(message = "The price cannot be negative.")
    private double unitPrice = 0.0;

    @Column(name = "stock_quantity")
    @PositiveOrZero(message = "The stock cannot be negative.")
    private int stockQuantity = 0;

    @ManyToMany
    @NotNull(message = "The list of categories must not be empty.")
    @Size(min = 1, message = "At least one category must be provided.")
    @Valid
    @JoinTable(
        name = "product_categories",
        joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "category_id"))
    private List<Category> categories = new ArrayList<>();

}
