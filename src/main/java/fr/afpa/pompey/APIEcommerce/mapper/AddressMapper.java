package fr.afpa.pompey.APIEcommerce.mapper;

import org.mapstruct.Mapper;

import fr.afpa.pompey.APIEcommerce.dto.address.AddressResponse;
import fr.afpa.pompey.APIEcommerce.model.Address;

/**
 * Mapper interface for converting between Address entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface AddressMapper {

    /** Convert an Address entity to a DTO. */
    AddressResponse toDTO(Address address);
}
