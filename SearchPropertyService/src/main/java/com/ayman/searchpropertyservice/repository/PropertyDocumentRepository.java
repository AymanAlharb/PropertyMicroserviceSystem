package com.ayman.searchpropertyservice.repository;

import com.ayman.searchpropertyservice.model.document.PropertyDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PropertyDocumentRepository
        extends ElasticsearchRepository<PropertyDocument, Long> {


    List<PropertyDocument> findByPriceBetween(double min, double max);
}