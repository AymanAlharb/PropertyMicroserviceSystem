package com.ayman.transactionservice.feign;

import com.ayman.transactionservice.config.FeignClientConfig;
import com.ayman.transactionservice.constant.ApiRoutes;
import com.ayman.transactionservice.model.dto.external.PropertyWrapper;
import com.ayman.transactionservice.model.dto.external.UserWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "AUTH-SERVICE", configuration = FeignClientConfig.class)
public interface AuthClient {

    @GetMapping(ApiRoutes.AUTH + "/get-by-username")
    ResponseEntity<UserWrapper> getUserByUsername(@RequestParam String username);

    @GetMapping(ApiRoutes.AUTH + "/get-by-id")
    ResponseEntity<UserWrapper> getUserById(@RequestParam Long userId);

}

