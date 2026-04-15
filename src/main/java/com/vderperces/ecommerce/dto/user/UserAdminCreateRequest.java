package com.vderperces.ecommerce.dto.user;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request payload for admin creation of a new user.
 */
@Data
@Schema(description = "Request payload used by an admin to create a new user account")
public class UserAdminCreateRequest {

    /** User account last name. */
    @NotBlank(message = "The last name must be provided.")
    @Schema(description = "User last name", example = "Doe",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    /** User account first name. */
    @NotBlank(message = "The first name must be provided.")
    @Schema(description = "User first name", example = "John",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    /** Optional contact phone number. */
    @Schema(description = "User phone number", example = "+33 6 12 34 56 78")
    private String phone;

    /** User email used for login. */
    @NotBlank(message = "The email address must be provided.")
    @Email(message = "The email format is invalid")
    @Schema(description = "User email address used for login", example = "john.doe@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    /** Optional user postal address. */
    @Schema(description = "User postal address", example = "123 Main St, Paris")
    private String address;

    /** Password for the new user account. */
    @NotBlank(message = "The password must be provided.")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(description = "Password for the new account (minimum 6 characters)",
            example = "P@ssw0rd!", minLength = 6, requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    /** Role IDs to assign to the new user. */
    @Size(min = 1, message = "At least one role must be assigned")
    @Schema(description = "List of role IDs assigned to the user", example = "[1, 2]")
    private List<Long> roles;
}
