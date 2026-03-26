package fr.afpa.pompey.APIEcommerce.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "The current password must be provided.")
    private String currentPassword;

    @NotBlank(message = "The new password must be provided.")
    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;
}
