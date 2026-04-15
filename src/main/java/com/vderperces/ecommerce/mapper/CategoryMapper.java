package com.vderperces.ecommerce.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.vderperces.ecommerce.dto.category.CategoryRequest;
import com.vderperces.ecommerce.dto.category.CategoryResponse;
import com.vderperces.ecommerce.model.Category;

/**
 * Mapper converting between Category entity and DTO objects.
 */
@Mapper(componentModel = "spring")
public interface CategoryMapper {

    /**
     * Convert a Category entity to a DTO.
     *
     * @param category entity to convert
     * @return category response DTO
     */
    CategoryResponse toDTO(Category category);

    /**
     * Convert a category create/update request into an entity.
     *
     * @param categoryRequest category payload
     * @return entity for persistence
     */
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "products", ignore = true)
    Category toEntity(CategoryRequest categoryRequest);
}
