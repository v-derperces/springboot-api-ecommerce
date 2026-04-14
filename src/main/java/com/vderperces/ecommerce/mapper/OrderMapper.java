package com.vderperces.ecommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vderperces.ecommerce.dto.order.OrderResponse;
import com.vderperces.ecommerce.model.Order;

/**
 * Mapper interface for converting between Order entities and DTOs.
 */
@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface OrderMapper {

    /**
     * Convert a DTO to an Order entity.
     *
     * @param order the DTO to convert
     * @return the corresponding entity
     */
    @Mapping(target = "userResponse", source = "user")
    OrderResponse toDTO(Order order);
}
