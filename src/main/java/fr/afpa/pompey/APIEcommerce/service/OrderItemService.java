package fr.afpa.pompey.APIEcommerce.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import fr.afpa.pompey.APIEcommerce.model.OrderItem;
import fr.afpa.pompey.APIEcommerce.repository.OrderItemRepository;

@Service
public class OrderItemService {

    private final OrderItemRepository orderItemRepository;

    public OrderItemService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    public Iterable<OrderItem> getLines() {
        return orderItemRepository.findAll();
    }

    public Optional<OrderItem> getLine(Long id) {
        return orderItemRepository.findById(id);
    }

    public OrderItem saveLine(OrderItem orderItem) {
        return orderItemRepository.save(orderItem);
    }

    public void deleteLine(Long id) {
        orderItemRepository.deleteById(id);
    }
}
