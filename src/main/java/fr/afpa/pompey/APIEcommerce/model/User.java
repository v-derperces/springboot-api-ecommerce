package fr.afpa.pompey.APIEcommerce.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false)
    @NotBlank(message = "The last name must be provided.")
    private String lastName;

    @Column(nullable = false)
    @NotBlank(message = "The first name must be provided.")
    private String firstName;

    private String phone;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "The email address must be provided.")
    @Email(message = "The email format is invalid")
    private String email;

    private String address;

    @Column(nullable = false)
    @NotBlank(message = "The password must be provided.")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;


    @ManyToMany
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "role_id"))
    private List<Role> roles = new ArrayList<>();

    @Column(nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Order> orders = new ArrayList<>();

}
