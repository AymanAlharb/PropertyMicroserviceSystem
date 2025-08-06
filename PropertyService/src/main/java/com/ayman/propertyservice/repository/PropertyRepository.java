package com.ayman.propertyservice.repository;


import com.ayman.propertyservice.model.entity.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    Property findPropertyById(Long id);
}

