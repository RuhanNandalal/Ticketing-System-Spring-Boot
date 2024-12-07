package com.example.ticketing_system_spring_boot.service;

import com.example.ticketing_system_spring_boot.model.Ticket;
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
    public ConsumerService(TicketPool ticketPool) {
        this.ticketPool = ticketPool;
    }

    public void startConsumers(int consumerCount, int retrievalRate) {
        if (running) {
            throw new IllegalStateException("Consumers are already running.");
        }

        running = true;
        for (int i = 1; i <= consumerCount; i++) {
            Thread consumerThread = new Thread(() -> runConsumer(retrievalRate));
            consumerThreads.add(consumerThread);
            consumerThread.start();
        }
    }

    private void runConsumer(int retrievalRate) {
        while (running) {
            try {
                synchronized (ticketPool) {
                    Ticket ticket = ticketPool.retrieveTicket();
                    if (ticket != null) {
                        ticket.markAsSold();
                        System.out.println("Consumer purchased ticket: " + ticket.getTicketId());
                    }
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
        consumerThreads.forEach(Thread::interrupt);
        consumerThreads.clear();
    }
}
