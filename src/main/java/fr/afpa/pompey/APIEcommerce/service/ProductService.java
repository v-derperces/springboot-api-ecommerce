package fr.afpa.pompey.APIEcommerce.service;

import java.util.List;

import org.springframework.stereotype.Service;

import fr.afpa.pompey.APIEcommerce.dto.product.ProductRequest;
import fr.afpa.pompey.APIEcommerce.dto.product.ProductResponse;
import fr.afpa.pompey.APIEcommerce.exceptions.ConflictException;
import fr.afpa.pompey.APIEcommerce.exceptions.NotFoundException;
import fr.afpa.pompey.APIEcommerce.mapper.ProductMapper;
import fr.afpa.pompey.APIEcommerce.model.Product;
import fr.afpa.pompey.APIEcommerce.repository.OrderlineRepository;
import fr.afpa.pompey.APIEcommerce.repository.ProductRepository;
import fr.afpa.pompey.APIEcommerce.util.SkuGenerator;

/**
 * Service responsible for product business logic and persistence operations.
 */
@Service
public class ProductService {

    /** Repository used to access product data storage. */
    private final ProductRepository productRepository;

    /** Repository used to check orderline dependencies. */
    private final OrderlineRepository orderlineRepository;

    /** Service used to validate categories. */
    private final CategoryService categoryService;

    /** Mapper used to convert between product entities and DTOs. */
    private final ProductMapper productMapper;

    public ProductService(final ProductRepository productRepository,
            final OrderlineRepository orderlineRepository,
            final CategoryService categoryService,
            final ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.orderlineRepository = orderlineRepository;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
    }

    /**
     * Get all products.
     *
     * @return list of product responses
     */
    public List<ProductResponse> getProducts() {
        return this.productRepository.findAll().stream().map(this.productMapper::toDTO).toList();
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
     * @param id      product id
     * @param request product request payload
     * @return updated product response
     */
    public ProductResponse updateProduct(final Long id, final ProductRequest request) {
        final Product existingProduct = this.productRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Cannot update product: No product found with id: " + id));
        existingProduct.setName(request.getName());
        existingProduct.setPrice(request.getPrice());
        existingProduct.setStock(request.getStock());
        existingProduct.setActive(request.isActive());
        existingProduct.setDescription(request.getDescription());
        existingProduct.setImageUrls(request.getImageUrls());
        existingProduct.setCategories(this.categoryService.getCategoriesByIds(request.getCategoryIds()));
        return this.productMapper.toDTO(this.productRepository.save(existingProduct));
    }

    /**
     * Delete product by id.
     *
     * @param id product id
     */
    public void deleteProduct(final Long id) {
        final Product product = this.productRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Cannot delete product: No product found with id: " + id));

        if (this.orderlineRepository.existsByProduct(product)) {
            throw new ConflictException(
                    "Product with id " + id + " cannot be deleted because it is associated with an order.");
        }

        this.productRepository.deleteById(id);
    }
}
