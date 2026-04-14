package com.vderperces.ecommerce.dto.user;

import lombok.Data;

/**
 * Response DTO for user profile output.
 */
@Data
public class UserResponse {

    /** User unique identifier. */
    private Long id;

    /** First name. */
    private String firstName;

    /** Last name. */
    private String lastName;

    /** Email address. */
    private String email;

    /** Phone number. */
    private String phone;

    /** Address. */
    private String address;
}
