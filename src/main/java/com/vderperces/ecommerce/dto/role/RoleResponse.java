package com.vderperces.ecommerce.dto.role;

import lombok.Data;

/**
 * Response body for role details.
 */
@Data
public class RoleResponse {

    /** Unique identifier of created role. */
    private Long roleId;

    /** Name of role. */
    private String name;
}
