package com.example.ticketing_system_spring_boot;

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

    @BeforeEach
    public void setup() {
        ConfigurationRepository mockRepository = Mockito.mock(ConfigurationRepository.class);
        Mockito.when(mockRepository.findById(1L))
                .thenReturn(java.util.Optional.empty()); // Use default capacity

        pool = new TicketPool(mockRepository, 10); // 10 is the test capacity
    }

    @Test
    public void testTicketAddition() throws InterruptedException {
        Ticket ticket = new Ticket(1);
        pool.addTicket(ticket);

        assertEquals(1, pool.getAvailableTickets());
    }

    @Test
    public void testExceedingCapacity() throws InterruptedException {
        pool.addTicket(new Ticket(1));
        pool.addTicket(new Ticket(2));

        boolean exceptionThrown = false;
        try {
            pool.addTicket(new Ticket(3)); // This will block or throw
        } catch (InterruptedException e) {
            exceptionThrown = true;
        }
        assertFalse(exceptionThrown);
        assertEquals(2, pool.getAvailableTickets());
    }
}
