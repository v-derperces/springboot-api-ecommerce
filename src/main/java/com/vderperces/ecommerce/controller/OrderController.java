package com.vderperces.ecommerce.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vderperces.ecommerce.dto.order.OrderPaymentRequest;
import com.vderperces.ecommerce.dto.order.OrderRequest;
import com.vderperces.ecommerce.dto.order.OrderResponse;
import com.vderperces.ecommerce.service.OrderService;

import jakarta.validation.Valid;

/**
 * Rest controller for order operations.
 *
 * Provides endpoints for creating, reading and updating orders.
 */
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Creates a new order for the authenticated user.
     *
     * @param request   the order request containing items, addresses, etc.
     * @param principal the authenticated user
     * @return the created order details
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request,
            final Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request, authentication.getName()));
    }

    /**
     * Processes payment for an existing order.
     *
     * @param orderId   the ID of the order to pay for
     * @param request   the payment request containing the payment method
     * @param principal the authenticated user
     * @return the updated order details with paid status
     */
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<OrderResponse> payOrder(@PathVariable Long orderId,
            @Valid @RequestBody OrderPaymentRequest request,
            final Authentication authentication) {
        return ResponseEntity.ok(orderService.payOrder(orderId, request.getPaymentMethod(), authentication.getName()));
    }

}
