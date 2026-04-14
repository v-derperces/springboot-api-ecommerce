package com.vderperces.ecommerce.integration;

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vderperces.ecommerce.enums.OrderStatus;
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

        mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload))).andExpect(status().isCreated())
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
        assertEquals(8,
                productRepository.findById(testProduct.getProductId()).orElseThrow().getStock());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createOrderWithInsufficientStockShouldReturnBadRequest() throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentMethod", "CREDIT_CARD");
        payload.put("shippingAddress", buildAddressMap());
        payload.put("billingAddress", buildAddressMap());
        payload.put("items", List.of(buildItemMap(testProduct.getProductId(), 20)));

        mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isConflict());

        assertEquals(0, orderRepository.count());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createOrderWithUnavailableProductShouldReturn409() throws Exception {
        testProduct.setActive(false);
        productRepository.save(testProduct);

        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentMethod", "CREDIT_CARD");
        payload.put("shippingAddress", buildAddressMap());
        payload.put("billingAddress", buildAddressMap());
        payload.put("items", List.of(buildItemMap(testProduct.getProductId(), 1)));

        mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
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

        mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
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

        mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNotFound());

        assertEquals(0, orderRepository.count());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void payOrderShouldUpdateStatusToPaidAndSetPaidAt() throws Exception {

        Order order = createOrder(OrderStatus.CREATED);

        Map<String, Object> payPayload = new HashMap<>();
        payPayload.put("paymentMethod", "PAYPAL");

        mockMvc.perform(post("/api/v1/orders/" + order.getOrderId() + "/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payPayload))).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));

        Order paidOrder = orderRepository.findById(order.getOrderId()).orElseThrow();
        assertEquals("PAID", paidOrder.getStatus().toString());
        assertEquals("PAID", paidOrder.getPaymentStatus().toString());
        assertEquals("PAYPAL", paidOrder.getPaymentMethod().toString());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void payOrderAlreadyPaidShouldReturnConflict() throws Exception {
        Order order = createOrder(OrderStatus.CREATED);

        Map<String, Object> payPayload = new HashMap<>();
        payPayload.put("paymentMethod", "CREDIT_CARD");

        // First payment succeeds
        mockMvc.perform(post("/api/v1/orders/" + order.getOrderId() + "/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payPayload))).andExpect(status().isOk());

        // Second payment fails
        mockMvc.perform(post("/api/v1/orders/" + order.getOrderId() + "/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payPayload)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(username = "other@example.com", roles = "USER")
    void payOrderNotBelongingToUserShouldReturn404() throws Exception {

        Order order = createOrder(OrderStatus.CREATED); // Order belongs to user 'test@example.com'

        mockMvc.perform(post("/api/v1/orders/" + order.getOrderId() + "/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"paymentMethod\":\"CREDIT_CARD\"}")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void cancelOrderShouldUpdateStatusCancelledAndRestoreStock() throws Exception {
        Map<String, Object> createPayload = new HashMap<>();
        createPayload.put("paymentMethod", "CREDIT_CARD");
        createPayload.put("shippingAddress", buildAddressMap());
        createPayload.put("billingAddress", buildAddressMap());
        createPayload.put("items", List.of(buildItemMap(testProduct.getProductId(), 3)));

        String response = mockMvc
                .perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createPayload)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(response).get("orderId").asLong();

        Product productAfterCreate =
                productRepository.findById(testProduct.getProductId()).orElseThrow();
        assertEquals(7, productAfterCreate.getStock());

        mockMvc.perform(post("/api/v1/orders/" + orderId + "/cancel")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"));

        Product productAfterCancel =
                productRepository.findById(testProduct.getProductId()).orElseThrow();
        assertEquals(10, productAfterCancel.getStock());

        Order cancelledOrder = orderRepository.findById(orderId).orElseThrow();
        assertEquals("CANCELLED", cancelledOrder.getStatus().toString());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void cancelOrderAfterPaymentShouldSetPaymentStatusRefunded() throws Exception {
        Order order = createOrder(OrderStatus.PAID);

        mockMvc.perform(post("/api/v1/orders/" + order.getOrderId() + "/cancel")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.paymentStatus").value("REFUNDED"));

        Order cancelledOrder = orderRepository.findById(order.getOrderId()).orElseThrow();
        assertEquals("CANCELLED", cancelledOrder.getStatus().toString());
        assertEquals("REFUNDED", cancelledOrder.getPaymentStatus().toString());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getUserOrdersShouldReturnAllUserOrders() throws Exception {
        createOrder(OrderStatus.CREATED);
        createOrder(OrderStatus.PAID);

        mockMvc.perform(get("/api/v1/orders").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.content[0].userResponse.email").value("test@example.com"))
                .andExpect(jsonPath("$.content[1].userResponse.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getUserOrdersShouldReturnEmptyPageWhenNoOrders() throws Exception {
        mockMvc.perform(get("/api/v1/orders").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getUserOrdersWithPaginationShouldReturnPagedResults() throws Exception {
        createOrder(OrderStatus.CREATED);
        createOrder(OrderStatus.PAID);

        mockMvc.perform(
                get("/api/v1/orders?page=0&size=10").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getUserOrderByIdShouldReturnOrderDetailsWhenExists() throws Exception {

        Order order = createOrder(OrderStatus.CREATED);

        mockMvc.perform(get("/api/v1/orders/" + order.getOrderId())).andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(order.getOrderId()))
                .andExpect(jsonPath("$.userResponse.email").value("test@example.com"))
                .andExpect(jsonPath("$.reference").exists())
                .andExpect(jsonPath("$.items").isArray());
    }

    @Test
    void getUserOrderByIdShouldReturn404WhenOrderDoesNotBelongToUser() throws Exception {
        // Create order as 'test@example.com'
        Order order = createOrder(OrderStatus.CREATED);

        // Another user tries to access it
        mockMvc.perform(get("/api/v1/orders/" + order.getOrderId())
                .with(SecurityMockMvcRequestPostProcessors.user("other@example.com")))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllOrdersForAdminShouldReturnPagedOrders() throws Exception {

        createOrder(OrderStatus.CREATED);
        createOrder(OrderStatus.PAID);

        mockMvc.perform(get("/api/v1/admin/orders")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cancelOrderAsAdminShouldUpdateStatusAndRestoreStock() throws Exception {

        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentMethod", "CREDIT_CARD");
        payload.put("shippingAddress", buildAddressMap());
        payload.put("billingAddress", buildAddressMap());
        payload.put("items", List.of(buildItemMap(testProduct.getProductId(), 3)));

        String response = mockMvc
                .perform(post("/api/v1/orders")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(response).get("orderId").asLong();

        assertEquals(7,
                productRepository.findById(testProduct.getProductId()).orElseThrow().getStock());

        mockMvc.perform(post("/api/v1/admin/orders/" + orderId + "/cancel"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CANCELLED"));

        assertEquals(10,
                productRepository.findById(testProduct.getProductId()).orElseThrow().getStock());

        Order order = orderRepository.findById(orderId).orElseThrow();
        assertEquals("CANCELLED", order.getStatus().toString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cancelPaidOrderAsAdminShouldSetRefundedPaymentStatus() throws Exception {

        Order orderPaid = createOrder(OrderStatus.PAID);

        // CANCEL AS ADMIN
        mockMvc.perform(post("/api/v1/admin/orders/" + orderPaid.getOrderId() + "/cancel"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.paymentStatus").value("REFUNDED"));

        Order order = orderRepository.findById(orderPaid.getOrderId()).orElseThrow();
        assertEquals("REFUNDED", order.getPaymentStatus().toString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cancelAlreadyCancelledOrderShouldBeIdempotent() throws Exception {

        Order order = createOrder(OrderStatus.CREATED);

        // FIRST CANCEL
        mockMvc.perform(post("/api/v1/admin/orders/" + order.getOrderId() + "/cancel"))
                .andExpect(status().isOk());

        // STOCK restored
        assertEquals(10,
                productRepository.findById(testProduct.getProductId()).orElseThrow().getStock());

        // SECOND CANCEL (idempotent)
        mockMvc.perform(post("/api/v1/admin/orders/" + order.getOrderId() + "/cancel"))
                .andExpect(status().isOk());

        Order orderCancelled = orderRepository.findById(order.getOrderId()).orElseThrow();
        assertEquals("CANCELLED", orderCancelled.getStatus().toString());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void cancelShippedOrderShouldReturnConflict() throws Exception {

        Order order = createOrder(OrderStatus.SHIPPED);

        mockMvc.perform(post("/api/v1/admin/orders/" + order.getOrderId() + "/cancel"))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrderStatusPaidToShippedShouldSucceed() throws Exception {

        Order order = createOrder(OrderStatus.PAID);

        Map<String, Object> updateRequest = Map.of("status", "SHIPPED");

        mockMvc.perform(patch("/api/v1/admin/orders/" + order.getOrderId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))).andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHIPPED"));

        Order updated = orderRepository.findById(order.getOrderId()).orElseThrow();
        assertEquals(OrderStatus.SHIPPED, updated.getStatus());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrderStatusShippedToDeliveredShouldSucceed() throws Exception {

        Order order = createOrder(OrderStatus.SHIPPED);
        Long orderId = order.getOrderId();

        Map<String, Object> updateRequest = Map.of("status", "DELIVERED");

        mockMvc.perform(
                patch("/api/v1/admin/orders/" + orderId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("DELIVERED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrderStatusInvalidTransitionShouldReturn409() throws Exception {

        Order order = createOrder(OrderStatus.CANCELLED);
        Long orderId = order.getOrderId();

        Map<String, Object> payload = Map.of("status", "DELIVERED");

        mockMvc.perform(
                patch("/api/v1/admin/orders/" + orderId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateOrderStatusFromDeliveredShouldReturn409() throws Exception {

        Order order = createOrder(OrderStatus.DELIVERED);
        Long orderId = order.getOrderId();

        Map<String, Object> payload = Map.of("status", "SHIPPED");

        mockMvc.perform(
                patch("/api/v1/admin/orders/" + orderId).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isConflict());
    }

    private Order createOrder(OrderStatus orderStatus) throws Exception {
        Map<String, Object> payload = new HashMap<>();
        payload.put("paymentMethod", "CREDIT_CARD");
        payload.put("shippingAddress", buildAddressMap());
        payload.put("billingAddress", buildAddressMap());
        payload.put("items", List.of(buildItemMap(testProduct.getProductId(), 1)));

        String response = mockMvc
                .perform(post("/api/v1/orders")
                        .with(SecurityMockMvcRequestPostProcessors.user("test@example.com"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andReturn().getResponse().getContentAsString();

        Long orderId = objectMapper.readTree(response).get("orderId").asLong();

        Order order = orderRepository.findById(orderId).orElseThrow();
        order.setStatus(orderStatus);
        orderRepository.save(order);

        return orderRepository.findById(orderId).orElseThrow();
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
