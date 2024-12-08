package com.example.ticketing_system_spring_boot.service;

import com.example.ticketing_system_spring_boot.model.Ticket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class VendorService {
    private final TicketPool ticketPool;
    private final List<Thread> vendorThreads = new ArrayList<>();
    private volatile boolean running = false; // To control thread execution

    @Autowired
    public VendorService(TicketPool ticketPool) {
        this.ticketPool = ticketPool;
    }

    public void startVendors(int vendorCount, int ticketReleaseRate) {
        if (running) {
            throw new IllegalStateException("Vendors are already running.");
        }

        running = true;
        for (int i = 1; i <= vendorCount; i++) {
            Thread vendorThread = new Thread(() -> runVendor(ticketReleaseRate));
            vendorThreads.add(vendorThread);
            vendorThread.start();
        }
    }

    private void runVendor(int ticketReleaseRate) {
        Random random = new Random();
        while (running) {
            try {
                int ticketId = random.nextInt(1000);
                Ticket ticket = new Ticket(ticketId);

                synchronized (ticketPool) {
                    ticketPool.addTicket(ticket);
                }

                System.out.println("Vendor added ticket: " + ticketId);
                Thread.sleep(ticketReleaseRate);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stopVendors() {
        running = false;
        vendorThreads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        vendorThreads.clear();
    }
}
