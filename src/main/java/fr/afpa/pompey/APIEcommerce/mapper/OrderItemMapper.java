package fr.afpa.pompey.APIEcommerce.mapper;

import org.mapstruct.Mapper;

import fr.afpa.pompey.APIEcommerce.dto.orderItem.OrderItemResponse;
import fr.afpa.pompey.APIEcommerce.model.OrderItem;

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
