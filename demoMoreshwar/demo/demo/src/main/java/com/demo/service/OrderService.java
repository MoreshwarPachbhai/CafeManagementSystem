package com.demo.service;

import com.demo.dto.OrderItemRequest;
import com.demo.dto.OrderRequest;
import com.demo.dto.HistoryOrderDto; 
import com.demo.dto.OrderListDto; 
import com.demo.model.Customer;
import com.demo.model.MenuItem;
import com.demo.model.Order;
import com.demo.model.OrderItem;
import com.demo.model.OrderItemId;
import com.demo.model.Staff;
import com.demo.repository.CustomerRepository;
import com.demo.repository.MenuItemRepository;
import com.demo.repository.OrderItemRepository;
import com.demo.repository.OrderRepository;
import com.demo.repository.StaffRepository; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate; 
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private StaffRepository staffRepository; 

    @Transactional
    public Order createPendingOrder() {
        Order newOrder = new Order();
        newOrder.setStatus("PENDING");
        // We no longer set tableNo to a String, it's an Integer and null by default
        return orderRepository.save(newOrder);
    }

    @Transactional
    public Order submitPendingOrder(Long orderId, OrderRequest orderRequest) {

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        
        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("Order is not in PENDING state and cannot be updated.");
        }

       customerRepository.findByContact(orderRequest.getCustomerContact())
        .orElseGet(() -> customerRepository.save(
                new Customer(
                        orderRequest.getCustomerName(),
                        orderRequest.getCustomerContact()
                )
        ));


        Integer staffId = Integer.parseInt(orderRequest.getStaffId());
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found with ID: " + staffId));

        
        List<Long> itemIds = orderRequest.getItems().stream()
                                         .map(OrderItemRequest::getItemId)
                                         .collect(Collectors.toList());
        
        Map<Long, MenuItem> menuItemMap = menuItemRepository.findAllById(itemIds).stream()
                                          .collect(Collectors.toMap(MenuItem::getItemId, item -> item));

        double finalTotalPrice = 0.0;
        List<OrderItem> orderItemsToSave = new ArrayList<>();

        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            MenuItem menuItem = menuItemMap.get(itemRequest.getItemId());
            if (menuItem == null) {
                throw new RuntimeException("Menu item not found with ID: " + itemRequest.getItemId());
            }

            double lineTotal = menuItem.getPrice() * itemRequest.getQuantity();
            finalTotalPrice += lineTotal;
            
            orderItemsToSave.add(createOrderItem(itemRequest, menuItem, staff, BigDecimal.valueOf(lineTotal)));
        }

        // --- THIS IS THE FIX ---
        // We get the tableNo as a String from the form,
        // and we must parse it into an Integer.
        order.setStaff(staff);
        order.setTableNo(Integer.parseInt(orderRequest.getTableNo())); // <-- FIX
        order.setTotalPrice(BigDecimal.valueOf(finalTotalPrice));
        order.setStatus("ON_PROCESS"); 
        // --- END OF FIX ---

        Order savedOrder = orderRepository.save(order); 

        for (OrderItem orderItem : orderItemsToSave) {
            orderItem.setOrder(savedOrder);
            orderItem.setId(new OrderItemId(orderId, orderItem.getMenuItem().getItemId()));
        }

        orderItemRepository.saveAll(orderItemsToSave);

        return savedOrder;
    }

    @Transactional
    public void cancelPendingOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElse(null); 

        if (order != null && "PENDING".equals(order.getStatus())) {
            orderItemRepository.deleteByOrderId(orderId);
            orderRepository.delete(order);
            System.out.println("Cancelled and deleted pending order ID: " + orderId);
        } else if (order == null) {
            System.out.println("Order " + orderId + " already deleted or never existed.");
        } else {
            throw new RuntimeException("Cannot cancel an order that is already " + order.getStatus());
        }
    }

    private OrderItem createOrderItem(OrderItemRequest itemRequest, MenuItem menuItem, Staff staff, BigDecimal lineTotal) {
        OrderItem orderItem = new OrderItem();
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(itemRequest.getQuantity());
        orderItem.setStaff(staff);
        orderItem.setTotalPrice(lineTotal);
        return orderItem;
    }


    // =================================================================
    // --- METHODS FOR THE DASHBOARDS (No changes here) ---
    // =================================================================

    @Transactional(readOnly = true)
    public List<OrderListDto> getOrdersByStatus(String status) {
        List<Order> orders = orderRepository.findByStatusOrderByDateDesc(status);
        return orders.stream()
                     .map(OrderListDto::new) 
                     .collect(Collectors.toList());
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public List<HistoryOrderDto> getHistoryOrders(LocalDate date) {
        List<Order> orders;
        String status = "PAID"; 
        if (date == null) {
            orders = orderRepository.findByStatusOrderByDateDesc(status);
        } else {
            orders = orderRepository.findByStatusAndDate(status, date);
        }
        return orders.stream()
                     .map(HistoryOrderDto::new)
                     .collect(Collectors.toList());
    }
}

