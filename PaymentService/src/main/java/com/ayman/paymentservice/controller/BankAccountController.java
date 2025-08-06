package com.ayman.paymentservice.controller;

import com.ayman.paymentservice.constant.ApiRoutes;
import com.ayman.paymentservice.model.dto.request.CreateBankRequest;
import com.ayman.paymentservice.model.dto.response.ApiResponse;
import com.ayman.paymentservice.service.BankAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiRoutes.BANK_ACCOUNT)
public class BankAccountController {
    private final BankAccountService bankService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addBankAccount(@RequestBody CreateBankRequest createBankRequest){
        bankService.addBankAccount(createBankRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Bank account created successfully"));
    }
}
