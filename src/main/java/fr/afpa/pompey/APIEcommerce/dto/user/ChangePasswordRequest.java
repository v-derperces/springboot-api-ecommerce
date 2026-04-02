package fr.afpa.pompey.APIEcommerce.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request payload to change an existing user's password.
 */
@Data
public class ChangePasswordRequest {

    /** Current password for verification. */
    @NotBlank(message = "The current password must be provided.")
    private String currentPassword;

    /** New desired password. */
    @NotBlank(message = "The new password must be provided.")
    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;
}
