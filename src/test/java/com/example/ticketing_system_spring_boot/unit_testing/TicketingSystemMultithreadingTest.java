//package com.example.ticketing_system_spring_boot.unit_testing;
//
//import com.example.ticketing_system_spring_boot.model.Ticket;
//import com.example.ticketing_system_spring_boot.repository.ConfigurationRepository;
//import com.example.ticketing_system_spring_boot.service.ConsumerService;
//import com.example.ticketing_system_spring_boot.service.TicketPool;
//import com.example.ticketing_system_spring_boot.service.VendorService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import java.util.concurrent.atomic.AtomicInteger;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class TicketingSystemMultithreadingTest {
//
//    private TicketPool ticketPool;
//    private VendorService vendorService;
//    private ConsumerService consumerService;
//
//    @BeforeEach
//    void setUp() {
//        ticketPool = Mockito.spy(new TicketPool(Mockito.mock(ConfigurationRepository.class), 50)); // maxCapacity=50
//        vendorService = new VendorService(ticketPool);
//        consumerService = new ConsumerService(ticketPool);
//    }
//
//    @AfterEach
//    void tearDown() {
//        vendorService.stopVendors();
//        consumerService.stopConsumers();
//    }
//
//    @Test
//    void testVendorsAndConsumersMultithreading() throws InterruptedException {
//        int vendorCount = 5;
//        int ticketReleaseRate = 1000; // 100ms per ticket
//        int consumerCount = 5;
//        int retrievalRate = 1500; // 150ms per ticket retrieval
//
//        AtomicInteger ticketsConsumed = new AtomicInteger(0);
//        AtomicInteger ticketsProduced = new AtomicInteger(0);
//
//        // Spy on TicketPool to count tickets produced and consumed
//        Mockito.doAnswer(invocation -> {
//            Ticket ticket = (Ticket) invocation.callRealMethod(); // Cast to Ticket
//            if (ticket != null) {
//                ticketsConsumed.incrementAndGet();
//            }
//            return ticket;
//        }).when(ticketPool).retrieveTicket();
//
//        Mockito.doAnswer(invocation -> {
//            boolean added = (boolean) invocation.callRealMethod();
//            if (added) {
//                ticketsProduced.incrementAndGet();
//            }
//            return added;
//        }).when(ticketPool).addTicket(Mockito.any(Ticket.class));
//
//        // Start vendors and consumers
//        vendorService.startVendors(vendorCount, ticketReleaseRate);
//        consumerService.startConsumers(consumerCount, retrievalRate);
//
//        // Let the simulation run for 3 seconds
//        Thread.sleep(3000);
//
//        // Stop the simulation
//        vendorService.stopVendors();
//        consumerService.stopConsumers();
//
//        // Assertions
//        int remainingTickets = ticketPool.getAvailableTickets();
//        System.out.println("Tickets Produced: " + ticketsProduced.get());
//        System.out.println("Tickets Consumed: " + ticketsConsumed.get());
//        System.out.println("Remaining Tickets in Pool: " + remainingTickets);
//
//        // Validate the ticket counts
//        assertTrue(ticketsProduced.get() > 0, "Tickets should be produced by vendors.");
//        assertTrue(ticketsConsumed.get() > 0, "Tickets should be consumed by consumers.");
//        assertEquals(ticketsProduced.get() - ticketsConsumed.get(), remainingTickets,
//                "Remaining tickets should match produced - consumed.");
//    }
//
//    @Test
//    void testFullPoolCondition() throws InterruptedException {
//        int vendorCount = 1;
//        int ticketReleaseRate = 50; // Fast release rate
//        int consumerCount = 0; // No consumers to test full pool
//
//        vendorService.startVendors(vendorCount, ticketReleaseRate);
//
//        // Let the simulation run until pool is full
//        Thread.sleep(3000);
//
//        vendorService.stopVendors();
//
//        // Assertions
//        assertEquals(50, ticketPool.getAvailableTickets(), "Pool should reach max capacity.");
//    }
//
//    @Test
//    void testEmptyPoolCondition() throws InterruptedException {
//        int vendorCount = 0; // No vendors to add tickets
//        int consumerCount = 1;
//        int retrievalRate = 50; // Fast retrieval rate
//
//        consumerService.startConsumers(consumerCount, retrievalRate);
//
//        // Let the simulation run for 1 second
//        Thread.sleep(1000);
//
//        consumerService.stopConsumers();
//
//        // Assertions
//        assertEquals(0, ticketPool.getAvailableTickets(), "Pool should remain empty with no vendors.");
//    }
//}
