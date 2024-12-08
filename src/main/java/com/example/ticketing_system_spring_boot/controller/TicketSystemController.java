package com.example.ticketing_system_spring_boot.controller;

import com.example.ticketing_system_spring_boot.model.SystemConfiguration;
import com.example.ticketing_system_spring_boot.repository.ConfigurationRepository;
import com.example.ticketing_system_spring_boot.service.TicketingService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/ticketing")
public class TicketSystemController {
    private final TicketingService ticketingService;
    private final ConfigurationRepository configurationRepository;

    @Autowired
    public TicketSystemController(TicketingService ticketingService, ConfigurationRepository configurationRepository) {
        this.ticketingService = ticketingService;
        this.configurationRepository = configurationRepository;
    }
    // Save or Update Configuration
    @PostMapping("/configuration")
    @Transactional
    public ResponseEntity<SystemConfiguration> saveConfiguration(@Valid @RequestBody SystemConfiguration systemConfiguration) {
        Optional<SystemConfiguration> existingConfig = configurationRepository.findSingletonConfiguration();
        if (existingConfig.isPresent()) {
            SystemConfiguration config = existingConfig.get();
            config.setMaxTicketCapacity(systemConfiguration.getMaxTicketCapacity());
            config.setCustomerRetrievalRate(systemConfiguration.getCustomerRetrievalRate());
            config.setTicketReleaseRate(systemConfiguration.getTicketReleaseRate());
            return ResponseEntity.ok(configurationRepository.save(config));
        } else {
            systemConfiguration.setId(1L);
            return ResponseEntity.ok(configurationRepository.save(systemConfiguration));
        }
    }

    // Retrieve Current Configuration
    @GetMapping("/current")
    public SystemConfiguration getCurrentConfiguration() {
        return configurationRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Configuration not found!"));
    }

    @PostMapping("/start")
    public ResponseEntity<String> startSimulation(
            @RequestParam int vendorCount,
            @RequestParam int consumerCount) {
        // Retrieve the configuration from the database
        SystemConfiguration configuration = configurationRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Configuration not found!"));

        // Use the configuration parameters from the database
        int ticketReleaseRate = configuration.getTicketReleaseRate();
        int retrievalRate = configuration.getCustomerRetrievalRate();

        // Pass all necessary parameters to start the simulation
        ticketingService.startSimulation(vendorCount, ticketReleaseRate, consumerCount, retrievalRate);

        return ResponseEntity.ok("Simulation started");
    }

    @PostMapping("/stop")
    public ResponseEntity<String> stopSimulation() {
        ticketingService.stopSimulation();
        return ResponseEntity.ok("Simulation stopped");
    }
}
