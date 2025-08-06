package com.ayman.paymentservice.service;

import com.ayman.paymentservice.feign.AuthClient;
import com.ayman.paymentservice.model.dto.external.UserWrapper;
import com.ayman.paymentservice.model.dto.request.CreateBankRequest;
import com.ayman.paymentservice.model.entity.BankAccount;
import com.ayman.paymentservice.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Slf4j
@Service
public class BankAccountService {
    private final BankAccountRepository bankRepository;
    private final CommonService commonService;
    private final AuthClient authClient;

    public void addBankAccount(CreateBankRequest bankRequest) {
        // Get the user
        UserWrapper user = authClient.getUserByUsername
                (commonService.getUsernameFromToken(SecurityContextHolder.getContext().getAuthentication())).getBody();

        // Check if the user has a bank account
        if (bankRepository.findBankAccountByUserId(user.getUserId()) != null) {
            log.info("User: {} tried to add another bank account", user.getUsername());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already has a bank account");
        }
        if (bankRepository.findBankAccountByAccountNumber(bankRequest.getAccountNumber()) != null) {
            log.info("User: {} tried to add a bank account with a used account number", user.getUsername());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Account number exists in the system");
        }
        // Create
        BankAccount bankAccount = BankAccount.builder()
                .accountNumber(bankRequest.getAccountNumber())
                .balance(bankRequest.getBalance())
                .userId(user.getUserId())
                .build();

        bankRepository.save(bankAccount);
        log.info("User: {} added a bank account", user.getUsername());
    }
}
