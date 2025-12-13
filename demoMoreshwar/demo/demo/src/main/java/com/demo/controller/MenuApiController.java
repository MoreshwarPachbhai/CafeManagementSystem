package com.demo.controller;

import com.demo.dto.MenuItemRequest;
import com.demo.service.MenuService;
import com.demo.model.MenuItem;
import com.demo.repository.MenuItemRepository; 
import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api") 
public class MenuApiController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private MenuService menuService;

    @GetMapping("/menu-items/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        // This now returns the SORTED list from the Repository query
        return new ResponseEntity<>(menuService.getAllCategories(), HttpStatus.OK);
    }

    /**
     * FIX APPLIED: Now uses the Service to get items sorted A-Z
     */
    @GetMapping("/menu-items")
    public List<MenuItem> getMenuItemsByCategory(@RequestParam String category) {
        
        System.out.println("Fetching sorted items for category: " + category);

        // FIX: Changed from direct repo call to Service method to ensure Sorting (A-Z)
        return menuService.getMenuItemsByCategory(category);
    }

    @GetMapping("/menu-items/search")
    public List<MenuItem> searchMenuItems(@RequestParam("q") String query) {
        System.out.println("Searching for items in DATABASE like: " + query);
        return menuItemRepository.findByNameContainingIgnoreCase(query);
    }

    // =================================================================
    // --- SETTINGS PAGE ENDPOINTS (Unchanged) ---
    // =================================================================

    @PostMapping("/menu-items/add")
    public ResponseEntity<String> addMenuItem(@RequestBody MenuItemRequest request) {
        try {
            MenuItem newItem = menuService.addMenuItem(request);
            return new ResponseEntity<>("Item added successfully with ID: " + newItem.getItemId(), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/menu-items/all")
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        return new ResponseEntity<>(menuService.getAllMenuItems(), HttpStatus.OK);
    }

    @DeleteMapping("/menu-items/delete/{id}")
    public ResponseEntity<String> deleteMenuItem(@PathVariable Long id) {
        try {
            menuService.deleteMenuItem(id);
            return new ResponseEntity<>("Item deleted successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/menu-items/update/{id}")
    public ResponseEntity<String> updateMenuItem(@PathVariable Long id, @RequestBody MenuItemRequest request) {
        System.out.println("Received UPDATE request for Item ID: " + id); 
        try {
            menuService.updateMenuItem(id, request);
            return new ResponseEntity<>("Menu item updated successfully", HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}