package fr.afpa.pompey.APIEcommerce.dto.user;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String address;
}
