package fr.afpa.pompey.APIEcommerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.afpa.pompey.APIEcommerce.dto.product.ProductRequest;
import fr.afpa.pompey.APIEcommerce.dto.product.ProductResponse;
import fr.afpa.pompey.APIEcommerce.service.ProductService;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    void getProductsShouldReturn200() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProductAsAdminShouldReturn201() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Widget");
        request.setPrice(9.99);
        request.setStock(5);
        request.setCategoryIds(java.util.List.of(1L));

        ProductResponse response = new ProductResponse();
        response.setProductId(1L);
        response.setName("Widget");

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.name").value("Widget"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void createProductAsUserShouldReturn403() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Widget");
        request.setPrice(9.99);
        request.setStock(5);
        request.setCategoryIds(java.util.List.of(1L));

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProductAsAdminShouldReturn200() throws Exception {
        ProductRequest request = new ProductRequest();
        request.setName("Updated Widget");
        request.setPrice(12.50);
        request.setStock(7);
        request.setCategoryIds(java.util.List.of(1L));

        ProductResponse response = new ProductResponse();
        response.setProductId(1L);
        response.setName("Updated Widget");

        when(productService.updateProduct(any(Long.class), any(ProductRequest.class))).thenReturn(response);

        mockMvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value(1))
                .andExpect(jsonPath("$.name").value("Updated Widget"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProductAsAdminShouldReturn204() throws Exception {
        mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }
}
