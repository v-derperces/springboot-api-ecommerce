package fr.afpa.pompey.APIEcommerce.controller;

import fr.afpa.pompey.APIEcommerce.model.Orderline;
import fr.afpa.pompey.APIEcommerce.service.OrderlineService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class OrderlineController {
    private final OrderlineService orderlineService;

    public OrderlineController(OrderlineService orderlineService) {
        this.orderlineService = orderlineService;
    }

    @PostMapping("/orderline")
    public Orderline createOrderline(@Valid @RequestBody Orderline orderline) {
        return orderlineService.saveLine(orderline);
    }

    @GetMapping("/orderlines")
    public Iterable<Orderline> getOrderlines() {
        return orderlineService.getLines();
    }

    @GetMapping("/orderline/{id}")
    public Orderline getOrderline(@PathVariable Long id) {
        Optional<Orderline> l = orderlineService.getLine(id);
        return l.orElse(null);
    }

    @PutMapping("/orderline/{id}")
    public Orderline updateOrderline(@Valid @RequestBody Orderline orderline, @PathVariable Long id) {
        Optional<Orderline> existingOpt = orderlineService.getLine(id);
        if (existingOpt.isPresent()) {
            Orderline existing = existingOpt.get();
            existing.setOrder(orderline.getOrder());
            existing.setQuantity(orderline.getQuantity());
            existing.setUnitPrice(orderline.getUnitPrice());
            existing.setProduct(orderline.getProduct());
            return orderlineService.saveLine(existing);
        }
        return null;
    }

    @DeleteMapping("/orderline/{id}")
    public void deleteOrderline(@PathVariable Long id) {
        orderlineService.deleteLine(id);
    }
}
