package com.vderperces.ecommerce.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.vderperces.ecommerce.dto.product.ProductRequest;
import com.vderperces.ecommerce.dto.product.ProductResponse;
import com.vderperces.ecommerce.enums.ProductVisibility;
import com.vderperces.ecommerce.service.ProductService;
import jakarta.validation.Valid;

/**
 * REST controller for product-based endpoints.
 *
 * Provides operations for creating, reading, updating, and deleting products.
 */
@RestController
@RequestMapping("/api/v1")
public class ProductController {

    /** Service for product business operations. */
    private final ProductService productService;

    public ProductController(final ProductService productService) {
        this.productService = productService;
    }

    /**
     * Create a new product.
     *
     * @param request the product request payload
     * @return the created product response with HTTP 201
     */
    @PostMapping("/admin/products")
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody final ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.productService.createProduct(request));
    }

    /**
     * Get active products (public catalog).
     *
     * @param pageable pagination information (default: page 0, size 20)
     * @return paginated list of active products
     */
    @GetMapping("/products")
    public ResponseEntity<Page<ProductResponse>> getActiveProducts(@PageableDefault(size = 20,
            page = 0, sort = "name", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(productService.getActiveProducts(pageable));
    }

    /**
     * Get product details (only if active).
     *
     * @param id product id
     * @return product details
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getActiveProductById(id));
    }

    /**
     * Get products with visibility filter (admin only).
     *
     * @param visibility filter (ALL, ACTIVE, INACTIVE)
     * @param pageable pagination information (default: page 0, size 20)
     * @return paginated list of products
     */
    @GetMapping("/admin/products")
    public ResponseEntity<Page<ProductResponse>> getProducts(
            @RequestParam(defaultValue = "ALL") ProductVisibility visibility,
            @PageableDefault(size = 20, page = 0, sort = "name",
                    direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(productService.getProducts(visibility, pageable));
    }

    /**
     * Get product details (admin view, includes inactive products).
     *
     * @param id product id
     * @return product details
     */
    @GetMapping("/admin/products/{id}")
    public ResponseEntity<ProductResponse> getProductAsAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    /**
     * Update a product by id.
     *
     * @param request the new product values
     * @param id the product id
     * @return updated product response with HTTP 200
     */
    @PutMapping("/admin/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @Valid @RequestBody final ProductRequest request, @PathVariable final Long id) {
        return ResponseEntity.ok(this.productService.updateProduct(id, request));
    }

    /**
     * Delete a product by id.
     *
     * @param id the product id
     * @return no content response with HTTP 204
     */
    @DeleteMapping("/admin/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable final Long id) {
        this.productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
