package fr.afpa.pompey.APIEcommerce.dto.user;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserAdminUpdateRequest {

     @NotBlank(message = "The last name must be provided.")
    private String lastName;

    @NotBlank(message = "The first name must be provided.")
    private String firstName;

    private String phone;

    @NotBlank(message = "The email address must be provided.")
    @Email(message = "The email format is invalid")
    private String email;

    private String address;

    @Size(min = 1, message = "At least one role must be assigned")
    private List<Long> roles;

    private boolean active;
}
