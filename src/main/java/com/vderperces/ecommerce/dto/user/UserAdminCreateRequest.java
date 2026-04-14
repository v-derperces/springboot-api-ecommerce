package com.vderperces.ecommerce.dto.user;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request payload for admin creation of a new user.
 */
@Data
public class UserAdminCreateRequest {

    /** User account last name. */
    @NotBlank(message = "The last name must be provided.")
    private String lastName;

    /** User account first name. */
    @NotBlank(message = "The first name must be provided.")
    private String firstName;

    /** Optional contact phone number. */
    private String phone;

    /** User email used for login. */
    @NotBlank(message = "The email address must be provided.")
    @Email(message = "The email format is invalid")
    private String email;

    /** Optional user postal address. */
    private String address;

    /** Password for the new user account. */
    @NotBlank(message = "The password must be provided.")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    /** Role IDs to assign to the new user. */
    @Size(min = 1, message = "At least one role must be assigned")
    private List<Long> roles;
}
