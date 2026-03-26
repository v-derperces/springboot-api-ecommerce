package fr.afpa.pompey.APIEcommerce.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreateRequest {

    @NotBlank(message = "The first name must be provided.")
    private String firstName;

    @NotBlank(message = "The last name must be provided.")
    private String lastName;

    @NotBlank(message = "The email address must be provided.")
    @Email(message = "The email format is invalid")
    private String email;

    @NotBlank(message = "The password must be provided.")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
}
