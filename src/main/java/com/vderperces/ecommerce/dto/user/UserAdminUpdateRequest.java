package com.vderperces.ecommerce.dto.user;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request payload for administrator updates to a user.
 */
@Data
@Schema(description = "Request payload used by an admin to update an existing user")
public class UserAdminUpdateRequest {

    /** Updated last name. */
    @NotBlank(message = "The last name must be provided.")
    @Schema(description = "User last name", example = "Doe",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    /** Updated first name. */
    @NotBlank(message = "The first name must be provided.")
    @Schema(description = "User first name", example = "John",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    /** Updated contact phone. */
    @Schema(description = "User phone number", example = "+33 6 12 34 56 78")
    private String phone;

    /** Updated email. */
    @NotBlank(message = "The email address must be provided.")
    @Email(message = "The email format is invalid")
    @Schema(description = "User email address", example = "john.doe@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    /** Updated address. */
    @Schema(description = "User postal address", example = "123 Main St, Paris")
    private String address;

    /** Role IDs to assign to user. */
    @Size(min = 1, message = "At least one role must be assigned")
    @Schema(description = "List of role IDs assigned to the user", example = "[1, 2]")
    private List<Long> roles;

    /** Whether account is active. */
    @Schema(description = "Indicates whether the user account is active", example = "true")
    private boolean active;
}
