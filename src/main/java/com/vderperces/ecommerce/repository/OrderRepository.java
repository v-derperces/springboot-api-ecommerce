package com.vderperces.ecommerce.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vderperces.ecommerce.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByUser_UserId(Long userId);
}
