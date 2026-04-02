package fr.afpa.pompey.APIEcommerce.service;

import fr.afpa.pompey.APIEcommerce.model.Orderline;
import fr.afpa.pompey.APIEcommerce.repository.OrderlineRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrderlineService {

    private final OrderlineRepository orderlineRepository;

    public OrderlineService(OrderlineRepository orderlineRepository) {
        this.orderlineRepository = orderlineRepository;
    }

    public Iterable<Orderline> getLines() {
        return orderlineRepository.findAll();
    }

    public Optional<Orderline> getLine(Long id) {
        return orderlineRepository.findById(id);
    }

    public Orderline saveLine(Orderline orderline) {
        return orderlineRepository.save(orderline);
    }

    public void deleteLine(Long id) {
        orderlineRepository.deleteById(id);
    }
}
