package com.ayman.paymentservice.feign;

import com.ayman.paymentservice.config.FeignClientConfig;
import com.ayman.paymentservice.constant.ApiRoutes;
import com.ayman.paymentservice.model.dto.external.UserWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "AUTH-SERVICE", configuration = FeignClientConfig.class)
public interface AuthClient {


    @GetMapping(ApiRoutes.AUTH + "get-by-username")
    ResponseEntity<UserWrapper> getUserByUsername(@RequestParam String username);

    @GetMapping(ApiRoutes.AUTH + "get-by-id")
    ResponseEntity<UserWrapper> getUserById(@RequestParam Long userId);
}

