package com.vderperces.ecommerce.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.vderperces.ecommerce.dto.order.OrderRequest;
import com.vderperces.ecommerce.dto.order.OrderResponse;
import com.vderperces.ecommerce.enums.OrderStatus;
import com.vderperces.ecommerce.enums.PaymentMethod;
import com.vderperces.ecommerce.enums.PaymentStatus;
import com.vderperces.ecommerce.exceptions.ConflictException;
import com.vderperces.ecommerce.exceptions.InsufficientStockException;
import com.vderperces.ecommerce.exceptions.InvalidOrderStatusException;
import com.vderperces.ecommerce.exceptions.InvalidPaymentMethodException;
import com.vderperces.ecommerce.exceptions.NotFoundException;
import com.vderperces.ecommerce.exceptions.OrderCreationException;
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

/**
 * Service responsible for order business logic and persistence operations.
 */
@Service
public class OrderService {

    private static final Set<PaymentMethod> ALLOWED_METHODS = EnumSet.of(PaymentMethod.CREDIT_CARD,
            PaymentMethod.PAYPAL, PaymentMethod.BANK_TRANSFER);

    /** Repository used to manage {@link Order} persistence operations. */
    private final OrderRepository orderRepository;

    /** Repository used to retrieve and manage {@link User} data. */
    private final UserRepository userRepository;

    /** Repository used to retrieve and update {@link Product} data. */
    private final ProductRepository productRepository;

    /** Mapper used to convert address DTOs to entities. */
    private final AddressMapper addressMapper;

    /**
     * Mapper used to convert {@link Order} entities to {@link OrderResponse} DTOs.
     */
    private final OrderMapper orderMapper;

    /** Logger for tracking order processing events and issues. */
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    /**
     * Constructs an {@code OrderService} with required dependencies.
     *
     * @param orderRepository repository for order persistence
     * @param userRepository repository for user retrieval
     * @param productRepository repository for product retrieval and updates
     * @param addressMapper mapper for address conversion
     * @param orderMapper mapper for order conversion
     */
    public OrderService(final OrderRepository orderRepository, final UserRepository userRepository,
            final ProductRepository productRepository, final AddressMapper addressMapper,
            final OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.addressMapper = addressMapper;
        this.orderMapper = orderMapper;
    }

    /**
     * Creates a new order for a given user.
     * <p>
     * This method:
     * <ul>
     * <li>Retrieves the user by email</li>
     * <li>Validates product availability</li>
     * <li>Updates product stock</li>
     * <li>Builds and persists the order</li>
     * <li>Retries up to 3 times in case of reference conflicts</li>
     * </ul>
     *
     * @param request the order request containing items and addresses
     * @param username the email of the user placing the order
     * @return the created order as {@link OrderResponse}
     *
     * @throws NotFoundException if the user or a product is not found
     * @throws InsufficientStockException if a product does not have enough stock
     * @throws OrderCreationException if the order cannot be created after retries
     */
    @Transactional
    public OrderResponse createOrder(OrderRequest request, String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        for (int attempt = 0; attempt < 3; attempt++) {
            try {

                Order order = new Order();
                order.setUser(user);
                order.setStatus(OrderStatus.CREATED);
                order.setPaymentStatus(PaymentStatus.PENDING);
                order.setPaymentMethod(request.getPaymentMethod());
                order.setShippingAddress(addressMapper.toEntity(request.getShippingAddress()));
                order.setBillingAddress(addressMapper.toEntity(request.getBillingAddress()));
                order.setReference(generateOrderReference());
                order.setCreatedAt(LocalDateTime.now());
                order.setUpdatedAt(LocalDateTime.now());

                List<OrderItem> items = request.getItems().stream().map(itemReq -> {
                    Product product = productRepository.findById(itemReq.getProductId())
                            .orElseThrow(() -> new NotFoundException(
                                    "Product not found: " + itemReq.getProductId()));

                    if (!product.isActive()) {
                        throw new ProductUnavailableException(
                                "Product '" + product.getName() + "' (id=" + product.getProductId()
                                        + ") is not available for purchase");
                    }

                    int quantity = itemReq.getQuantity();

                    if (product.getStock() < quantity) {
                        LOGGER.warn(
                                "Insufficient stock for product '{}' (requested={}, available={})",
                                product.getName(), quantity, product.getStock());
                        throw new InsufficientStockException(
                                "Insufficient stock for product: " + product.getName());
                    }

                    product.setStock(product.getStock() - quantity);

                    OrderItem item = new OrderItem();
                    item.setProduct(product);
                    item.setProductName(product.getName());
                    item.setProductSku(product.getSku());
                    item.setUnitPrice(product.getPrice());
                    item.setQuantity(quantity);
                    item.setOrder(order);
                    return item;
                }).collect(Collectors.toList());

                order.setItems(items);
                order.setTotalAmount(order.calculateTotalAmount());

                Order savedOrder = orderRepository.save(order);

                return orderMapper.toDTO(savedOrder);

            } catch (DataIntegrityViolationException e) {
                if (attempt == 2) {
                    LOGGER.warn("Order creation failed after retries due to reference conflict", e);
                    throw new OrderCreationException(
                            "Unable to create order at this time. Please try again later.");
                }
                LOGGER.debug("Reference conflict on attempt {}, retrying...", attempt + 1);
            }
        }
        throw new OrderCreationException(
                "Unexpected error creating order. Please contact support.");
    }

