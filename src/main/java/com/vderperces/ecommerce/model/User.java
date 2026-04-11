package com.vderperces.ecommerce.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * User entity representing a system user profile and credentials.
 */
@Data
@Entity
@Table(name = "users")
public class User {

    /** Unique identifier of the user. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    /** User last name. */
    @Column(nullable = false)
    @NotBlank(message = "The last name must be provided.")
    private String lastName;

    /** User first name. */
    @Column(nullable = false)
    @NotBlank(message = "The first name must be provided.")
    private String firstName;

    /** Contact phone number. */
    private String phone;

    /** User email address (unique). */
    @Column(nullable = false, unique = true)
    @NotBlank(message = "The email address must be provided.")
    @Email(message = "The email format is invalid")
    private String email;

    /** User address for shipping and contact. */
    private String address;

    /** User password (write-only). */
    @Column(nullable = false)
    @NotBlank(message = "The password must be provided.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    /** Roles assigned to the user. */
    @ManyToMany
    @JoinTable(name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
    private List<Role> roles = new ArrayList<>();

    /** Flag to indicate if user account is active. */
    @Column(nullable = false)
    private boolean active = true;

    /** Related orders for this user. */
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();

    /**
     * Normalize and set first name.
     *
     * @param firstName first name
     */
    public void setFirstName(final String firstName) {
        this.firstName = firstName.trim();
    }

    /**
     * Normalize and set last name.
     *
     * @param lastName last name
     */
    public void setLastName(final String lastName) {
        this.lastName = lastName.trim();
    }

    /**
     * Normalize and set phone number.
     *
     * @param phone phone number
     */
    public void setPhone(final String phone) {
        this.phone = phone != null ? phone.trim() : null;
    }

    /**
     * Normalize and set address.
     *
     * @param address postal address
     */
    public void setAddress(final String address) {
        this.address = address != null ? address.trim() : null;
    }

}
