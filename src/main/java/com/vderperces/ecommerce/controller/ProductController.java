package com.vderperces.ecommerce.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.vderperces.ecommerce.dto.product.ProductRequest;
import com.vderperces.ecommerce.dto.product.ProductResponse;
import com.vderperces.ecommerce.service.ProductService;

import jakarta.validation.Valid;

/**
 * REST controller for product-based endpoints.
 *
 * Provides operations for creating, reading, updating, and deleting products.
 */
@RestController
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
    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody final ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED.value()).body(this.productService.createProduct(request));
    }

    /**
     * Get all products.
     *
     * @return list of product responses with HTTP 200
     */
    @GetMapping("/products")
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return ResponseEntity.ok(this.productService.getProducts());
    }

    /**
     * Get a product by id.
     *
     * @param id the product id
     * @return product response with HTTP 200
     */
    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable final Long id) {
        return ResponseEntity.ok(this.productService.getProduct(id));
    }

    /**
     * Update a product by id.
     *
     * @param request the new product values
     * @param id      the product id
     * @return updated product response with HTTP 200
     */
    @PutMapping("/products/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@Valid @RequestBody final ProductRequest request,
            @PathVariable final Long id) {
        return ResponseEntity.ok(this.productService.updateProduct(id, request));
    }

    /**
     * Delete a product by id.
     *
     * @param id the product id
     * @return no content response with HTTP 204
     */
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable final Long id) {
        this.productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
