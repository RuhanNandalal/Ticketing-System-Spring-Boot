package com.example.ticketing_system_spring_boot.service;

import com.example.ticketing_system_spring_boot.model.SystemConfiguration;
import com.example.ticketing_system_spring_boot.model.Ticket;
import com.example.ticketing_system_spring_boot.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

@Service
public class TicketPool {
    private static final Logger logger = LoggerFactory.getLogger(TicketPool.class);

    private final LinkedList<Ticket> tickets = new LinkedList<>();
    private final int maxCapacity;

    // Initializing default value for max ticket capacity if a value not found in the database
    @Autowired
    public TicketPool(ConfigurationRepository configurationRepository,
                      @Value("${ticket.pool.maxCapacity:100}") int defaultMaxCapacity) {
        // Attempt to fetch from the database, fallback to default
        this.maxCapacity = configurationRepository.findById(1L)
                .map(SystemConfiguration::getMaxTicketCapacity)
                .orElseGet(() -> {
                    logger.warn("Configuration with ID 1 not found. Using default value: {}", defaultMaxCapacity);
                    return defaultMaxCapacity;
                });

        logger.info("Initialized TicketPool with max capacity: {}", this.maxCapacity);
    }

    public boolean addTicket(Ticket ticket) {
        synchronized (tickets) {
            if (tickets.size() >= maxCapacity) {
                logger.warn("Failed to add ticket {}: pool is at maximum capacity", ticket.getTicketId());
                return false; // Reject ticket if the pool is full
            }
            tickets.add(ticket);
            tickets.notifyAll(); // Notify waiting consumers
            logger.info("Added ticket {} to the pool. Current size: {}", ticket.getTicketId(), tickets.size());
            return true;
        }
    }

    public Ticket retrieveTicket() {
        synchronized (tickets) {
            while (tickets.isEmpty()) {
                try {
                    logger.info("No tickets available. Waiting for tickets to be added...");
                    tickets.wait(); // Wait until a ticket is available
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Thread interrupted while waiting for tickets", e);
                    return null;
                }
            }
            Ticket ticket = tickets.removeFirst();
            tickets.notifyAll(); // Notify waiting vendors
            logger.info("Retrieved ticket {} from the pool. Remaining size: {}", ticket.getTicketId(), tickets.size());
            return ticket;
        }
    }

    public synchronized int getAvailableTickets() {
        int size = tickets.size();
        logger.debug("Checking available tickets: {}", size);
        return size;
    }
}
