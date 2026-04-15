package com.vderperces.ecommerce.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vderperces.ecommerce.dto.order.OrderPaymentRequest;
import com.vderperces.ecommerce.dto.order.OrderRequest;
import com.vderperces.ecommerce.dto.order.OrderResponse;
import com.vderperces.ecommerce.dto.order.UpdateOrderStatusRequest;
import com.vderperces.ecommerce.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Rest controller for order operations.
 *
 * Provides endpoints for creating, reading, paying and cancelling orders. All endpoints require authentication except
 * where explicitly stated.
 */
@RestController
@RequestMapping("/api/v1")
@Tag(name = "Orders", description = "Order management operations")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Creates a new order for the authenticated user.
     *
     * @param request the order request containing items, addresses, etc.
     * @param principal the authenticated user
     * @return the created order details
     */
    @Operation(summary = "Create order")
    @ApiResponses({@ApiResponse(responseCode = "201", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "User or product not found"),
            @ApiResponse(responseCode = "409",
                    description = "Insufficient stock or product unavailable")})
    @PostMapping("/orders")
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request,
            final Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(request, authentication.getName()));
    }

    /**
     * Processes payment for an existing order.
     *
     * @param orderId the ID of the order to pay for
     * @param request the payment request containing the payment method
     * @param principal the authenticated user
     * @return the updated order details with paid status
     */
    @Operation(summary = "Pay order")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Payment successful"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409",
                    description = "Invalid order status or payment method")})
    @PostMapping("/orders/{orderId}/pay")
    public ResponseEntity<OrderResponse> payOrder(@PathVariable Long orderId,
            @Valid @RequestBody OrderPaymentRequest request, final Authentication authentication) {
        return ResponseEntity.ok(orderService.payOrder(orderId, request.getPaymentMethod(),
                authentication.getName()));
    }

    /**
     * Cancels an existing order and restores stock for its items.
     *
     * The order can only be cancelled if it is not in a final state (DELIVERED or CANCELLED) and belongs to the
     * authenticated user.
     *
     * @param orderId the ID of the order to cancel
     * @param authentication the authenticated user
     * @return the cancelled order details with HTTP 200
     */
    @Operation(summary = "Cancel order (user)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Order cancelled"),
            @ApiResponse(responseCode = "404", description = "Order not found"),
            @ApiResponse(responseCode = "409", description = "Order cannot be cancelled")})
    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId,
            final Authentication authentication) {
        return ResponseEntity.ok(orderService.cancelOrderAsUser(orderId, authentication.getName()));
    }

    /**
     * Retrieves all orders for the authenticated user with pagination support.
     *
     * @param authentication the authenticated user
     * @param pageable pagination and sorting parameters (default: page 0, size 20, sorted by createdAt DESC)
     * @return a page of the user's orders with HTTP 200
     */
    @Operation(summary = "Get user orders")
    @ApiResponse(responseCode = "200", description = "Orders retrieved")
    @GetMapping("/orders")
    public ResponseEntity<Page<OrderResponse>> getUserOrders(final Authentication authentication,
            @PageableDefault(size = 20, page = 0, sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.getUserOrders(authentication.getName(), pageable));
    }

    /**
     * Retrieve an order for the authenticated user.
     *
     * @param orderId the ID of the order to retrieve
     * @param authentication the authenticated user
     * @return the order detail
     */
    @Operation(summary = "Get user order by id")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")})
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<OrderResponse> getUserOrder(@PathVariable Long orderId,
            final Authentication authentication) {
        return ResponseEntity.ok(orderService.getUserOrderById(authentication.getName(), orderId));
    }

    /**
     * Retrieve all orders (admin access).
     *
     * @param pageable pagination and sorting parameters
     * @return paginated list of all orders
     */
    @Operation(summary = "Get all orders (admin)")
    @ApiResponse(responseCode = "200", description = "Orders retrieved")
    @GetMapping("/admin/orders")
    public ResponseEntity<Page<OrderResponse>> getAllOrders(@PageableDefault(size = 20, page = 0,
            sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    /**
     * Retrieve a specific order by its ID (admin access).
     *
     * @param orderId the ID of the order
     * @return the order details
     */
    @Operation(summary = "Get order by id (admin)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")})
    @GetMapping("/admin/orders/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.getOrderByIdForAdmin(orderId));
    }

    /**
     * Cancels an order as an admin.
     *
     * <p>
     * Allows cancelling any order except those already delivered or shipped. If the order is already cancelled, the
     * operation is idempotent and returns the current state.
     * </p>
     *
     * @param orderId the id of the order to cancel
     * @return the cancelled order
     */
    @Operation(summary = "Cancel order (admin)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Order cancelled"),
            @ApiResponse(responseCode = "409", description = "Order cannot be cancelled"),
            @ApiResponse(responseCode = "404", description = "Order not found")})
    @PostMapping("/admin/orders/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrderAsAdmin(orderId));
    }

    /**
     * Updates the status of an order as an admin.
     *
     * <p>
     * This endpoint allows an admin to progress an order through its lifecycle. Only forward transitions are supported:
     * <ul>
     * <li>PAID → SHIPPED</li>
     * <li>SHIPPED → DELIVERED</li>
     * </ul>
     *
     * @param orderId the identifier of the order to update
     * @param request the requested status update
     * @return the updated order
     */
    @Operation(summary = "Update order status (admin)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Order updated"),
            @ApiResponse(responseCode = "409", description = "Invalid status transition"),
            @ApiResponse(responseCode = "404", description = "Order not found")})
    @PatchMapping("/admin/orders/{orderId}")
    public ResponseEntity<OrderResponse> updateOrder(@PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatusAsAdmin(orderId, request));
    }
}
