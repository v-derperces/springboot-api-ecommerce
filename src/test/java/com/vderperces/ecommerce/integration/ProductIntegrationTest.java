package com.vderperces.ecommerce.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vderperces.ecommerce.model.Category;
import com.vderperces.ecommerce.model.Product;
import com.vderperces.ecommerce.repository.CategoryRepository;
import com.vderperces.ecommerce.repository.ProductRepository;
import com.vderperces.ecommerce.util.SkuGenerator;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void getProductsShouldReturnSavedProduct() throws Exception {
        Category category = buildCategory("Gadgets");
        categoryRepository.save(category);

        Product product = buildProduct("Widget", new BigDecimal("9.99"), 5, category);
        productRepository.save(product);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Widget"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProductAsAdminShouldReturnCreated() throws Exception {
        Category category = buildCategory("Gadgets");
        categoryRepository.save(category);

        var payload = new java.util.HashMap<String, Object>();
        payload.put("name", "Widget");
        payload.put("price", new BigDecimal("9.99"));
        payload.put("stock", 5);
        payload.put("categoryIds", List.of(category.getCategoryId()));

        mockMvc.perform(post("/api/v1/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Widget"))
                .andExpect(jsonPath("$.productId").isNumber());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProductWithInvalidNameShouldReturnBadRequest() throws Exception {
        Category category = buildCategory("Gadgets");
        categoryRepository.save(category);

        var payload = new java.util.HashMap<String, Object>();
        payload.put("name", "");
        payload.put("price", new BigDecimal("9.99"));
        payload.put("stock", 5);
        payload.put("categoryIds", List.of(category.getCategoryId()));

        mockMvc.perform(post("/api/v1/admin/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProductAsAdminShouldRemoveProduct() throws Exception {
        Category category = buildCategory("Gadgets");
        categoryRepository.save(category);

        Product product = buildProduct("Widget", new BigDecimal("9.99"), 5, category);
        Product saved = productRepository.save(product);

        mockMvc.perform(delete("/api/v1/admin/products/" + saved.getProductId()))
                .andExpect(status().isNoContent());

        assert productRepository.findById(saved.getProductId()).isEmpty();
    }

    private Category buildCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }

    private Product buildProduct(String name, BigDecimal price, int stock, Category category) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStock(stock);
        product.setCategories(List.of(category));
        product.setSku(SkuGenerator.generateSku(name));
        return product;
    }
}
