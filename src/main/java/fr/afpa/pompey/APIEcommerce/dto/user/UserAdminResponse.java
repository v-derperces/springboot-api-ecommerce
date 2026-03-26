package fr.afpa.pompey.APIEcommerce.dto.user;

import java.util.List;

import fr.afpa.pompey.APIEcommerce.dto.role.RoleResponse;
import lombok.Data;

@Data
public class UserAdminResponse {
     private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phone;

    private String address;

    private List<RoleResponse> roles;

    private boolean active;
}
