package com.vderperces.ecommerce.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Payload for login request containing username and password.
 */
@Data
@Schema(description = "Request payload used to authenticate a user with username/email and password")
public class LoginRequest {

    /** Username/email to authenticate. */
    @Schema(description = "Username or email used for authentication",
            example = "john.doe@example.com")
    private String username;

    /** Password to authenticate. */
    @Schema(description = "User password in plain text (will be encrypted on server side)",
            example = "P@ssw0rd!")
    private String password;
}
