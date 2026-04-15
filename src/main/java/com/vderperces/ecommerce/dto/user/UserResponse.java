package com.vderperces.ecommerce.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Response DTO for user profile output.
 */
@Data
@Schema(description = "User profile response")
public class UserResponse {

    /** User unique identifier. */
    @Schema(description = "User unique identifier", example = "1")
    private Long id;

    /** First name. */
    @Schema(description = "User first name", example = "John")
    private String firstName;

    /** Last name. */
    @Schema(description = "User last name", example = "Doe")
    private String lastName;

    /** Email address. */
    @Schema(description = "User email address", example = "john.doe@example.com")
    private String email;

    /** Phone number. */
    @Schema(description = "User phone number", example = "+33612345678")
    private String phone;

    /** Address. */
    @Schema(description = "User address", example = "123 Main St, Paris")
    private String address;
}
