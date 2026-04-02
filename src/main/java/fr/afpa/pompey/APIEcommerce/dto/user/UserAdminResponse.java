package fr.afpa.pompey.APIEcommerce.dto.user;

import java.util.List;

import fr.afpa.pompey.APIEcommerce.dto.role.RoleResponse;
import lombok.Data;

/**
 * Response DTO for administrators viewing user details.
 */
@Data
public class UserAdminResponse {

    /** User unique id. */
    private Long id;

    /** User first name. */
    private String firstName;

    /** User last name. */
    private String lastName;

    /** User contact email. */
    private String email;

    /** User phone number. */
    private String phone;

    /** User shipping address. */
    private String address;

    /** Assigned roles for the user. */
    private List<RoleResponse> roles;

    /** Whether user account is active. */
    private boolean active;
}
