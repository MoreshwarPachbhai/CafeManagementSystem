package com.demo.repository;

import com.demo.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate; // --- NEW IMPORT ---
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds all orders by status, newest first.
     * Used for "On Process", "Complete", and "History" (with no date filter).
     */
    List<Order> findByStatusOrderByDateDesc(String status);

    /**
     * --- NEW METHOD FOR HISTORY PAGE ---
     * This is a custom query. It finds all orders with a given status
     * AND where the date part of the "date" timestamp matches the
     * specific date the user picked.
     *
     * FUNCTION('DATE', o.date) extracts just the date (e.g., 2025-11-03)
     * from the full timestamp (e.g., 2025-11-03 14:30:00).
     */
    @Query("SELECT o FROM Order o WHERE o.status = :status AND FUNCTION('DATE', o.date) = :date ORDER BY o.date DESC")
    List<Order> findByStatusAndDate(
        @Param("status") String status,
        @Param("date") LocalDate date
    );
}