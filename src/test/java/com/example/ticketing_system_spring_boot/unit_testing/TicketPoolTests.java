//package com.example.ticketing_system_spring_boot.unit_testing;
//
//import com.example.ticketing_system_spring_boot.model.SystemConfiguration;
//import com.example.ticketing_system_spring_boot.model.Ticket;
//import com.example.ticketing_system_spring_boot.service.TicketPool;
//import com.example.ticketing_system_spring_boot.repository.ConfigurationRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class TicketPoolTests {
//    private TicketPool pool;
//    private ConfigurationRepository configurationRepository;
//
//    @BeforeEach
//    public void setup() {
//        configurationRepository = Mockito.mock(ConfigurationRepository.class);
//
//        SystemConfiguration mockConfig = new SystemConfiguration();
//        mockConfig.setId(1L);
//        mockConfig.setMaxTicketCapacity(2);
//        Mockito.when(configurationRepository.findById(1L))
//                .thenReturn(java.util.Optional.of(mockConfig));
//
//        pool = new TicketPool(configurationRepository, 100); // Default fallback = 100
//    }
//
//    @Test
//    public void testTicketAddition() {
//        Ticket ticket = new Ticket(1);
//        boolean added = pool.addTicket(ticket);
//
//        // Validate addition and pool size
//        assertEquals(1, pool.getAvailableTickets(), "Ticket should be added successfully.");
//        assertTrue(added, "The ticket should be added to the pool.");
//    }
//
//    @Test
//    public void testExceedingCapacity() {
//        pool.addTicket(new Ticket(1));
//        pool.addTicket(new Ticket(2));
//
//        boolean added = pool.addTicket(new Ticket(3)); // Exceed capacity
//
//        assertFalse(added, "Adding a ticket beyond capacity should fail.");
//        assertEquals(2, pool.getAvailableTickets(), "Pool size should remain at max capacity.");
//    }
//}
