package fr.afpa.pompey.APIEcommerce.dto;

import lombok.Data;

/**
 * Payload for login request containing username and password.
 */
@Data
public class LoginRequest {

    /** Username/email to authenticate. */
    private String username;

    /** Password to authenticate. */
    private String password;
}
