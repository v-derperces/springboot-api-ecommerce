package com.vderperces.ecommerce.integration;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vderperces.ecommerce.model.Category;
import com.vderperces.ecommerce.model.Order;
import com.vderperces.ecommerce.model.Product;
import com.vderperces.ecommerce.model.Role;
import com.vderperces.ecommerce.model.User;
import com.vderperces.ecommerce.repository.CategoryRepository;
import com.vderperces.ecommerce.repository.OrderRepository;
import com.vderperces.ecommerce.repository.ProductRepository;
import com.vderperces.ecommerce.repository.RoleRepository;
import com.vderperces.ecommerce.repository.UserRepository;
import com.vderperces.ecommerce.util.SkuGenerator;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new IllegalStateException("USER role not found"));

        testUser = new User();
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("TestPass123"));
        testUser.setRoles(new ArrayList<>(List.of(userRole)));
        testUser.setActive(true);
        testUser = userRepository.save(testUser);

        Category category = new Category();
        category.setName("Hardware");
        category = categoryRepository.save(category);

        testProduct = new Product();
        testProduct.setSku(SkuGenerator.generateSku("Gadget"));
        testProduct.setName("Gadget");
        testProduct.setDescription("A useful gadget");
        testProduct.setPrice(new BigDecimal("49.99"));
        testProduct.setStock(10);
        testProduct.setActive(true);
        testProduct.setCategories(new ArrayList<>(List.of(category)));
        testProduct = productRepository.save(testProduct);
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createOrderShouldReturnCreatedAndPersistData() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentMethod", "CREDIT_CARD");
        payload.put("shippingAddress", buildAddressMap());
        payload.put("billingAddress", buildAddressMap());
        payload.put("items", List.of(buildItemMap(testProduct.getProductId(), 2)));

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").isNumber())
                .andExpect(jsonPath("$.reference").value(matchesPattern("ORD-\\d{6}-[A-Z0-9]{8}")))
                .andExpect(jsonPath("$.totalAmount").value("99.98"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.paymentMethod").value("CREDIT_CARD"))
                .andExpect(jsonPath("$.userResponse.email").value("test@example.com"));

        Order saved = orderRepository.findAll().stream().findFirst().orElseThrow();
        assertEquals(1, saved.getItems().size());
        assertEquals(0, saved.getTotalAmount().compareTo(new BigDecimal("99.98")));
        assertEquals("test@example.com", saved.getUser().getEmail());
        assertEquals(8, productRepository.findById(testProduct.getProductId()).orElseThrow().getStock());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createOrderWithInsufficientStockShouldReturnBadRequest() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentMethod", "CREDIT_CARD");
        payload.put("shippingAddress", buildAddressMap());
        payload.put("billingAddress", buildAddressMap());
        payload.put("items", List.of(buildItemMap(testProduct.getProductId(), 20)));

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isConflict());

        assertEquals(0, orderRepository.count());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createOrderWithUnavailableProductShouldReturnBadRequest() throws Exception {
        testProduct.setActive(false);
        productRepository.save(testProduct);

        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentMethod", "CREDIT_CARD");
        payload.put("shippingAddress", buildAddressMap());
        payload.put("billingAddress", buildAddressMap());
        payload.put("items", List.of(buildItemMap(testProduct.getProductId(), 1)));

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isConflict());

        assertEquals(0, orderRepository.count());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createOrderWithNonExistentProductShouldReturn404() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentMethod", "CREDIT_CARD");
        payload.put("shippingAddress", buildAddressMap());
        payload.put("billingAddress", buildAddressMap());
        payload.put("items", List.of(buildItemMap(9999L, 1)));

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());

        assertEquals(0, orderRepository.count());
    }

    @Test
    @WithMockUser(username = "nonexistent@example.com", roles = "USER")
    void createOrderWithNonExistentUserShouldReturn404() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentMethod", "CREDIT_CARD");
        payload.put("shippingAddress", buildAddressMap());
        payload.put("billingAddress", buildAddressMap());
        payload.put("items", List.of(buildItemMap(testProduct.getProductId(), 1)));

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());

        assertEquals(0, orderRepository.count());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void payOrderShouldUpdateStatusToPaidAndSetPaidAt() throws Exception {
        Map<String, Object> createPayload = new HashMap<>();
        createPayload.put("paymentMethod", "CREDIT_CARD");
        createPayload.put("shippingAddress", buildAddressMap());
        createPayload.put("billingAddress", buildAddressMap());
        createPayload.put("items", List.of(buildItemMap(testProduct.getProductId(), 1)));

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isCreated());

        Order createdOrder = orderRepository.findAll().stream().findFirst().orElseThrow();
        Long orderId = createdOrder.getOrderId();

        Map<String, Object> payPayload = new HashMap<>();
        payPayload.put("paymentMethod", "PAYPAL");

        mockMvc.perform(post("/api/v1/orders/" + orderId + "/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));

        Order paidOrder = orderRepository.findById(orderId).orElseThrow();
        assertEquals("PAID", paidOrder.getStatus().toString());
        assertEquals("PAID", paidOrder.getPaymentStatus().toString());
        assertEquals("PAYPAL", paidOrder.getPaymentMethod().toString());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void payOrderAlreadyPaidShouldReturnConflict() throws Exception {
        Map<String, Object> createPayload = new HashMap<>();
        createPayload.put("paymentMethod", "CREDIT_CARD");
        createPayload.put("shippingAddress", buildAddressMap());
        createPayload.put("billingAddress", buildAddressMap());
        createPayload.put("items", List.of(buildItemMap(testProduct.getProductId(), 1)));

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isCreated());

        Order createdOrder = orderRepository.findAll().stream().findFirst().orElseThrow();
        Long orderId = createdOrder.getOrderId();

        Map<String, Object> payPayload = new HashMap<>();
        payPayload.put("paymentMethod", "CREDIT_CARD");

        mockMvc.perform(post("/api/v1/orders/" + orderId + "/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payPayload)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/orders/" + orderId + "/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payPayload)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "other@example.com", roles = "USER")
    void payOrderNotBelongingToUserShouldReturn404() throws Exception {
        Map<String, Object> createPayload = new HashMap<>();
        createPayload.put("paymentMethod", "CREDIT_CARD");
        createPayload.put("shippingAddress", buildAddressMap());
        createPayload.put("billingAddress", buildAddressMap());
        createPayload.put("items", List.of(buildItemMap(testProduct.getProductId(), 1)));

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/v1/orders/1/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentMethod\":\"CREDIT_CARD\"}"))
                .andExpect(status().isNotFound());
    }

    private Map<String, Object> buildAddressMap() {
        Map<String, Object> address = new HashMap<>();
        address.put("street", "123 Main St");
        address.put("city", "Paris");
        address.put("zipCode", "75001");
        address.put("country", "France");
        return address;
    }

    private Map<String, Object> buildItemMap(Long productId, int quantity) {
        Map<String, Object> item = new HashMap<>();
        item.put("productId", productId);
        item.put("quantity", quantity);
        return item;
    }
}
