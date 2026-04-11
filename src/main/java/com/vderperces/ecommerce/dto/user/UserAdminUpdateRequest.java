package com.vderperces.ecommerce.dto.user;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request payload for administrator updates to a user.
 */
@Data
public class UserAdminUpdateRequest {

    /** Updated last name. */
    @NotBlank(message = "The last name must be provided.")
    private String lastName;

    /** Updated first name. */
    @NotBlank(message = "The first name must be provided.")
    private String firstName;

    /** Updated contact phone. */
    private String phone;

    /** Updated email. */
    @NotBlank(message = "The email address must be provided.")
    @Email(message = "The email format is invalid")
    private String email;

    /** Updated address. */
    private String address;

    /** Role IDs to assign to user.*/
    @Size(min = 1, message = "At least one role must be assigned")
    private List<Long> roles;

    /** Whether account is active. */
    private boolean active;
}
