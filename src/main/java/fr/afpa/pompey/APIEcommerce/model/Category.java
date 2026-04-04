package fr.afpa.pompey.APIEcommerce.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Represents a product category in the e-commerce domain.
 *
 * Categories are persisted in the database and may be associated with
 * multiple products through a many-to-many relationship.
 */
@Data
@Entity
@Table(name = "category")
public class Category {

    /**
     * Unique identifier for the category.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    /**
     * Category display name, required and unique.
     */
    @Column(name = "name", unique = true, nullable = false)
    @NotBlank(message = "The category name is required.")
    @Size(max = 30, message = "The category name should not exceed 30 characters.")
    private String name;

    /**
     * Products assigned to this category.
     *
     * This relationship is the inverse side of the Product.categories mapping.
     */
    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private List<Product> products = new ArrayList<>();

    /**
     * Set the category name after trimming whitespace.
     *
     * @param name the category name to store
     */
    public void setName(final String name) {
        this.name = name.trim();
    }
}
