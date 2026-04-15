package com.vderperces.ecommerce.dto.address;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request body for creating or updating an address.
 */
@Data
@Schema(description = "Request payload for creating or updating an address")
public class AddressRequest {

    /** Street address (e.g., "123 Main St"). */
    @NotBlank(message = "Street cannot be blank")
    @Size(max = 100, message = "Street must be at most 100 characters")
    @Schema(description = "Street address", example = "123 Main St", maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String street;

    /** City name (e.g., "Paris"). */
    @NotBlank(message = "City cannot be blank")
    @Size(max = 100, message = "City must be at most 100 characters")
    @Schema(description = "City name", example = "Paris", maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    /** Zip code (e.g., "75001"). */
    @NotBlank(message = "Zip code cannot be blank")
    @Size(max = 20, message = "Zip code must be at most 20 characters")
    @Schema(description = "Postal or zip code", example = "75001", maxLength = 20,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String zipCode;

    /** Country name (e.g., "France"). */
    @NotBlank(message = "Country cannot be blank")
    @Size(max = 100, message = "Country must be at most 100 characters")
    @Schema(description = "Country name", example = "France", maxLength = 100,
            requiredMode = Schema.RequiredMode.REQUIRED)
    private String country;
}
