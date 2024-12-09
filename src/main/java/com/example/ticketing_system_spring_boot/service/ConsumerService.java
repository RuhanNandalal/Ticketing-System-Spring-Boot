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
                Ticket ticket = ticketPool.retrieveTicket();
                if (ticket != null) {
                    ticket.markAsSold();
                    System.out.println("Consumer " + consumerId + " purchased ticket: " + ticket.getTicketId());

                    TicketTransaction transaction = new TicketTransaction();
                    transaction.setTicketId(ticket.getTicketId());
                    transaction.setConsumerId(consumerId);
                    transaction.setVendorId(ticket.getVendorId());
                    ticketTransactionRepository.save(transaction);
                }
                Thread.sleep(retrievalRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
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
