package com.vderperces.ecommerce.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Payload to update an existing user profile.
 */
@Data
public class UserUpdateRequest {

    /** Updated last name. */
    @NotBlank(message = "The last name must be provided.")
    private String lastName;

    /** Updated first name. */
    @NotBlank(message = "The first name must be provided.")
    private String firstName;

    /** Updated phone number. */
    private String phone;

    /** Updated email address. */
    @NotBlank(message = "The email address must be provided.")
    @Email(message = "The email format is invalid")
    private String email;

    /** Updated user postal address. */
    private String address;
}
