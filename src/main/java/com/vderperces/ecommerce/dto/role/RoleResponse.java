package com.vderperces.ecommerce.dto.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Response body for role details.
 */
@Data
@Schema(description = "Response object representing a role")
public class RoleResponse {

    /** Unique identifier of created role. */
    @Schema(description = "Unique identifier of the role", example = "1")
    private Long roleId;

    /** Name of role. */
    @Schema(description = "Name of the role", example = "USER")
    private String name;
}
