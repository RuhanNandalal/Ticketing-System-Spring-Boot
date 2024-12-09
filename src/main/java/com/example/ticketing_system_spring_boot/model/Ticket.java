package com.example.ticketing_system_spring_boot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Database-generated ID
    private int ticketId; // Custom ticket ID for business logic
    private boolean available = true; // Default state
    private Long vendorId;  // Add this field to associate vendor with ticket

    // Constructor accepting ticketId
    public Ticket(int ticketId, Long vendorId) {
        this.ticketId = ticketId;
        this.vendorId = vendorId;
    }

    // Method to mark the ticket as sold
    public void markAsSold () {
        this.available = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }
}
