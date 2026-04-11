package com.vderperces.ecommerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.vderperces.ecommerce.dto.order.OrderRequest;
import com.vderperces.ecommerce.dto.order.OrderResponse;
import com.vderperces.ecommerce.dto.orderitem.OrderItemRequest;
import com.vderperces.ecommerce.dto.orderitem.OrderItemResponse;
import com.vderperces.ecommerce.dto.user.UserResponse;
import com.vderperces.ecommerce.enums.OrderStatus;
import com.vderperces.ecommerce.enums.PaymentMethod;
import com.vderperces.ecommerce.enums.PaymentStatus;
import com.vderperces.ecommerce.exceptions.InsufficientStockException;
import com.vderperces.ecommerce.exceptions.InvalidOrderStatusException;
import com.vderperces.ecommerce.exceptions.NotFoundException;
import com.vderperces.ecommerce.exceptions.ProductUnavailableException;
import com.vderperces.ecommerce.mapper.AddressMapper;
import com.vderperces.ecommerce.mapper.OrderMapper;
import com.vderperces.ecommerce.model.Order;
import com.vderperces.ecommerce.model.OrderItem;
import com.vderperces.ecommerce.model.Product;
import com.vderperces.ecommerce.model.User;
import com.vderperces.ecommerce.repository.OrderRepository;
import com.vderperces.ecommerce.repository.ProductRepository;
import com.vderperces.ecommerce.repository.UserRepository;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private AddressMapper addressMapper;
    private OrderMapper orderMapper;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepository.class);
        userRepository = mock(UserRepository.class);
        productRepository = mock(ProductRepository.class);
        addressMapper = mock(AddressMapper.class);
        orderMapper = mock(OrderMapper.class);

        orderService = new OrderService(orderRepository, userRepository, productRepository, addressMapper, orderMapper);
    }

    @Test
    void createOrderSuccessful() {

        String username = "user@example.com";
        User user = new User();
        user.setEmail(username);
        when(userRepository.findByEmail(username)).thenReturn(Optional.of(user));

        Product product = new Product();
        product.setProductId(1L);
        product.setName("Test Product");
        product.setStock(10);
        product.setPrice(new BigDecimal("100.0"));
        product.setSku("SKU1");
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        OrderItem item = new OrderItem();
        item.setProduct(product);

        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId(1L);
        itemReq.setQuantity(2);

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(itemReq));

        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(username);

        OrderItemResponse itemResponse = new OrderItemResponse();
        itemResponse.setProductName("Test Product");
        itemResponse.setQuantity(2);
        itemResponse.setUnitPrice(new BigDecimal("100.00"));

        OrderResponse response = new OrderResponse();
        response.setUserResponse(userResponse);
        response.setOrderId(1L);
        response.setReference("ORD-202401-ABCDEFGH");
        response.setTotalAmount(new BigDecimal("200.00"));
        response.setItems(List.of(itemResponse));
        when(orderMapper.toDTO(any(Order.class))).thenReturn(response);

        Order savedOrder = new Order();
        savedOrder.setUser(user);
        savedOrder.setItems(List.of(item));
        savedOrder.setTotalAmount(new BigDecimal("200.00"));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderResponse result = orderService.createOrder(request, username);

        assertNotNull(result);

        // Verify what was persisted
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(user, orderCaptor.getValue().getUser());
        assertEquals(1, orderCaptor.getValue().getItems().size());
        assertEquals(8, product.getStock());
        assertEquals(OrderStatus.CREATED, orderCaptor.getValue().getStatus());
        assertEquals(PaymentStatus.PENDING, orderCaptor.getValue().getPaymentStatus());

        // Verify respone
        assertEquals(user.getEmail(), result.getUserResponse().getEmail());
        assertEquals(1, result.getItems().size());
        assertEquals(new BigDecimal("200.00"), result.getTotalAmount());
    }

    @Test
    void createOrderUserNotFound() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());
        OrderRequest request = new OrderRequest();

        assertThrows(NotFoundException.class, () -> orderService.createOrder(request, "unknown@example.com"));
    }

    @Test
    void createOrderProductNotFound() {
        User user = new User();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId(1L);
        itemReq.setQuantity(1);

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(itemReq));

        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.createOrder(request, "user@example.com"));
    }

    @Test
    void createOrderProductUnavailable() {
        User user = new User();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Product product = new Product();
        product.setProductId(1L);
        product.setName("Test Product");
        product.setActive(false);
        product.setStock(10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId(1L);
        itemReq.setQuantity(5);

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(itemReq));

        assertThrows(ProductUnavailableException.class, () -> orderService.createOrder(request, "user@example.com"));
    }

    @Test
    void createOrderInsufficientStock() {
        User user = new User();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));

        Product product = new Product();
        product.setProductId(1L);
        product.setName("Test Product");
        product.setStock(1);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        OrderItemRequest itemReq = new OrderItemRequest();
        itemReq.setProductId(1L);
        itemReq.setQuantity(5);

        OrderRequest request = new OrderRequest();
        request.setItems(List.of(itemReq));

        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(request, "user@example.com"));
    }

    @Test
    void payOrderShouldUpdateStatusAndSetPaidAt() {
        User user = new User();
        user.setEmail("user@example.com");

        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setUser(user);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse paidResponse = new OrderResponse();
        paidResponse.setOrderId(1L);
        paidResponse.setStatus(OrderStatus.PAID);
        paidResponse.setPaymentStatus(PaymentStatus.PAID);
        when(orderMapper.toDTO(any(Order.class))).thenReturn(paidResponse);

        OrderResponse result = orderService.payOrder(1L, PaymentMethod.CREDIT_CARD, "user@example.com");

        assertEquals(OrderStatus.PAID, result.getStatus());
        assertEquals(PaymentStatus.PAID, result.getPaymentStatus());
        verify(orderRepository).save(order);
        assertNotNull(order.getPaidAt());
        assertEquals(PaymentMethod.CREDIT_CARD, order.getPaymentMethod());
    }

    @Test
    void payOrderNotInCreatedStatusShouldThrowConflictException() {
        User user = new User();
        user.setEmail("user@example.com");

        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.PAID);
        order.setUser(user);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStatusException.class,
                () -> orderService.payOrder(1L, PaymentMethod.CREDIT_CARD, "user@example.com"));
    }

    @Test
    void payOrderNotBelongingToUserShouldThrowNotFoundException() {
        User user = new User();
        user.setEmail("other@example.com");

        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setUser(user);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(NotFoundException.class,
                () -> orderService.payOrder(1L, PaymentMethod.CREDIT_CARD, "user@example.com"));
    }

    @Test
    void cancelOrderShouldRestoreStockAndReturnCancelledResponse() {
        User user = new User();
        user.setEmail("user@example.com");

        Product product = new Product();
        product.setProductId(1L);
        product.setName("Test Product");
        product.setStock(5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(2);

        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.CREATED);
        order.setUser(user);
        order.setItems(List.of(item));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderResponse canceledResponse = new OrderResponse();
        canceledResponse.setOrderId(1L);
        canceledResponse.setStatus(OrderStatus.CANCELLED);
        when(orderMapper.toDTO(any(Order.class))).thenReturn(canceledResponse);

        OrderResponse result = orderService.cancelOrder(1L, "user@example.com");

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
        assertEquals(7, product.getStock());
        verify(orderRepository).save(order);
    }

    @Test
    void cancelOrderInFinalStateShouldThrowInvalidOrderStatusException() {
        User user = new User();
        user.setEmail("user@example.com");

        Order order = new Order();
        order.setOrderId(1L);
        order.setStatus(OrderStatus.DELIVERED);
        order.setUser(user);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        assertThrows(InvalidOrderStatusException.class, () -> orderService.cancelOrder(1L, "user@example.com"));
    }

}
