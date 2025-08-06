package com.ayman.propertyservice.feign;

import com.ayman.propertyservice.config.FeignClientConfig;
import com.ayman.propertyservice.constant.ApiRoutes;
import com.ayman.propertyservice.model.dto.external.UserWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "AUTH-SERVICE", configuration = FeignClientConfig.class)
public interface AuthClient {

    @GetMapping(ApiRoutes.AUTH + "/get-by-username")
    ResponseEntity<UserWrapper> getUserByUsername(@RequestParam String username);

    @GetMapping(ApiRoutes.AUTH + "/get-by-id")
    ResponseEntity<UserWrapper> getUserById(@RequestParam Long userId);

}

