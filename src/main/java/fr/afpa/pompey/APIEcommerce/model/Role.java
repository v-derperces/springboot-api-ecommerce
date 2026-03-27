package fr.afpa.pompey.APIEcommerce.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "The role name is required.")
    private String name;

    public void setName(String name) {
        this.name = name.trim().toUpperCase();
    }
}
