package com.example.ticketing_system_spring_boot.repository;

import com.example.ticketing_system_spring_boot.model.Consumer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumerRepository extends JpaRepository<Consumer, Long> {
}
