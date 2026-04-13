package com.vderperces.ecommerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vderperces.ecommerce.dto.address.AddressRequest;
import com.vderperces.ecommerce.dto.address.AddressResponse;
import com.vderperces.ecommerce.dto.order.OrderPaymentRequest;
import com.vderperces.ecommerce.dto.order.OrderRequest;
import com.vderperces.ecommerce.dto.order.OrderResponse;
import com.vderperces.ecommerce.dto.orderitem.OrderItemRequest;
import com.vderperces.ecommerce.dto.orderitem.OrderItemResponse;
import com.vderperces.ecommerce.dto.user.UserResponse;
import com.vderperces.ecommerce.enums.OrderStatus;
import com.vderperces.ecommerce.enums.PaymentMethod;
import com.vderperces.ecommerce.enums.PaymentStatus;
import com.vderperces.ecommerce.exceptions.NotFoundException;
import com.vderperces.ecommerce.service.OrderService;

/**
 * Unit tests for OrderController.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createOrderShouldReturn201() throws Exception {
        OrderRequest request = buildOrderRequest();

        OrderResponse response = buildOrderResponse(1L, "ORD-202604-ABCDEFGH", "test@example.com");

        when(orderService.createOrder(any(OrderRequest.class), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))).andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.reference").value("ORD-202604-ABCDEFGH"))
                .andExpect(jsonPath("$.totalAmount").value("299.98"))
                .andExpect(jsonPath("$.paymentStatus").value("PENDING"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.userResponse.email").value("test@example.com"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].productName").value("Test Product"))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createOrderWithoutShippingAddressShouldReturn400() throws Exception {
        var payload = new java.util.HashMap<String, Object>();
        payload.put("items", List.of(buildOrderItemRequest()));
        payload.put("billingAddress", buildAddressRequest());
        payload.put("paymentMethod", "CREDIT_CARD");

        mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createOrderWithoutBillingAddressShouldReturn400() throws Exception {
        var payload = new java.util.HashMap<String, Object>();
        payload.put("items", List.of(buildOrderItemRequest()));
        payload.put("shippingAddress", buildAddressRequest());
        payload.put("paymentMethod", "CREDIT_CARD");

        mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createOrderWithoutPaymentMethodShouldReturn400() throws Exception {
        var payload = new java.util.HashMap<String, Object>();
        payload.put("items", List.of(buildOrderItemRequest()));

        mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createOrderUserNotFoundShouldReturn404() throws Exception {
        OrderRequest request = buildOrderRequest();

        when(orderService.createOrder(any(OrderRequest.class), anyString()))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrderWithoutAuthenticationShouldReturn401() throws Exception {
        OrderRequest request = buildOrderRequest();

        mockMvc.perform(post("/api/v1/orders").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void payOrderShouldReturn200() throws Exception {
        OrderPaymentRequest paymentRequest = new OrderPaymentRequest();
        paymentRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        OrderResponse paidResponse = new OrderResponse();
        paidResponse.setOrderId(1L);
        paidResponse.setStatus(OrderStatus.PAID);
        paidResponse.setPaymentStatus(PaymentStatus.PAID);
        paidResponse.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        when(orderService.payOrder(any(Long.class), any(PaymentMethod.class), anyString()))
                .thenReturn(paidResponse);

        mockMvc.perform(post("/api/v1/orders/1/pay").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void payOrderWithoutPaymentMethodShouldReturn400() throws Exception {
        var payload = new java.util.HashMap<String, Object>();

        mockMvc.perform(post("/api/v1/orders/1/pay").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void payOrderNotFoundShouldReturn404() throws Exception {
        OrderPaymentRequest paymentRequest = new OrderPaymentRequest();
        paymentRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        when(orderService.payOrder(any(Long.class), any(PaymentMethod.class), anyString()))
                .thenThrow(new NotFoundException("Order not found"));

        mockMvc.perform(post("/api/v1/orders/999/pay").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void payOrderWithoutAuthenticationShouldReturn401() throws Exception {
        OrderPaymentRequest paymentRequest = new OrderPaymentRequest();
        paymentRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        mockMvc.perform(post("/api/v1/orders/1/pay").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void cancelOrderShouldReturn200() throws Exception {
        OrderResponse response = buildOrderResponse(1L, "ORD-202604-ABCDEFGH", "test@example.com");
        response.setStatus(OrderStatus.CANCELLED);

        when(orderService.cancelOrder(any(Long.class), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/v1/orders/1/cancel")).andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.reference").value("ORD-202604-ABCDEFGH"))
                .andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void cancelOrderNotFoundShouldReturn404() throws Exception {
        when(orderService.cancelOrder(any(Long.class), anyString()))
                .thenThrow(new NotFoundException("Order not found"));

        mockMvc.perform(post("/api/v1/orders/999/cancel")).andExpect(status().isNotFound());
    }

    @Test
    void cancelOrderWithoutAuthenticationShouldReturn401() throws Exception {
        mockMvc.perform(post("/api/v1/orders/1/cancel")).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getUserOrdersShouldReturn200WithPaginatedOrders() throws Exception {
        OrderResponse order1 = buildOrderResponse(1L, "ORD-202604-AAAAAAAA", "test@example.com");
        OrderResponse order2 = buildOrderResponse(2L, "ORD-202604-BBBBBBBB", "test@example.com");

        Page<OrderResponse> page = new PageImpl<>(List.of(order1, order2));

        when(orderService.getUserOrders(anyString(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/orders").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].orderId").value(1))
                .andExpect(jsonPath("$.content[1].orderId").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getUserOrdersWithPaginationShouldReturn200() throws Exception {
        OrderResponse order1 = buildOrderResponse(1L, "ORD-202604-AAAAAAAA", "test@example.com");

        Page<OrderResponse> page = new PageImpl<>(List.of(order1),
                org.springframework.data.domain.PageRequest.of(0, 10), 1);

        when(orderService.getUserOrders(anyString(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(
                get("/api/v1/orders?page=0&size=10").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void getUserOrdersWithoutAuthenticationShouldReturn401() throws Exception {
        mockMvc.perform(get("/api/v1/orders").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getOrderByIdShouldReturn200() throws Exception {

        OrderResponse response = buildOrderResponse(1L, "ORD-123", "test@example.com");

        when(orderService.getUserOrderById(anyString(), any(Long.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/orders/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.reference").value("ORD-123"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void getOrderByIdNotFoundShouldReturn404() throws Exception {

        when(orderService.getUserOrderById(anyString(), anyLong()))
                .thenThrow(new NotFoundException("Order not found"));

        mockMvc.perform(get("/api/v1/orders/999")).andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllOrdersAsAdminShouldReturnPagedOrders() throws Exception {

        OrderResponse response1 = buildOrderResponse(1L, "ORD-1", "user1@example.com");
        OrderResponse response2 = buildOrderResponse(2L, "ORD-2", "user2@example.com");
        OrderResponse response3 = buildOrderResponse(3L, "ORD-3", "user2@example.com");

        Page<OrderResponse> page = new PageImpl<>(List.of(response1, response2, response3));

        when(orderService.getAllOrders(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/orders")).andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(3));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllOrdersWithPaginationShouldReturnCorrectPage() throws Exception {

        OrderResponse response1 = buildOrderResponse(1L, "ORD-1", "user1@example.com");
        OrderResponse response2 = buildOrderResponse(2L, "ORD-2", "user2@example.com");
        Page<OrderResponse> page = new PageImpl<>(List.of(response1, response2));

        when(orderService.getAllOrders(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/admin/orders?page=0&size=10")).andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(2));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getOrderByIdForAdminShouldReturnOrder() throws Exception {

        OrderResponse response = buildOrderResponse(1L, "ORD-123", "user@example.com");

        when(orderService.getOrderByIdForAdmin(any(Long.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/admin/orders/1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.reference").value("ORD-123"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getOrderByIdForAdminAsUserShouldReturnForbidden() throws Exception {

        mockMvc.perform(get("/api/v1/admin/orders/1")).andExpect(status().isForbidden());
    }

    private OrderRequest buildOrderRequest() {
        OrderRequest request = new OrderRequest();
        request.setItems(List.of(buildOrderItemRequest()));
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        request.setShippingAddress(buildAddressRequest());
        request.setBillingAddress(buildAddressRequest());
        return request;
    }

    private OrderItemRequest buildOrderItemRequest() {
        OrderItemRequest item = new OrderItemRequest();
        item.setProductId(1L);
        item.setQuantity(2);
        return item;
    }

    private AddressRequest buildAddressRequest() {
        AddressRequest address = new AddressRequest();
        address.setStreet("123 Main St");
        address.setCity("Paris");
        address.setZipCode("75001");
        address.setCountry("France");
        return address;
    }

    private AddressResponse buildAddressResponse() {
        AddressResponse address = new AddressResponse();
        address.setStreet("123 Main St");
        address.setCity("Paris");
        address.setZipCode("75001");
        address.setCountry("France");
        return address;
    }

    private OrderResponse buildOrderResponse(Long orderId, String reference, String email) {
        OrderResponse response = new OrderResponse();
        response.setOrderId(orderId);
        response.setReference(reference);
        response.setTotalAmount(new BigDecimal("299.98"));
        response.setStatus(OrderStatus.CREATED);
        response.setPaymentStatus(PaymentStatus.PENDING);
        response.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        response.setBillingAddress(buildAddressResponse());
        response.setShippingAddress(buildAddressResponse());

        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(email);
        response.setUserResponse(userResponse);

        OrderItemResponse itemResponse = new OrderItemResponse();
        itemResponse.setProductName("Test Product");
        itemResponse.setProductSku("TP-001");
        itemResponse.setQuantity(2);
        itemResponse.setUnitPrice(new BigDecimal("149.99"));

        response.setItems(List.of(itemResponse));
        response.setCreatedAt(LocalDateTime.now());

        return response;
    }
}
