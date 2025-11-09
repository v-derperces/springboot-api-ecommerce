package fr.afpa.pompey.APIEcommerce.repository;

import fr.afpa.pompey.APIEcommerce.model.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RoleRepository extends CrudRepository<Role, Integer> {
    Optional<Object> findRoleByName(String name);
}
