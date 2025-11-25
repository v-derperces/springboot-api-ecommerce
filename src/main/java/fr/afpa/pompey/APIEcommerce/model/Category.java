package fr.afpa.pompey.APIEcommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long CategoryId;

    @Column(name = "name", unique = true, nullable = false)
    @NotBlank(message = "The category name is required.")
    @Size(max = 30, message = "The category name should not exceed 30 characters.")
    private String name;

    @ManyToMany(mappedBy = "categories")
    @JsonIgnore
    private List<Product> products = new ArrayList<>();
}
