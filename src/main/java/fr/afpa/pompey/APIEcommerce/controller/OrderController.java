package fr.afpa.pompey.APIEcommerce.controller;

import fr.afpa.pompey.APIEcommerce.model.Order;
import fr.afpa.pompey.APIEcommerce.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order")
    public Order createOrder(@Valid @RequestBody Order order) {
        return orderService.saveOrder(order);
    }

    @GetMapping("/orders")
    public Iterable<Order> getOrders() {
        return orderService.getOrders();
    }

    @GetMapping("/order/{id}")
    public Order getOrder(@PathVariable Long id) {
        Optional<Order> c = orderService.getOrder(id);
        return c.orElse(null);
    }

    @PutMapping("/order/{id}")
    public Order updateOrder(@Valid @RequestBody Order order, @PathVariable Long id) {
        Optional<Order> existingOpt = orderService.getOrder(id);
        if (existingOpt.isPresent()) {
            Order existing = existingOpt.get();
            existing.setStatus(order.getStatus());

            if (order.getUser() != null) {
                existing.setUser(order.getUser());
            }
            existing.setStatus(order.getStatus());
            existing.setOrderlines(order.getOrderlines());
            existing.setOrderDate(order.getOrderDate());
            return orderService.saveOrder(existing);
        }
        return null;
    }

    @DeleteMapping("/order/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

}
