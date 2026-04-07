package fr.afpa.pompey.APIEcommerce.controller;

import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import fr.afpa.pompey.APIEcommerce.model.Order;
import fr.afpa.pompey.APIEcommerce.service.OrderService;
import jakarta.validation.Valid;

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
            existing.setItems(null);
            existing.setCreatedAt(null);
            return orderService.saveOrder(existing);
        }
        return null;
    }

    @DeleteMapping("/order/{id}")
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
    }

}
