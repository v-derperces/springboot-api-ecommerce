package com.vderperces.ecommerce.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request body for creating or updating a role.
 */
@Data
@Schema(description = "Request payload for creating or updating a user role")
public class RoleRequest {

    /** Name of the role. */
    @NotBlank(message = "The role name must be provided.")
    @Size(max = 30, message = "The role name must not exceed 30 characters.")
    @Schema(description = "Name of the role", example = "ROLE_ADMIN", maxLength = 30,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}
