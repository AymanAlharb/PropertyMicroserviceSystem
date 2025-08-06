package com.ayman.propertyservice.controller;

import com.ayman.propertyservice.constant.ApiRoutes;
import com.ayman.propertyservice.model.dto.response.ApiResponse;
import com.ayman.propertyservice.model.dto.request.CreatePropertyRequest;
import com.ayman.propertyservice.model.dto.response.PropertyResponse;
import com.ayman.propertyservice.model.entity.Property;
import com.ayman.propertyservice.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.PROPERTY)
public class PropertyController {
    private final PropertyService propertyService;

    @GetMapping("/get-all")
    public List<Property> list() {
        return propertyService.findAll();
    }

    @GetMapping("/get/{id}")
    public Property get(@PathVariable Long id) {
        return propertyService.findById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProperty(@RequestBody CreatePropertyRequest propertyRequest) {
        propertyService.addProperty(propertyRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Property Added successfully"));
    }

    @PutMapping("/update/{id}")
    public Property update(@PathVariable Long id, @RequestBody Property property) {
        return propertyService.update(id, property);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        propertyService.delete(id);
    }

    @GetMapping("/get-by-id")
    public ResponseEntity<PropertyResponse> getPropertyById(@RequestParam Long propertyId){
        return ResponseEntity.status(HttpStatus.OK).body(propertyService.getPropertyById(propertyId));
    }

    @PutMapping("/update-status")
    public ResponseEntity<ApiResponse> updatePropertyStatus(@RequestParam Long propertyId, @RequestParam String status){
        propertyService.updatePropertyStatus(propertyId, status);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Status updated successfully"));
    }

    @PutMapping("/update-ownership")
    public ResponseEntity<ApiResponse> updatePropertyOwnership(@RequestParam Long propertyId, @RequestParam Long newOwnerId){
        propertyService.updatePropertyOwnerShip(propertyId, newOwnerId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Property Ownership changed successfully"));
    }
}

