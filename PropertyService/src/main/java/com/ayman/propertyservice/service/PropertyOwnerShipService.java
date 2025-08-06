package com.ayman.propertyservice.service;


import com.ayman.propertyservice.model.entity.PropertyOwnership;
import com.ayman.propertyservice.model.struct.CompleteTransactionStruct;
import com.ayman.propertyservice.repository.PropertyOwnerShipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Service
public class PropertyOwnerShipService {
    private final PropertyOwnerShipRepository ownerShipRepository;

    @RabbitListener(queues = {"${rabbitmq.owner-ship-queue.owner-ship-queue-name}"})
    private void paymentListener(CompleteTransactionStruct completeTransactionStruct){
        log.info("Ownership consumer received the property with the id: {}", completeTransactionStruct.getPropertyId());
        updateOldOwnerShipFlag(completeTransactionStruct.getPropertyId(), completeTransactionStruct.getOldOwnerId());
        createPropertyOwnerShip(completeTransactionStruct.getPropertyId(), completeTransactionStruct.getNewOwnerId());
    }

    private void createPropertyOwnerShip(Long propertyId, Long newOwnerId) {
        PropertyOwnership ownership = PropertyOwnership.builder()
                .ownerFlag(true)
                .ownershipDate(LocalDateTime.now())
                .propertyId(propertyId)
                .ownerId(newOwnerId)
                .build();
        ownerShipRepository.save(ownership);
        log.info("New property ownership created for the property with the id: {} for the user with the id: {}",
                propertyId, newOwnerId);
    }


    private void updateOldOwnerShipFlag(Long propertyId, Long oldOwnerId) {
        PropertyOwnership oldOwnership = ownerShipRepository.getPropertyOwnershipByPropertyIdAndOwnerId(propertyId, oldOwnerId);
        oldOwnership.setOwnerFlag(false);
        ownerShipRepository.save(oldOwnership);
    }


}
