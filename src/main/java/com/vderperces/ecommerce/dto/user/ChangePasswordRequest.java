package com.vderperces.ecommerce.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request payload to change an existing user's password.
 */
@Data
@Schema(description = "Request payload used to change the password of an existing user")
public class ChangePasswordRequest {

    /** Current password for verification. */
    @NotBlank(message = "The current password must be provided.")
    @Schema(description = "Current password used to verify user identity", example = "OldP@ssw0rd!",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String currentPassword;

    /** New desired password. */
    @NotBlank(message = "The new password must be provided.")
    @Size(min = 6, message = "New password must be at least 6 characters")
    @Schema(description = "New password to replace the current one (minimum 6 characters)",
            example = "NewP@ssw0rd!", minLength = 6, requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
