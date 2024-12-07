package com.example.ticketing_system_spring_boot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Database-generated ID

    private int ticketId; // Custom ticket ID for business logic
    private boolean available = true; // Default state

    public int getTicketId() {
        return ticketId;
    }

    // Constructor accepting ticketId
    public Ticket(int ticketId) {
        this.ticketId = ticketId;
    }
    // Method to mark the ticket as sold
    public void markAsSold () {
        this.available = false;
    }
}
