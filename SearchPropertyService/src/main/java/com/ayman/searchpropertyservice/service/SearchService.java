package com.ayman.searchpropertyservice.service;


import com.ayman.searchpropertyservice.model.document.PropertyDocument;
import com.ayman.searchpropertyservice.model.dto.PropertyEvent;
import com.ayman.searchpropertyservice.model.mapper.PropertyEventMapper;
import com.ayman.searchpropertyservice.repository.PropertyDocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearchService {

    private final PropertyDocumentRepository propertyRepository;
    private final PropertyEventMapper propertyMapper;

    public Iterable<PropertyDocument> getAll(){
        return propertyRepository.findAll();
    }

    public List<PropertyDocument> searchByPrice(double min, double max) {
        return propertyRepository.findByPriceBetween(min, max);
    }


    @RabbitListener(queues = {"${rabbitmq.property-queue.propertyQueueName}"})
    private void propertyAddedEventListener(PropertyEvent propertyEvent) {
        createPropertyDocument(propertyEvent);
    }

    private void createPropertyDocument(PropertyEvent propertyEvent) {
        propertyRepository.save(propertyMapper.toDocument(propertyEvent));
        log.info("Property document {} added to elasticsearch", propertyEvent.getTitle());
    }
}

