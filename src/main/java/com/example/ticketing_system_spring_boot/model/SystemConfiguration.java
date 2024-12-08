package com.example.ticketing_system_spring_boot.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
public class SystemConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Max ticket capacity cannot be null")
    @Min(value = 1, message = "Max ticket capacity must be at least 1")
    private int maxTicketCapacity;

    @NotNull(message = "Customer retrieval rate cannot be null")
    @Min(value = 1, message = "Customer retrieval rate must be at least 1")
    private int customerRetrievalRate;

    @NotNull(message = "Ticket release rate cannot be null")
    @Min(value = 1, message = "Ticket release rate must be at least 1")
    private int ticketReleaseRate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMaxTicketCapacity() {
        return maxTicketCapacity;
    }

    public void setMaxTicketCapacity(int maxTicketCapacity) {
        this.maxTicketCapacity = maxTicketCapacity;
    }

    public int getCustomerRetrievalRate() {
        return customerRetrievalRate;
    }

    public void setCustomerRetrievalRate(int customerRetrievalRate) {
        this.customerRetrievalRate = customerRetrievalRate;
    }

    public int getTicketReleaseRate() {
        return ticketReleaseRate;
    }

    public void setTicketReleaseRate(int ticketReleaseRate) {
        this.ticketReleaseRate = ticketReleaseRate;
    }

    public SystemConfiguration() {}
    public SystemConfiguration(Long id, int maxTicketCapacity, int customerRetrievalRate, int ticketReleaseRate) {
        this.id = id;
        this.maxTicketCapacity = maxTicketCapacity;
        this.customerRetrievalRate = customerRetrievalRate;
        this.ticketReleaseRate = ticketReleaseRate;
    }

    public SystemConfiguration(long l, int i) {
    }
}
