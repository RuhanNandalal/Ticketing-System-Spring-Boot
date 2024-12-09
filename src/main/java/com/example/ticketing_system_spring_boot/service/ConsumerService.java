package com.example.ticketing_system_spring_boot.service;

import com.example.ticketing_system_spring_boot.model.Consumer;
import com.example.ticketing_system_spring_boot.model.Ticket;
import com.example.ticketing_system_spring_boot.model.TicketTransaction;
import com.example.ticketing_system_spring_boot.repository.ConsumerRepository;
import com.example.ticketing_system_spring_boot.repository.TicketTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConsumerService {
    private final TicketPool ticketPool;
    private final List<Thread> consumerThreads = new ArrayList<>();
    private volatile boolean running = false;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private TicketTransactionRepository ticketTransactionRepository;

    @Autowired
    public ConsumerService(TicketPool ticketPool) {
        this.ticketPool = ticketPool;
    }

    public void startConsumers(int consumerCount, int retrievalRate) {
        if (running) {
            throw new IllegalStateException("Consumers are already running.");
        }

        running = true;

        for (int i = 1; i <= consumerCount; i++) {
            // Create and save the consumer in the database with a unique name
            Consumer consumer = new Consumer();
            consumer.setName("Consumer-" + i + "-" + Thread.currentThread().getId()); // Make name unique by appending thread ID
            consumerRepository.save(consumer);

            // Pass the consumer ID to the consumer thread
            Long consumerId = consumer.getId();

            Thread consumerThread = new Thread(() -> runConsumer(retrievalRate, consumerId));
            consumerThreads.add(consumerThread);
            consumerThread.start();
        }
    }

    private void runConsumer(int retrievalRate, Long consumerId) {
        while (running) {
            try {
                synchronized (ticketPool) {
                    Ticket ticket = ticketPool.retrieveTicket(); // Retrieve from pool
                    if (ticket != null) {
                        ticket.markAsSold();
                        System.out.println("Consumer " + consumerId + " purchased ticket: " + ticket.getTicketId());

                        // Create transaction and save to the database
                        TicketTransaction transaction = new TicketTransaction();
                        transaction.setTicketId(ticket.getTicketId());
                        transaction.setConsumerId(consumerId);
                        transaction.setVendorId(ticket.getVendorId());  // Set vendorId here
                        ticketTransactionRepository.save(transaction);
                    }
                }
                Thread.sleep(retrievalRate); // Wait based on retrieval rate
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stopConsumers() {
        running = false;
        consumerThreads.forEach(thread -> {
            try {
                thread.join(); // Wait for threads to terminate
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumerThreads.clear();
    }
}
