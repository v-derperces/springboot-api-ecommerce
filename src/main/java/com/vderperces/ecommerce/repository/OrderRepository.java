package com.vderperces.ecommerce.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.vderperces.ecommerce.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    boolean existsByUser_UserId(Long userId);

    /**
     * Finds all orders for a user paginated and sorted.
     *
     * @param email the email of the user
     * @param pageable pagination and sorting parameters
     * @return page of orders for the user
     */
    Page<Order> findByUser_Email(String email, Pageable pageable);

    Optional<Order> findByOrderIdAndUser_Email(Long id, String email);
}
