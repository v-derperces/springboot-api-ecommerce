package com.vderperces.ecommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vderperces.ecommerce.dto.user.UserAdminCreateRequest;
import com.vderperces.ecommerce.dto.user.UserAdminResponse;
import com.vderperces.ecommerce.dto.user.UserCreateRequest;
import com.vderperces.ecommerce.dto.user.UserResponse;
import com.vderperces.ecommerce.model.User;

/**
 * Mapper converting between User entities and user-related DTO objects.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Convert entity to standard user response DTO.
     *
     * @param user entity to convert
     * @return user response DTO
     */
    @Mapping(target = "id", source = "userId")
    UserResponse toResponse(User user);

    /**
     * Convert Registration request to User entity.
     *
     * @param request create request payload
     * @return user entity
     */
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "active", ignore = true)
    User toEntity(UserCreateRequest request);

    /**
     * Convert entity to admin user response DTO.
     *
     * @param user entity to convert
     * @return admin user response DTO
     */
    @Mapping(target = "id", source = "userId")
    UserAdminResponse toResponseAdmin(User user);

    /**
     * Convert admin user creation request to entity.
     *
     * @param request admin request payload
     * @return user entity
     */
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toEntity(UserAdminCreateRequest request);

}
