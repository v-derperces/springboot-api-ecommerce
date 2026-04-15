package com.vderperces.ecommerce.dto.address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Response body for an address.
 */
@Data
@Schema(description = "Response object representing an address")
public class AddressResponse {

    /** Street address. */
    @Schema(description = "Street address", example = "123 Main St")
    private String street;

    /** City name. */
    @Schema(description = "City name", example = "Paris")
    private String city;

    /** Zip code. */
    @Schema(description = "Postal or zip code", example = "75001")
    private String zipCode;

    /** Country name. */
    @Schema(description = "Country name", example = "France")
    private String country;
}
