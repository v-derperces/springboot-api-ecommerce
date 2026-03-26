package fr.afpa.pompey.APIEcommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.afpa.pompey.APIEcommerce.dto.role.RoleRequest;
import fr.afpa.pompey.APIEcommerce.dto.role.RoleResponse;
import fr.afpa.pompey.APIEcommerce.model.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleResponse toDTO(Role role);

    @Mapping(target = "roleId", ignore = true)
    Role toEntity(RoleRequest roleRequest);
}
