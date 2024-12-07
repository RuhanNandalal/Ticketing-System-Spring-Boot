package com.example.ticketing_system_spring_boot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class SystemConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private int maxTicketCapacity;
    private int customerRetrievalRate;
    private int ticketReleaseRate;

    public SystemConfiguration() {
    }

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }
    public SystemConfiguration(Long id, int maxTicketCapacity) {
        this.id = id;
        this.maxTicketCapacity = maxTicketCapacity;
    }
}
