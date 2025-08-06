package com.ayman.transactionservice.feign;

import com.ayman.transactionservice.config.FeignClientConfig;
import com.ayman.transactionservice.constant.ApiRoutes;
import com.ayman.transactionservice.model.dto.external.PropertyWrapper;
import io.swagger.v3.oas.models.responses.ApiResponse;
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
}
