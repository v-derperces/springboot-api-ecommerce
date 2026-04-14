package com.vderperces.ecommerce.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Payload to create a standard user account.
 */
@Data
public class UserCreateRequest {

    /** First name. */
    @NotBlank(message = "The first name must be provided.")
    private String firstName;

    /** Last name. */
    @NotBlank(message = "The last name must be provided.")
    private String lastName;

    /** Email address. */
    @NotBlank(message = "The email address must be provided.")
    @Email(message = "The email format is invalid")
    private String email;

    /** Password for registration. */
    @NotBlank(message = "The password must be provided.")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
