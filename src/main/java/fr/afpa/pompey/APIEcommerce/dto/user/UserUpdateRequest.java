package fr.afpa.pompey.APIEcommerce.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequest {

    @NotBlank(message = "The last name must be provided.")
    private String lastName;

    @NotBlank(message = "The first name must be provided.")
    private String firstName;

    private String phone;

    @NotBlank(message = "The email address must be provided.")
    @Email(message = "The email format is invalid")
    private String email;

    private String address;
}
