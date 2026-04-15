package com.vderperces.ecommerce.dto.user;

import java.util.List;

import com.vderperces.ecommerce.dto.role.RoleResponse;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Response DTO for administrators viewing user details.
 */
@Data
@Schema(description = "Admin response containing full user account details")
public class UserAdminResponse {

    /** User unique id. */
    @Schema(description = "Unique identifier of the user", example = "1")
    private Long id;

    /** User first name. */
    @Schema(description = "User first name", example = "John")
    private String firstName;

    /** User last name. */
    @Schema(description = "User last name", example = "Doe")
    private String lastName;

    /** User contact email. */
    @Schema(description = "User email address", example = "john.doe@example.com")
    private String email;

    /** User phone number. */
    @Schema(description = "User phone number", example = "+33 6 12 34 56 78")
    private String phone;

    /** User shipping address. */
    @Schema(description = "User shipping address", example = "123 Main St, Paris")
    private String address;

    /** Assigned roles for the user. */
    @Schema(description = "List of roles assigned to the user")
    private List<RoleResponse> roles;

    /** Whether user account is active. */
    @Schema(description = "Indicates whether the user account is active", example = "true")
    private boolean active;
}
