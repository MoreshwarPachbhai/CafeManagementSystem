package com.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "menu_items") // The exact name of your table in PostgreSQL
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id") // The name of your primary key column
    private Long itemId;

    @Column(name = "name") // The name of your item name column
    private String name;

    @Column(name = "price") // The name of your price column
    private double price;
    
    @Column(name = "category") // The name of your category column
    private String category;

    // --- Constructors ---
    public MenuItem() {
        // Default constructor required by JPA
    }

    // --- Getters and Setters ---
    // These are required for JPA and JSON serialization

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

