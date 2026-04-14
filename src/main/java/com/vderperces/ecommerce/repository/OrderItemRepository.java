package com.vderperces.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vderperces.ecommerce.model.OrderItem;
import com.vderperces.ecommerce.model.Product;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    boolean existsByProduct(Product product);
}
