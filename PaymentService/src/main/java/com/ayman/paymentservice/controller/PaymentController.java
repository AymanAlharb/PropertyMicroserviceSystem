package com.ayman.paymentservice.controller;

import com.ayman.paymentservice.constant.ApiRoutes;
import com.ayman.paymentservice.model.dto.request.PaymentRequest;
import com.ayman.paymentservice.model.dto.response.ApiResponse;
import com.ayman.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.PAYMENT)
public class PaymentController {
    private final PaymentService paymentService;

    @PutMapping("/pay")
    public ResponseEntity<ApiResponse> transactionPayment(@RequestBody PaymentRequest paymentRequest){
        paymentService.payment(paymentRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Payment completed successfully"));
    }
}
