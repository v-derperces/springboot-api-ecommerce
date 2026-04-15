package com.vderperces.ecommerce.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Payload to create a standard user account.
 */
@Data
@Schema(description = "Request payload used to create a standard user account")
public class UserCreateRequest {

    /** First name. */
    @NotBlank(message = "The first name must be provided.")
    @Schema(description = "User first name", example = "John",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    /** Last name. */
    @NotBlank(message = "The last name must be provided.")
    @Schema(description = "User last name", example = "Doe",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    /** Email address. */
    @NotBlank(message = "The email address must be provided.")
    @Email(message = "The email format is invalid")
    @Schema(description = "User email address", example = "john.doe@example.com",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    /** Password for registration. */
    @NotBlank(message = "The password must be provided.")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(description = "Password for account creation (minimum 6 characters)",
            example = "P@ssw0rd!", minLength = 6, requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
