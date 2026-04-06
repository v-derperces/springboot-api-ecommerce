package fr.afpa.pompey.APIEcommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fr.afpa.pompey.APIEcommerce.dto.category.CategoryResponse;
import fr.afpa.pompey.APIEcommerce.dto.product.ProductRequest;
import fr.afpa.pompey.APIEcommerce.dto.product.ProductResponse;
import fr.afpa.pompey.APIEcommerce.exceptions.ConflictException;
import fr.afpa.pompey.APIEcommerce.exceptions.NotFoundException;
import fr.afpa.pompey.APIEcommerce.mapper.ProductMapper;
import fr.afpa.pompey.APIEcommerce.model.Category;
import fr.afpa.pompey.APIEcommerce.model.Product;
import fr.afpa.pompey.APIEcommerce.repository.OrderlineRepository;
import fr.afpa.pompey.APIEcommerce.repository.ProductRepository;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderlineRepository orderlineRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductsShouldReturnMappedResponses() {
        Product product = new Product();
        product.setProductId(1L);
        product.setName("Widget");

        ProductResponse response = new ProductResponse();
        response.setProductId(1L);
        response.setName("Widget");

        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toDTO(product)).thenReturn(response);

        List<ProductResponse> result = productService.getProducts();

        assertEquals(1, result.size());
        assertEquals("Widget", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getProductNotFoundShouldThrowNotFoundException() {
        long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productService.getProduct(productId));

        assertEquals("Cannot get product: No product found with id: " + productId, exception.getMessage());
    }

    @Test
    void createProductShouldSaveAndReturnResponse() {
        ProductRequest request = new ProductRequest();
        request.setName("Widget");
        request.setPrice(9.99);
        request.setStock(10);
        request.setCategoryIds(List.of(1L));

        Product product = new Product();
        product.setName("Widget");

        Product savedProduct = new Product();
        savedProduct.setProductId(1L);
        savedProduct.setName("Widget");
        savedProduct.setPrice(9.99);
        savedProduct.setStock(10);
        savedProduct.setCategories(List.of(new Category()));

        ProductResponse response = new ProductResponse();
        response.setProductId(1L);
        response.setName("Widget");

        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setCategoryId(1L);
        categoryResponse.setName("Gadgets");

        when(productMapper.toEntity(request)).thenReturn(product);
        when(categoryService.getCategory(1L)).thenReturn(categoryResponse);
        when(productRepository.save(product)).thenReturn(savedProduct);
        when(productMapper.toDTO(savedProduct)).thenReturn(response);

        ProductResponse result = productService.createProduct(request);

        assertNotNull(result);
        assertEquals(1L, result.getProductId());
        assertEquals("Widget", result.getName());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void deleteProductWithOrderlineShouldThrowConflictException() {
        long productId = 10L;
        Product product = new Product();
        product.setProductId(productId);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(orderlineRepository.existsByProduct(product)).thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> productService.deleteProduct(productId));

        assertEquals("Product with id " + productId + " cannot be deleted because it is associated with an order",
                exception.getMessage());
        verify(orderlineRepository, times(1)).existsByProduct(product);
    }
}
