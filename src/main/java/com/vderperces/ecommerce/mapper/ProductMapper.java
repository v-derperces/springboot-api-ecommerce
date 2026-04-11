package com.vderperces.ecommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vderperces.ecommerce.dto.product.ProductRequest;
import com.vderperces.ecommerce.dto.product.ProductResponse;
import com.vderperces.ecommerce.model.Product;

/**
 * Mapper converting between Product entity and DTO objects.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper {

    /**
     * Convert a Product entity to a DTO.
     *
     * @param product entity to convert
     * @return product response DTO
     */
    ProductResponse toDTO(Product product);

    /**
     * Convert a product create/update request into an entity.
     *
     * @param productRequest product payload
     * @return entity for persistence
     */
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "categories", ignore = true)
    @Mapping(target = "sku", ignore = true)
    Product toEntity(ProductRequest productRequest);
}
