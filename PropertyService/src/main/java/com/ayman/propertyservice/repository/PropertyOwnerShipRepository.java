package com.ayman.propertyservice.repository;

import com.ayman.propertyservice.model.entity.PropertyOwnership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyOwnerShipRepository extends JpaRepository<PropertyOwnership, Long> {
    PropertyOwnership getPropertyOwnershipByPropertyIdAndOwnerId(Long propertyId, Long ownerId);
}
