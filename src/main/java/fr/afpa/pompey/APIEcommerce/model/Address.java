package fr.afpa.pompey.APIEcommerce.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Represents an address associated with a user or an order.
 * This class is embedded within the User and Order entities to store address information.
 */
@Data
@Embeddable
public class Address {

    /** Street address. */
    @Column(name = "street", nullable = false, length = 100)
    @NotBlank(message = "Street cannot be blank")
    @Size(max = 100, message = "Street must be at most 100 characters")
    private String street;

    /** City name. */
    @Column(name = "city", nullable = false, length = 100)
    @NotBlank(message = "City cannot be blank")
    @Size(max = 100, message = "City must be at most 100 characters")
    private String city;

    /** Zip code. */
    @Column(name = "zip_code", nullable = false, length = 20)
    @NotBlank(message = "Zip code cannot be blank")
    @Size(max = 20, message = "Zip code must be at most 20 characters")
    private String zipCode;

    /** Country name. */
    @Column(name = "country", nullable = false, length = 100)
    @NotBlank(message = "Country cannot be blank")
    @Size(max = 100, message = "Country must be at most 100 characters")
    private String country;

    /**
     * Sets the street address, trimming any leading or trailing whitespace.
     *
     * @param street the street address
     */
    public void setStreet(String street) {
        this.street = street != null ? street.trim() : null;
    }

    /**
     * Sets the city name, trimming any leading or trailing whitespace.
     *
     * @param city the city name
     */
    public void setCity(String city) {
        this.city = city != null ? city.trim() : null;
    }

    /**
     * Sets the zip code, trimming any leading or trailing whitespace.
     *
     * @param zipCode the zip code
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode != null ? zipCode.trim() : null;
    }

    /**
     * Sets the country name, trimming any leading or trailing whitespace.
     *
     * @param country the country name
     */
    public void setCountry(String country) {
        this.country = country != null ? country.trim() : null;
    }
}
