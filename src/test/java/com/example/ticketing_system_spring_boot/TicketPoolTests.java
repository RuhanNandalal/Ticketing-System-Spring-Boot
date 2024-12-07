package com.example.ticketing_system_spring_boot;

import com.example.ticketing_system_spring_boot.model.SystemConfiguration;
import com.example.ticketing_system_spring_boot.model.Ticket;
import com.example.ticketing_system_spring_boot.service.TicketPool;
import com.example.ticketing_system_spring_boot.repository.ConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TicketPoolTests {
    private TicketPool pool;
    private ConfigurationRepository configurationRepository;

    @BeforeEach
    public void setup() {
        configurationRepository = Mockito.mock(ConfigurationRepository.class);
        Mockito.when(configurationRepository.findById(1L))
                .thenReturn(java.util.Optional.of(new SystemConfiguration(1L, 2))); // Set maxCapacity = 2

        // Initialize the TicketPool with the mocked ConfigurationRepository
        pool = new TicketPool(configurationRepository, 2);
    }

    @Test
    public void testTicketAddition() throws InterruptedException {
        Ticket ticket = new Ticket(1);
        pool.addTicket(ticket);

        assertEquals(1, pool.getAvailableTickets());
    }

    @Test
    public void testExceedingCapacity() throws InterruptedException {
        TicketPool pool = new TicketPool(configurationRepository, 2); // Max capacity = 2

        // Add tickets up to capacity
        pool.addTicket(new Ticket(1));
        pool.addTicket(new Ticket(2));

        // Attempt to add one more ticket
        boolean added = pool.addTicket(new Ticket(3));

        // Verify that adding beyond capacity is not allowed
        assertFalse(added);
    }
}
