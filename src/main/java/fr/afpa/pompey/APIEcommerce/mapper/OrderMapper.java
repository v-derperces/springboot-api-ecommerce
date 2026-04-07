package fr.afpa.pompey.APIEcommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import fr.afpa.pompey.APIEcommerce.dto.order.OrderResponse;
import fr.afpa.pompey.APIEcommerce.model.Order;

/**
 * Mapper interface for converting between Order entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    /**
     * Convert a DTO to an Order entity.
     *
     * @param order the DTO to convert
     * @return the corresponding entity
     */
    @Mapping(target = "billingAddress", source = "billingAddress")
    OrderResponse toDTO(Order order);
}