    /**
     * Processes payment for an existing order.
     * <p>
     * This method validates that the order exists, belongs to the authenticated user, and is in CREATED status before
     * processing the payment. Upon successful payment, the order status is updated to PAID, payment status to PAID, and
     * paidAt timestampis set.
     *
     * @param orderId the ID of the order to pay for
     * @param paymentMethod the payment method used (e.g., CREDIT_CARD, PAYPAL)
     * @param username the email of the authenticated user
     * @return the updated order as {@link OrderResponse}
     *
     * @throws NotFoundException if the order does not exist or does not belong to the user
     * @throws ConflictException if the order is not in CREATED status
     */
    @Transactional
    public OrderResponse payOrder(Long orderId, PaymentMethod paymentMethod, String username) {
        if (!ALLOWED_METHODS.contains(paymentMethod)) {
            throw new InvalidPaymentMethodException(
                    "Payment method '" + paymentMethod + "' is not allowed");
        }

        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new NotFoundException("Payment aborted. Order not found: " + orderId));

        if (!order.getUser().getEmail().equals(username)) {
            LOGGER.warn("User {} attempted to pay for order {} which belongs to another user",
                    username, orderId);
            throw new NotFoundException("Order not found: " + orderId);
        }

        if (order.getStatus() != OrderStatus.CREATED) {
            LOGGER.warn("Cannot pay for order {} in status {}", orderId, order.getStatus());
            throw new InvalidOrderStatusException("Order cannot be paid because it is "
                    + order.getStatus().toString().toLowerCase());
        }

        // TODO: integrate real payment gateway here
        // Currently marking order as paid directly for demo/simulation purposes
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        Order paidOrder = orderRepository.save(order);
        LOGGER.info("Successfully processed payment for order {}", orderId);

