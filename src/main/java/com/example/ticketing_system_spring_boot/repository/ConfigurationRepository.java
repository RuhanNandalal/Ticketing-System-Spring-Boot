package com.example.ticketing_system_spring_boot.repository;

import com.example.ticketing_system_spring_boot.model.SystemConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigurationRepository extends JpaRepository<SystemConfiguration, Long> {
    @Query("SELECT c FROM SystemConfiguration c WHERE c.id = 1")
    Optional<SystemConfiguration> findSingletonConfiguration();
}
