package fr.afpa.pompey.APIEcommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.afpa.pompey.APIEcommerce.dto.role.RoleRequest;
import fr.afpa.pompey.APIEcommerce.dto.role.RoleResponse;
import fr.afpa.pompey.APIEcommerce.model.Role;

/**
 * Mapper converting between Role entity and DTO objects.
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

    /**
     * Convert a Role entity to a DTO.
     *
     * @param role entity to convert
     * @return role response DTO
     */
    RoleResponse toDTO(Role role);

    /**
     * Convert a role create/update request into an entity.
     *
     * @param roleRequest role payload
     * @return entity for persistence
     */
    @Mapping(target = "roleId", ignore = true)
    Role toEntity(RoleRequest roleRequest);
}
