package com.demo.service;

import com.demo.dto.MenuItemRequest;
import com.demo.model.MenuItem;
import com.demo.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class MenuService {

    @Autowired
    private MenuItemRepository menuItemRepository;

    /**
     * Adds a new item to the menu.
     */
    @Transactional
    public MenuItem addMenuItem(MenuItemRequest request) {
        MenuItem newItem = new MenuItem();
        newItem.setName(request.getName());
        newItem.setPrice(request.getPrice());
        newItem.setCategory(request.getCategory());
        return menuItemRepository.save(newItem);
    }

    public List<MenuItem> getAllMenuItems() {
        return menuItemRepository.findAll();
    }

    @Transactional
    public void deleteMenuItem(Long itemId) {
        if (!menuItemRepository.existsById(itemId)) {
            throw new RuntimeException("Menu item not found with ID: " + itemId);
        }
        menuItemRepository.deleteById(itemId);
    }

    @Transactional
    public MenuItem updateMenuItem(Long itemId, MenuItemRequest request) {
        MenuItem item = menuItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found with ID: " + itemId));

        item.setName(request.getName());
        item.setPrice(request.getPrice());
        item.setCategory(request.getCategory());

        return menuItemRepository.save(item);
    }

    // --- FIX FOR JUMBLED LISTS ---

    // 1. Categories (Sorted by the Repository Query)
    public List<String> getAllCategories() {
        return menuItemRepository.findAllCategories();
    }

    // 2. Items in a Category (Sorted Alphabetically)
    public List<MenuItem> getMenuItemsByCategory(String category) {
        // This calls the new method we added to MenuItemRepository
        return menuItemRepository.findByCategoryOrderByNameAsc(category);
    }
}