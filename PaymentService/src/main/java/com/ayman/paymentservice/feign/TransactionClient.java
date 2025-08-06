package com.ayman.paymentservice.feign;

import com.ayman.paymentservice.config.FeignClientConfig;
import com.ayman.paymentservice.constant.ApiRoutes;
import com.ayman.paymentservice.model.dto.external.TransactionWrapper;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "TRANSACTION-SERVICE", configuration = FeignClientConfig.class)
public interface TransactionClient {

    @GetMapping(ApiRoutes.TRANSACTION + "/get-by-id")
    ResponseEntity<TransactionWrapper> getTransactionById(@RequestParam Long transactionId);

}
