package com.vderperces.ecommerce.mapper;

import org.mapstruct.Mapper;

import com.vderperces.ecommerce.dto.address.AddressRequest;
import com.vderperces.ecommerce.dto.address.AddressResponse;
import com.vderperces.ecommerce.model.Address;

/**
 * Mapper interface for converting between Address entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface AddressMapper {

    /** Convert an Address entity to a DTO. */
    AddressResponse toDTO(Address address);

    Address toEntity(AddressRequest addressRequest);
}
