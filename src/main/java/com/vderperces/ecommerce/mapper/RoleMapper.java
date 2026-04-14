package com.vderperces.ecommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vderperces.ecommerce.dto.role.RoleRequest;
import com.vderperces.ecommerce.dto.role.RoleResponse;
import com.vderperces.ecommerce.model.Role;

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
