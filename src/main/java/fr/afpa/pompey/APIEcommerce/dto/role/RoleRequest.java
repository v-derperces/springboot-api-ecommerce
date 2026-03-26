package fr.afpa.pompey.APIEcommerce.dto.role;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequest {

    @NotBlank(message = "The role name must be provided.")
    private String name;
}
