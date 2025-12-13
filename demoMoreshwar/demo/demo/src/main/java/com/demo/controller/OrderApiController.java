package com.demo.controller;

import com.demo.dto.HistoryOrderDto; 
import com.demo.dto.OrderListDto;
import com.demo.dto.OrderRequest;
import com.demo.model.Order;
import com.demo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping; // --- NEW IMPORT ---
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.PatchMapping; 
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam; 
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate; 
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

    @Autowired
    private OrderService orderService; // This is the "brain"

    // =================================================================
    // --- NEW ENDPOINTS FOR "PENDING ORDER" WORKFLOW ---
    // =================================================================

    /**
     * This is called by the *first* "+ Add" button click.
     * It creates a new, empty order with "PENDING" status.
     * It returns the new Order object (so the JS can get the orderId).
     */
    @PostMapping("/create-pending")
    public ResponseEntity<Order> createPendingOrder() {
        try {
            Order pendingOrder = orderService.createPendingOrder();
            // Return the full Order object, including the new ID
            return new ResponseEntity<>(pendingOrder, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This is called by the "Order" button.
     * It finds the "PENDING" order and updates it with all the final data.
     */
    @PatchMapping("/submit/{orderId}")
    public ResponseEntity<String> submitPendingOrder(
            @PathVariable Long orderId,
            @RequestBody OrderRequest orderRequest) {
        try {
            Order savedOrder = orderService.submitPendingOrder(orderId, orderRequest);
            String responseMessage = "Order created successfully with ID: " + savedOrder.getOrderId();
            return new ResponseEntity<>(responseMessage, HttpStatus.OK); // Use OK for updates

        } catch (RuntimeException e) {
            e.printStackTrace(); 
            return new ResponseEntity<>("Error creating order: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * This is called by the "Cancel" button.
     * It deletes the "PENDING" order (and any associated items).
     */
    @DeleteMapping("/cancel/{orderId}")
    public ResponseEntity<String> cancelPendingOrder(@PathVariable Long orderId) {
        try {
            orderService.cancelPendingOrder(orderId);
            return new ResponseEntity<>("Pending order " + orderId + " cancelled.", HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error cancelling order: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    // =================================================================
    // --- ENDPOINTS FOR THE OTHER DASHBOARDS (No changes here) ---
    // =================================================================

    /**
     * This GET endpoint fetches the list of orders based on their status.
     * e.g., GET /api/orders?status=ON_PROCESS
     */
    @GetMapping
    public ResponseEntity<List<OrderListDto>> getOrdersByStatus(@RequestParam String status) {
        try {
            List<OrderListDto> orders = orderService.getOrdersByStatus(status);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * This PATCH endpoint updates the status of a single order.
     * e.g., PATCH /api/orders/1001/status
     */
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<String> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, String> body) {
        
        try {
            String newStatus = body.get("status");
            if (newStatus == null || newStatus.trim().isEmpty()) {
                return new ResponseEntity<>("Status is required.", HttpStatus.BAD_REQUEST);
            }

            orderService.updateOrderStatus(orderId, newStatus);
            String responseMessage = "Order " + orderId + " updated to " + newStatus;
            return new ResponseEntity<>(responseMessage, HttpStatus.OK);

        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error updating order: " + e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * This GET endpoint fetches the list of "PAID" orders for the history page.
     * e.g., GET /api/orders/history
     */
    @GetMapping("/history")
    public ResponseEntity<List<HistoryOrderDto>> getHistoryOrders(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        try {
            List<HistoryOrderDto> orders = orderService.getHistoryOrders(date);
            return new ResponseEntity<>(orders, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

