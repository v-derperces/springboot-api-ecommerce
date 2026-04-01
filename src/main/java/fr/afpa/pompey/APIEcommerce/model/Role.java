package fr.afpa.pompey.APIEcommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Role entity used to define access permissions in the application.
 */
@Data
@Entity
@Table(name = "role")
public class Role {

    /** Unique identifier of the role. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "The role name is required.")
    @Size(max = 30, message = "The role name must not exceed 30 characters.")
    private String name;

    /**
     * Set and normalize role name.
     *
     * @param name raw role name
     */
    public void setName(final String name) {
        this.name = name.trim().toUpperCase();
    }
}
