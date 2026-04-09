package fr.afpa.pompey.APIEcommerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.afpa.pompey.APIEcommerce.dto.address.AddressRequest;
import fr.afpa.pompey.APIEcommerce.dto.address.AddressResponse;
import fr.afpa.pompey.APIEcommerce.dto.order.OrderPaymentRequest;
import fr.afpa.pompey.APIEcommerce.dto.order.OrderRequest;
import fr.afpa.pompey.APIEcommerce.dto.order.OrderResponse;
import fr.afpa.pompey.APIEcommerce.dto.orderItem.OrderItemRequest;
import fr.afpa.pompey.APIEcommerce.dto.orderItem.OrderItemResponse;
import fr.afpa.pompey.APIEcommerce.dto.user.UserResponse;
import fr.afpa.pompey.APIEcommerce.enums.OrderStatus;
import fr.afpa.pompey.APIEcommerce.enums.PaymentMethod;
import fr.afpa.pompey.APIEcommerce.enums.PaymentStatus;
import fr.afpa.pompey.APIEcommerce.exceptions.NotFoundException;
import fr.afpa.pompey.APIEcommerce.service.OrderService;

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

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
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

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
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

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createOrderWithoutPaymentMethodShouldReturn400() throws Exception {
        var payload = new java.util.HashMap<String, Object>();
        payload.put("items", List.of(buildOrderItemRequest()));

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void createOrderUserNotFoundShouldReturn404() throws Exception {
        OrderRequest request = buildOrderRequest();

        when(orderService.createOrder(any(OrderRequest.class), anyString()))
                .thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrderWithoutAuthenticationShouldReturn401() throws Exception {
        OrderRequest request = buildOrderRequest();

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
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

        mockMvc.perform(post("/orders/1/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.status").value("PAID"))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    void payOrderWithoutPaymentMethodShouldReturn400() throws Exception {
        var payload = new java.util.HashMap<String, Object>();

        mockMvc.perform(post("/orders/1/pay")
                .contentType(MediaType.APPLICATION_JSON)
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

        mockMvc.perform(post("/orders/999/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void payOrderWithoutAuthenticationShouldReturn401() throws Exception {
        OrderPaymentRequest paymentRequest = new OrderPaymentRequest();
        paymentRequest.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        mockMvc.perform(post("/orders/1/pay")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isUnauthorized());
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
