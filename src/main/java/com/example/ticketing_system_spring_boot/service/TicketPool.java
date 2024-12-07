package com.example.ticketing_system_spring_boot.service;

import com.example.ticketing_system_spring_boot.model.SystemConfiguration;
import com.example.ticketing_system_spring_boot.model.Ticket;
import com.example.ticketing_system_spring_boot.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class TicketPool {
    private final LinkedList<Ticket> tickets = new LinkedList<>();
    private final int maxCapacity;

    //Initializing default value for max ticket capacity if a value not found in the database
    @Autowired
    public TicketPool(ConfigurationRepository configurationRepository,
                      @Value("${ticket.pool.maxCapacity:100}") int defaultMaxCapacity) {
        // Attempt to fetch from the database, fallback to default
        this.maxCapacity = configurationRepository.findById(1L)
                .map(SystemConfiguration::getMaxTicketCapacity)
                .orElse(defaultMaxCapacity);
    }

    public synchronized void addTicket(Ticket ticket) throws InterruptedException {
        while (tickets.size() >= maxCapacity) {
            wait(); // Wait until there's space in the pool
        }
        tickets.add(ticket);
        notifyAll(); // Notify waiting consumers
    }

    public synchronized Ticket retrieveTicket() throws InterruptedException {
        while (tickets.isEmpty()) {
            wait(); // Wait until a ticket is available
        }
        Ticket ticket = tickets.removeFirst();
        notifyAll(); // Notify waiting vendors
        return ticket;
    }

    public synchronized int getAvailableTickets() {
        return tickets.size();
    }
}
