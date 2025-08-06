package com.ayman.paymentservice.repository;

import com.ayman.paymentservice.model.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    BankAccount findBankAccountByUserId(Long userId);
    BankAccount findBankAccountByAccountNumber(String accountNumber);
}