        return orderMapper.toDTO(paidOrder);
    }

    /**
     * Cancels an existing order and restores stock for its items.
     * <p>
     * The order can only be cancelled if it is not in a final state (DELIVERED or CANCELLED) and belongs to the
     * authenticated user.
     *
     * @param orderId the id of the order to cancel
     * @param username the email of the authenticated user
     * @return the cancelled order as {@link OrderResponse}
     */
    @Transactional
    public OrderResponse cancelOrderAsUser(Long orderId, String username) {

        LOGGER.info("User request to cancel order id={}", orderId);

        Order order =
                orderRepository.findByOrderIdAndUser_Email(orderId, username).orElseThrow(() -> {
                    LOGGER.warn("Order not found for cancellation id={}", orderId);
                    return new NotFoundException("Order not found for cancellation: " + orderId);
                });

        Order updated = cancel(order, false);

        Order saved = orderRepository.save(updated);

        LOGGER.info("Order id={} cancelled by user", orderId);

        return orderMapper.toDTO(saved);

    }

    /**
     * Retrieves all orders for the authenticated user with pagination support.
     * <p>
     * This method ensures that users can only access their own orders by validating the ownership of each order against
     * the authenticated username.
     *
     * @param username the email of the authenticated user
     * @param pageable pagination and sorting parameters (default: page 0, size 20)
     * @return a page of orders belonging to the user, mapped to {@link OrderResponse}
     *
     * @throws NotFoundException if the user does not exist
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(String username, Pageable pageable) {

        // Fetch paginated orders for user
        Page<Order> ordersPage = orderRepository.findByUser_Email(username, pageable);

        List<OrderResponse> orderResponses = ordersPage.getContent().stream()
                .map(orderMapper::toDTO).collect(Collectors.toList());

        return new PageImpl<>(orderResponses, pageable, ordersPage.getTotalElements());
    }

    /**
     * Retrieves a single order belonging to the authenticated user.
     *
     * <p>
     * This method ensures that the order exists and is associated with the given username. If no order is found, or if
     * it does not belong to the user, a {@link NotFoundException} is thrown.
     * </p>
     *
     * @param username the email of the authenticated user
     * @param orderId the id of the order to retrieve
     * @return the corresponding {@link OrderResponse}
     * @throws NotFoundException if the order does not exist or does not belong to the user
     */
    public OrderResponse getUserOrderById(String username, Long orderId) {

        Order order = orderRepository.findByOrderIdAndUser_Email(orderId, username)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

        return orderMapper.toDTO(order);
    }

    /**
     * Retrieve all orders (admin access).
     *
     * @param pageable pagination and sorting parameters
     * @return paginated list of all orders
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {

        Page<Order> ordersPage = orderRepository.findAll(pageable);

        List<OrderResponse> orderResponses = ordersPage.getContent().stream()
                .map(orderMapper::toDTO).collect(Collectors.toList());

        return new PageImpl<>(orderResponses, pageable, ordersPage.getTotalElements());
    }

    /**
     * Retrieve a specific order by its ID (admin access).
     *
     * @param orderId the ID of the order
     * @return the order details
     * @throws NotFoundException if the order does not exist
     */
    public OrderResponse getOrderByIdForAdmin(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Order not found with id: " + orderId));

        return orderMapper.toDTO(order);
    }

    /**
     * Cancels an order as an administrator.
     *
     * <p>
     * Unlike a regular user, an administrator can cancel an order even if it is no longer in a "cancellable" state,
     * except when the order is already delivered.
     * </p>
     *
     * <p>
     * This method:
     * <ul>
     * <li>Retrieves the order</li>
     * <li>Applies cancellation business rules (admin mode)</li>
     * <li>Persists the changes</li>
     * <li>Returns the updated order as a DTO</li>
     * </ul>
     *
     * @param orderId the identifier of the order to cancel
     * @return the cancelled order as a DTO
     *
     * @throws NotFoundException if the order does not exist
     * @throws InvalidOrderStatusException if the order cannot be cancelled
     */
    @Transactional
    public OrderResponse cancelOrderAsAdmin(Long orderId) {

        LOGGER.info("Admin request to cancel order id={}", orderId);

        Order order = orderRepository.findById(orderId).orElseThrow(() -> {
            LOGGER.warn("Order not found for cancellation id={}", orderId);
            return new NotFoundException("Order not found for cancellation: " + orderId);
        });

        Order updated = cancel(order, true);

        Order saved = orderRepository.save(updated);

        LOGGER.info("Order id={} cancelled by admin", orderId);

        return orderMapper.toDTO(saved);
    }

    /**
     * Cancels an order by applying business rules depending on the context (admin or regular user).
     *
     * <p>
     * Applied rules:
     * <ul>
     * <li>User: cancellation allowed only if the order is in a cancellable state (via {@code isCancellable()})</li>
     * <li>Admin: can cancel any order except if it is already delivered</li>
     * <li>If the order is paid: triggers a refund (to be implemented)</li>
     * <li>Restores stock for each order item</li>
     * <li>Updates the order status to {@code CANCELLED}</li>
     * </ul>
     *
     * @param order the order to cancel
     * @param isAdmin whether the operation is performed by an administrator
     * @return the updated order (not yet persisted)
     *
     * @throws InvalidOrderStatusException if cancellation is not allowed
     */
    private Order cancel(Order order, boolean isAdmin) {

        if (order.getStatus() == OrderStatus.CANCELLED) {
            LOGGER.info("Order id={} is already cancelled", order.getOrderId());
            return order;
        }

        LOGGER.info("Cancelling order id={} as {}", order.getOrderId(), isAdmin ? "ADMIN" : "USER");

        if (!isAdmin) {
            if (!order.isCancellable()) {
                LOGGER.warn("Cancellation refused for order id={} (user) due to status={}",
                        order.getOrderId(), order.getStatus());
                throw new InvalidOrderStatusException("Order cannot be cancelled because it is "
                        + order.getStatus().toString().toLowerCase());
            }
        } else {
            if (order.getStatus() == OrderStatus.SHIPPED
                    || order.getStatus() == OrderStatus.DELIVERED) {
                LOGGER.warn("Cancellation refused for order id={} (admin) due to status={}",
                        order.getOrderId(), order.getStatus());
                throw new InvalidOrderStatusException("Order cannot be cancelled because it is "
                        + order.getStatus().toString().toLowerCase());
            }
        }

        if (order.getStatus() == OrderStatus.PAID) {
            LOGGER.info("Triggering refund for order id={}", order.getOrderId());
            // TODO: call payment provider to process refund
            order.setPaymentStatus(PaymentStatus.REFUNDED);
        }

        order.getItems().forEach(item -> {
            Product product = item.getProduct();
            int newStock = product.getStock() + item.getQuantity();
            product.setStock(newStock);

            LOGGER.debug("Restored stock for product id={} (+{}), newStock={}",
                    product.getProductId(), item.getQuantity(), newStock);
        });

        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());

        LOGGER.info("Order id={} successfully cancelled", order.getOrderId());

        return order;
    }

    /**
     * Generates a unique order reference.
     * <p>
     * Format: {@code ORD-YYYYMM-XXXXXXXX}
     * <ul>
     * <li>YYYYMM: current year and month</li>
     * <li>XXXXXXXX: random uppercase alphanumeric string</li>
     * </ul>
     *
     * @return a unique order reference string
     */
    private String generateOrderReference() {
        String yearMonth = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
        return "ORD-" + yearMonth + "-"
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
