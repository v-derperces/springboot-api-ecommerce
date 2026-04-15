package com.vderperces.ecommerce.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.vderperces.ecommerce.dto.product.ProductRequest;
import com.vderperces.ecommerce.dto.product.ProductResponse;
import com.vderperces.ecommerce.enums.ProductVisibility;
import com.vderperces.ecommerce.exceptions.ConflictException;
import com.vderperces.ecommerce.exceptions.NotFoundException;
import com.vderperces.ecommerce.mapper.ProductMapper;
import com.vderperces.ecommerce.model.Product;
import com.vderperces.ecommerce.repository.OrderItemRepository;
import com.vderperces.ecommerce.repository.ProductRepository;
import com.vderperces.ecommerce.util.SkuGenerator;

/**
 * Service responsible for product business logic and persistence operations.
 */
@Service
public class ProductService {

    /** Repository used to access product data storage. */
    private final ProductRepository productRepository;

    /** Repository used to check order item dependencies. */
    private final OrderItemRepository orderItemRepository;

    /** Service used to validate categories. */
    private final CategoryService categoryService;

    /** Mapper used to convert between product entities and DTOs. */
    private final ProductMapper productMapper;

    public ProductService(final ProductRepository productRepository,
            final OrderItemRepository orderItemRepository, final CategoryService categoryService,
            final ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
    }

    /**
     * Get only active products for public catalog.
     *
     * @param pageable pagination
     * @return paginated active products
     */
    public Page<ProductResponse> getActiveProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable).map(productMapper::toDTO);
    }

    /**
     * Get a single active product (public access).
     *
     * @param id product id
     * @return product response
     */
    public ProductResponse getActiveProductById(Long id) {
        Product product = productRepository.findByProductIdAndActiveTrue(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        return productMapper.toDTO(product);
    }

    /**
     * Admin: get products with visibility filter.
     *
     * @param visibility ACTIVE, INACTIVE, ALL
     * @param pageable pagination
     * @return paginated products
     */
    public Page<ProductResponse> getProducts(ProductVisibility visibility, Pageable pageable) {

        Page<Product> products;

        switch (visibility) {
            case ACTIVE_ONLY -> products = productRepository.findByActiveTrue(pageable);
            case INACTIVE_ONLY -> products = productRepository.findByActiveFalse(pageable);
            default -> products = productRepository.findAll(pageable);
        }

        return products.map(productMapper::toDTO);
    }

    /**
     * Admin: get product by id (any state).
     *
     * @param id product id
     * @return product response
     */
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found"));

        return productMapper.toDTO(product);
    }

    /**
     * Get product by id.
     *
     * @param id product id
     * @return product response
     */
    public ProductResponse getProduct(final Long id) {
        final Product product = this.productRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Cannot get product: No product found with id: " + id));

        return this.productMapper.toDTO(product);
    }

    /**
     * Create a product.
     *
     * @param request product request payload
     * @return created product response
     */
    public ProductResponse createProduct(final ProductRequest request) {
        final Product product = this.productMapper.toEntity(request);
        product.setCategories(this.categoryService.getCategoriesByIds(request.getCategoryIds()));
        product.setSku(SkuGenerator.generateSku(request.getName()));
        return this.productMapper.toDTO(this.productRepository.save(product));
    }

    /**
     * Update a product.
     *
     * @param id product id
     * @param request product request payload
     * @return updated product response
     */
    public ProductResponse updateProduct(final Long id, final ProductRequest request) {
        final Product existingProduct =
                this.productRepository.findById(id).orElseThrow(() -> new NotFoundException(
                        "Cannot update product: No product found with id: " + id));
        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStock(request.getStock());
        existingProduct.setActive(request.isActive());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setImageUrls(request.getImageUrls());
        existingProduct
                .setCategories(this.categoryService.getCategoriesByIds(request.getCategoryIds()));
        return this.productMapper.toDTO(this.productRepository.save(existingProduct));
    }

    /**
     * Delete product by id.
     *
     * @param id product id
     */
    public void deleteProduct(final Long id) {
        final Product product =
                this.productRepository.findById(id).orElseThrow(() -> new NotFoundException(
                        "Cannot delete product: No product found with id: " + id));

        if (this.orderItemRepository.existsByProduct(product)) {
            throw new ConflictException("Product with id " + id
                    + " cannot be deleted because it is associated with an order");
        }

        this.productRepository.deleteById(id);
    }
}
