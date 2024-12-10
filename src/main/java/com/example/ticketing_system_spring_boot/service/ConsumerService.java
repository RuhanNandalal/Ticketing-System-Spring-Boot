package com.example.ticketing_system_spring_boot.service;

import com.example.ticketing_system_spring_boot.model.Consumer;
import com.example.ticketing_system_spring_boot.model.TicketTransaction;
import com.example.ticketing_system_spring_boot.repository.ConsumerRepository;
import com.example.ticketing_system_spring_boot.repository.TicketTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(TicketPool.class);

    private final TicketPool ticketPool;
    private final List<Thread> consumerThreads = new ArrayList<>();
    private volatile boolean running = false;

    @Autowired
    private ConsumerRepository consumerRepository;

    @Autowired
    private TicketTransactionRepository ticketTransactionRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

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
            Consumer consumer = new Consumer();
            consumer.setName("Consumer-" + i + "-" + System.nanoTime());
            consumerRepository.save(consumer);

            Long consumerId = consumer.getId();

            Thread consumerThread = new Thread(() -> runConsumer(retrievalRate, consumerId));
            consumerThreads.add(consumerThread);
            consumerThread.start();
        }
    }

    private void runConsumer(int retrievalRate, Long consumerId) {
        while (running) {
            try {
                Map<String, Object> ticketDetails = ticketPool.retrieveTicketDetails();
                if (ticketDetails == null) {
                    logger.warn("Error: Retrieved ticketDetails is null!");
                    continue;
                }

                // Safely convert ticketId and vendorId to Long
                Long ticketId = ((Number) ticketDetails.get("ticketId")).longValue();
                Long vendorId = ((Number) ticketDetails.get("vendorId")).longValue();

                if (ticketId == null || ticketId == 0 || vendorId == null) {
                    System.err.println("Error: Invalid ticketDetails. ticketId: " + ticketId + ", vendorId: " + vendorId);
                    continue;
                }

                System.out.println("Consumer " + consumerId + " purchased ticket: " + ticketId);

                TicketTransaction transaction = new TicketTransaction();
                transaction.setTicketId(ticketId.intValue());
                transaction.setConsumerId(consumerId);
                transaction.setVendorId(vendorId);
                ticketTransactionRepository.save(transaction);

                messagingTemplate.convertAndSend("/topic/simulation", Map.of(
                        "type", "ticketBought",
                        "consumerId", consumerId,
                        "ticketId", ticketId
                ));

                Thread.sleep(retrievalRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("Unexpected error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void stopConsumers() {
        running = false;
        synchronized (ticketPool) {
            ticketPool.notifyAll(); // Wake up all waiting threads
        }
        consumerThreads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumerThreads.clear();
    }
}
