package com.demo.repository;

import com.demo.model.OrderItem;
import com.demo.model.OrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // --- NEW IMPORT ---
import org.springframework.data.jpa.repository.Query; // --- NEW IMPORT ---
import org.springframework.data.repository.query.Param; // --- NEW IMPORT ---
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, OrderItemId> {
    
    /**
     * --- NEW METHOD FOR CANCELLING ORDERS ---
     * This adds a way to delete all items linked to an orderId
     * in a single, efficient query.
     */
    @Modifying // Tells Spring this is a DELETE/UPDATE query
    @Query("DELETE FROM OrderItem oi WHERE oi.order.orderId = :orderId")
    void deleteByOrderId(@Param("orderId") Long orderId);

}

