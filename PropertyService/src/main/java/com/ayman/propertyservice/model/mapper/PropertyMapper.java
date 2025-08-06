package com.ayman.propertyservice.model.mapper;

import com.ayman.propertyservice.model.entity.Property;
import com.ayman.propertyservice.model.document.PropertyMessage;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PropertyMapper {
    PropertyMessage toMessage(Property property);
}