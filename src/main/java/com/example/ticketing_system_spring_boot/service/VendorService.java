package com.example.ticketing_system_spring_boot.service;

import com.example.ticketing_system_spring_boot.model.Ticket;
import com.example.ticketing_system_spring_boot.model.Vendor;
import com.example.ticketing_system_spring_boot.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class VendorService {
    private final TicketPool ticketPool;
    private final List<Thread> vendorThreads = new ArrayList<>();
    private volatile boolean running = false;
    @Autowired
    private VendorRepository vendorRepository;

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
            // Create and save the vendor in the database
            Vendor vendor = new Vendor();
            vendor.setName("Vendor-" + i);
            vendorRepository.save(vendor);

            // Pass the vendor ID to the vendor thread
            Long vendorId = vendor.getId();

            Thread vendorThread = new Thread(() -> runVendor(ticketReleaseRate, vendorId));
            vendorThreads.add(vendorThread);
            vendorThread.start();
        }
    }

    private void runVendor(int ticketReleaseRate, Long vendorId) {
        while (running) {
            try {
                int ticketId = new Random().nextInt(1000); // Generate ticket ID
                Ticket ticket = new Ticket(ticketId, vendorId); // Pass vendorId when creating the ticket

                synchronized (ticketPool) {
                    boolean added = ticketPool.addTicket(ticket); // Add to pool
                    if (added) {
                        System.out.println("Vendor " + vendorId + " added ticket: " + ticketId);

                        // Save vendor-ticket association to the database
                        Vendor vendor = vendorRepository.findById(vendorId).orElseThrow();
                        vendor.setTicketId(ticketId);
                        vendorRepository.save(vendor);
                    }
                }
                Thread.sleep(ticketReleaseRate); // Wait based on release rate
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
