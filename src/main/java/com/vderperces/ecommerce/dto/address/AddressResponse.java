package com.vderperces.ecommerce.dto.address;

import lombok.Data;

/**
 * Response body for an address.
 */
@Data
public class AddressResponse {

    /** Street address. */
    private String street;

    /** City name. */
    private String city;

    /** Zip code. */
    private String zipCode;

    /** Country name. */
    private String country;
}
