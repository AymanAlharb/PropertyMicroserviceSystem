package com.ayman.paymentservice.feign;

import com.ayman.paymentservice.config.FeignClientConfig;
import com.ayman.paymentservice.constant.ApiRoutes;
import com.ayman.paymentservice.model.dto.external.PropertyWrapper;
import com.ayman.paymentservice.model.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(name = "PROPERTY-SERVICE", configuration = FeignClientConfig.class)
public interface PropertyClient {
    @GetMapping(ApiRoutes.PROPERTY + "/get-by-id")
    ResponseEntity<PropertyWrapper> getPropertyById(@RequestParam Long propertyId);

    @PutMapping(ApiRoutes.PROPERTY + "/update-status")
    ResponseEntity<ApiResponse> updatePropertyStatus(@RequestParam Long propertyId, @RequestParam String status);

    @PutMapping(ApiRoutes.PROPERTY + "/update-ownership")
    ResponseEntity<ApiResponse> updatePropertyOwnership(@RequestParam Long propertyId, @RequestParam Long newOwnerId);
}
