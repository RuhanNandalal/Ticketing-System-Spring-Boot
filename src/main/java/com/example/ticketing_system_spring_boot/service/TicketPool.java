package com.example.ticketing_system_spring_boot.service;

import com.example.ticketing_system_spring_boot.model.SystemConfiguration;
import com.example.ticketing_system_spring_boot.model.Ticket;
import com.example.ticketing_system_spring_boot.repository.ConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.LinkedList;
import java.util.Map;

@Service
public class TicketPool {
    private static final Logger logger = LoggerFactory.getLogger(TicketPool.class);

    private final LinkedList<Ticket> tickets = new LinkedList<>();
    private final ConfigurationRepository configurationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private volatile int maxCapacity;

    @Autowired
    public TicketPool(ConfigurationRepository configurationRepository, SimpMessagingTemplate messagingTemplate) {
        this.configurationRepository = configurationRepository;
        this.messagingTemplate = messagingTemplate;
        this.maxCapacity = fetchMaxCapacity();
    }

    private int fetchMaxCapacity() {
        return configurationRepository.findById(1L)
                .map(SystemConfiguration::getMaxTicketCapacity)
                .orElse(100);
    }

    public synchronized void initializeSimulation() {
        this.maxCapacity = fetchMaxCapacity();
        tickets.clear();
        logger.info("Simulation initialized with maxCapacity: {}", this.maxCapacity);
    }

    public boolean addTicket(Ticket ticket) {
        synchronized (tickets) {
            if (ticket == null || ticket.getTicketId() <= 0 || ticket.getVendorId() == null) {
                logger.error("Cannot add invalid ticket: {}", ticket);
                return false;
            }

            if (tickets.size() >= maxCapacity) {
                logger.warn("Failed to add ticket {}: pool is at maximum capacity", ticket.getTicketId());
                return false;
            }

            tickets.add(ticket);
            tickets.notifyAll();

            messagingTemplate.convertAndSend("/topic/simulation", Map.of(
                    "type", "ticketAdded",
                    "vendorId", ticket.getVendorId(),
                    "ticketId", ticket.getTicketId()
            ));

            logger.info("Added ticket {} to the pool. Current size: {}", ticket.getTicketId(), tickets.size());
            return true;
        }
    }

    public Map<String, Object> retrieveTicketDetails() {
        synchronized (tickets) {
            while (tickets.isEmpty()) {
                try {
                    logger.info("No tickets available. Waiting for tickets to be added...");
                    tickets.wait(5000);
                    if (tickets.isEmpty()) {
                        logger.warn("Timeout waiting for tickets.");
                        return null;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error("Thread interrupted while waiting for tickets", e);
                    return null;
                }
            }

            Ticket ticket = tickets.removeFirst();
            tickets.notifyAll();

            if (ticket == null || ticket.getTicketId() <= 0 || ticket.getVendorId() == null) {
                logger.error("Invalid ticket retrieved: {}", ticket);
                return null;
            }

            Map<String, Object> detailedTicketInfo = Map.of(
                    "ticketId", ticket.getTicketId(),
                    "vendorId", ticket.getVendorId()
            );

            messagingTemplate.convertAndSend("/topic/simulation", detailedTicketInfo);

            logger.info("Retrieved detailed ticket info {} from the pool. Remaining size: {}", detailedTicketInfo, tickets.size());
            return detailedTicketInfo;
        }
    }

    public synchronized int getAvailableTickets() {
        return tickets.size();
    }
}
