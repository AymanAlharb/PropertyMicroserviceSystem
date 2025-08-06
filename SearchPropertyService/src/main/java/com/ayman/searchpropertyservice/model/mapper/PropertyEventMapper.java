package com.ayman.searchpropertyservice.model.mapper;

import com.ayman.searchpropertyservice.model.document.PropertyDocument;
import com.ayman.searchpropertyservice.model.dto.PropertyEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PropertyEventMapper {
    PropertyDocument toDocument(PropertyEvent event);
}