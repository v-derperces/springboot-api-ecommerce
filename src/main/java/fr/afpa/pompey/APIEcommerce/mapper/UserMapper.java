package fr.afpa.pompey.APIEcommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import fr.afpa.pompey.APIEcommerce.dto.user.UserAdminCreateRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.UserAdminResponse;
import fr.afpa.pompey.APIEcommerce.dto.user.UserCreateRequest;
import fr.afpa.pompey.APIEcommerce.dto.user.UserResponse;
import fr.afpa.pompey.APIEcommerce.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", source = "userId")
    UserResponse toResponse(User user);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "active", ignore = true)
    User toEntity(UserCreateRequest request);

    @Mapping(target = "id", source = "userId")
    UserAdminResponse toResponseAdmin(User user);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "phone", ignore = true)
    @Mapping(target = "address", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User toEntity(UserAdminCreateRequest request);

}
