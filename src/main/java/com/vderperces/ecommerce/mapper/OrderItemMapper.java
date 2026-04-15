package com.vderperces.ecommerce.mapper;

import org.mapstruct.Mapper;

import com.vderperces.ecommerce.dto.orderitem.OrderItemResponse;
import com.vderperces.ecommerce.model.OrderItem;

/**
 * Mapper interface for converting between OrderItem entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    /**
     * Convert a DTO to an OrderItem entity.
     *
     * @param orderItem the DTO to convert
     * @return the corresponding entity
     */
    OrderItemResponse toDTO(OrderItem orderItem);
}
