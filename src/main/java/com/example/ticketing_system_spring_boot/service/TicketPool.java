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
    private final ConfigurationRepository configurationRepository;
    private volatile int maxCapacity; // Ensures visibility across threads

    @Autowired
    public TicketPool(ConfigurationRepository configurationRepository) {
        this.configurationRepository = configurationRepository;
        this.maxCapacity = fetchMaxCapacity();
    }

    private int fetchMaxCapacity() {
        return configurationRepository.findById(1L)
                .map(SystemConfiguration::getMaxTicketCapacity)
                .orElse(100); // Default value
    }

    /**
     * This method should be called when starting a simulation to fetch the latest configuration.
     */
    public synchronized void initializeSimulation() {
        this.maxCapacity = fetchMaxCapacity();
        tickets.clear(); // Optionally clear tickets if a new simulation implies a fresh start.
        logger.info("Simulation initialized with maxCapacity: {}", this.maxCapacity);
    }

    public boolean addTicket(Ticket ticket) {
        synchronized (tickets) {
            if (tickets.size() >= maxCapacity) {
                logger.warn("Failed to add ticket {}: pool is at maximum capacity", ticket.getTicketId());
                return false;
            }
            tickets.add(ticket);
            tickets.notifyAll();
            logger.info("Added ticket {} to the pool. Current size: {}", ticket.getTicketId(), tickets.size());
            return true;
        }
    }

    public Ticket retrieveTicket() {
        synchronized (tickets) {
            while (tickets.isEmpty()) {
                try {
                    logger.info("No tickets available. Waiting for tickets to be added...");
                    tickets.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Thread interrupted while waiting for tickets", e);
                    return null;
                }
            }
            Ticket ticket = tickets.removeFirst();
            tickets.notifyAll();
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
