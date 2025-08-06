package com.ayman.transactionservice.controller;

import com.ayman.transactionservice.constant.ApiRoutes;
import com.ayman.transactionservice.model.dto.requset.CreateApprovalRequest;
import com.ayman.transactionservice.model.dto.response.ApiResponse;
import com.ayman.transactionservice.model.dto.response.TransactionWrapper;
import com.ayman.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping(ApiRoutes.TRANSACTION)
@RestController
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping("/request")
    public ResponseEntity<ApiResponse> requestProperty(@RequestParam Long propertyId) {
        transactionService.requestProperty(propertyId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Request made successfully"));
    }

    @PatchMapping("/seller-process-transaction")
    public ResponseEntity<ApiResponse> sellerApproveOrDissApprove(@RequestBody @Valid CreateApprovalRequest approvalRequest) {
        transactionService.sellerApproveOrDissApprove(approvalRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Process completed successfully"));
    }

    @PatchMapping("/broker-process-transaction")
    public ResponseEntity<ApiResponse> brokerApproveOrDissApprove(@RequestBody @Valid CreateApprovalRequest approvalRequest) {
        transactionService.brokerApproveOrDissApprove(approvalRequest);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse("Process completed successfully"));
    }

    @GetMapping("/get-by-id")
    public ResponseEntity<TransactionWrapper> getTransactionById(@RequestParam Long transactionId){
        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransactionById(transactionId));
    }
}
