package fr.afpa.pompey.APIEcommerce.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import fr.afpa.pompey.APIEcommerce.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByName(String name);

    Optional<Role> findByName(String name);

    List<Role> findByRoleIdIn(List<Long> ids);
}
