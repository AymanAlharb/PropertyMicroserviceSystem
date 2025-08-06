package com.ayman.propertyservice.service;

import com.ayman.propertyservice.feign.AuthClient;
import com.ayman.propertyservice.model.enums.PropertyStatusEnum;
import com.ayman.propertyservice.model.dto.external.UserWrapper;
import com.ayman.propertyservice.model.dto.response.PropertyResponse;
import com.ayman.propertyservice.model.entity.Property;
import com.ayman.propertyservice.model.dto.request.CreatePropertyRequest;
import com.ayman.propertyservice.model.entity.PropertyOwnership;
import com.ayman.propertyservice.model.mapper.PropertyMapper;
import com.ayman.propertyservice.properties.RabbitMQProperties;
import com.ayman.propertyservice.repository.PropertyOwnerShipRepository;
import com.ayman.propertyservice.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final PropertyMapper propertyMapper;
    private final AuthClient authClient;
    private final PropertyOwnerShipRepository ownerShipRepository;

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;


    public List<Property> findAll() {
        return propertyRepository.findAll();
    }

    public Property findById(Long id) {
        Property property = propertyRepository.findPropertyById(id);
        if (property == null) throw new NoSuchElementException();
        return property;
    }

    public void addProperty(CreatePropertyRequest propertyRequest) {
        // Get Broker username for logging
        String brokerUsername = getUsernameFromToken(SecurityContextHolder.getContext().getAuthentication());

        // Get the owner and check if the owner in the system
        UserWrapper user = authClient.getUserByUsername(propertyRequest.getOwnerUsername()).getBody();

        if (user == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No such user in the system");

        // Check if the owner is a seller
        if (!user.getRole().equals("SELLER"))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Properties can only be added to sellers");


        // Create the propertyOwnership object
        PropertyOwnership propertyOwnership = PropertyOwnership.builder()
                .ownershipDate(LocalDateTime.now())
                .ownerFlag(true)
                .ownerId(user.getUserId())
                .build();

        UserWrapper broker = authClient.getUserByUsername(brokerUsername).getBody();

        // Create the property and save it
        Property property = Property.builder()
                .title(propertyRequest.getTitle())
                .description(propertyRequest.getDescription())
                .status(PropertyStatusEnum.getCode(propertyRequest.getStatus()))
                .location(propertyRequest.getLocation())
                .price(propertyRequest.getPrice())
                .brokerId(broker.getUserId())
                .ownerId(user.getUserId())
                .city(propertyRequest.getCity())
                .region(propertyRequest.getRegion())
                .build();

        propertyRepository.save(property);
        propertyOwnership.setPropertyId(property.getId());
        ownerShipRepository.save(propertyOwnership);

        // Add to elasticsearch database
        pushToElasticsearch(property);
    }


    public Property update(Long id, Property property) {
        if (!propertyRepository.existsById(id)) {
            throw new IllegalArgumentException("No property with id " + id);
        }
        property.setId(id);
        return propertyRepository.save(property);
    }

    public void delete(Long id) {
        propertyRepository.deleteById(id);
    }

    private void pushToElasticsearch(Property property) {
        // Push to the RabbitMQ queue
        rabbitTemplate.convertAndSend(rabbitMQProperties.getExchangeName(), rabbitMQProperties.getPropertyQueue().getRoutingPropertyQueueKeyName(), propertyMapper.toMessage(property));
        log.info("Property added to broker");
    }

    private String getUsernameFromToken(Authentication auth){
        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token not available");
        }
        Jwt jwt = jwtAuth.getToken();
        String username = jwt.getClaim("preferred_username");

        log.info("Username: {} extracted from the jwt token", username);

        return username;
    }


    public PropertyResponse getPropertyById(Long propertyId) {
        Property property = propertyRepository.findPropertyById(propertyId);
        if(property == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found");
        return PropertyResponse.builder()
                .id(property.getId())
                .title(property.getTitle())
                .description(property.getDescription())
                .status(property.getStatus())
                .price(property.getPrice())
                .brokerId(property.getBrokerId())
                .ownerId(property.getOwnerId())
                .build();
    }

    public void updatePropertyStatus(Long propertyId, String status) {
        Property property = propertyRepository.findPropertyById(propertyId);
        if(property == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found");
        property.setStatus(PropertyStatusEnum.getCode(status.toUpperCase()));
        propertyRepository.save(property);
    }

    public void updatePropertyOwnerShip(Long propertyId, Long newOwnerId) {
        // Get owner or throw
        Property property = propertyRepository.findPropertyById(propertyId);
        if(property == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found");

        // Get property or throw
        UserWrapper newOwner = authClient.getUserById(newOwnerId).getBody();
        if(newOwner == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "New owner not found");

        // Update and save
        property.setOwnerId(newOwnerId);
        propertyRepository.save(property);
    }
}

