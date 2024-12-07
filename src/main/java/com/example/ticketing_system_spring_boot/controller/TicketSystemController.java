package com.example.ticketing_system_spring_boot.controller;

import com.example.ticketing_system_spring_boot.service.TicketingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ticketing")
public class TicketSystemController {
    private final TicketingService ticketingService;

    @Autowired
    public TicketSystemController(TicketingService ticketingService) {
        this.ticketingService = ticketingService;
    }

    @PostMapping("/start")
    public ResponseEntity<String> startSimulation(
            @RequestParam int vendorCount,
            @RequestParam int ticketReleaseRate,
            @RequestParam int consumerCount,
            @RequestParam int retrievalRate) {
        ticketingService.startSimulation(vendorCount, ticketReleaseRate, consumerCount, retrievalRate);
        return ResponseEntity.ok("Simulation started");
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopSimulation() {
        ticketingService.stopSimulation();
        return ResponseEntity.ok("Simulation stopped");
    }
}
