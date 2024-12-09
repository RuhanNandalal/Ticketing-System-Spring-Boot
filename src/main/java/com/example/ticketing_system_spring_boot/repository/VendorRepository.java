package com.example.ticketing_system_spring_boot.repository;

import com.example.ticketing_system_spring_boot.model.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {
}
