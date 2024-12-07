package com.example.ticketing_system_spring_boot.repository;

import com.example.ticketing_system_spring_boot.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT t from Ticket t WHERE t.available = true")
    List<Ticket> findAvailableTickets(Pageable pageable);
}
