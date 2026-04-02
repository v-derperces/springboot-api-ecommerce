package fr.afpa.pompey.APIEcommerce.dto.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request body for creating or updating a role.
 */
@Data
public class RoleRequest {

    /** Name of the role. */
    @NotBlank(message = "The role name must be provided.")
    @Size(max = 30, message = "The role name must not exceed 30 characters.")
    private String name;
}
