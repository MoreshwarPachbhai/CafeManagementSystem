package com.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cust_id")
    private Long custId;

    @Column(name = "name")
    private String name;

    @Column(name = "contact")
    private String contact;

    // --- Constructors ---
    
    // No-argument constructor required by JPA
    public Customer() {
    }

    // Constructor for creating a new customer
    public Customer(String name, String contact) {
        this.name = name;
        this.contact = contact;
    }

    // --- Getters and Setters ---
    // Required by JPA

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}

