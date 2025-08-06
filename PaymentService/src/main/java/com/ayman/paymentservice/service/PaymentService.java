package com.ayman.paymentservice.service;

import com.ayman.paymentservice.feign.AuthClient;
import com.ayman.paymentservice.feign.PropertyClient;
import com.ayman.paymentservice.feign.TransactionClient;
import com.ayman.paymentservice.model.dto.external.TransactionWrapper;
import com.ayman.paymentservice.model.dto.external.UserWrapper;
import com.ayman.paymentservice.model.dto.request.PaymentRequest;
import com.ayman.paymentservice.model.entity.BankAccount;
import com.ayman.paymentservice.model.entity.Invoice;
import com.ayman.paymentservice.model.enums.TransectionStatusEnum;
import com.ayman.paymentservice.model.struct.CompleteTransactionStruct;
import com.ayman.paymentservice.model.struct.EmailStruct;
import com.ayman.paymentservice.properties.RabbitMQProperties;
import com.ayman.paymentservice.repository.BankAccountRepository;
import com.ayman.paymentservice.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.ayman.paymentservice.constant.EmailConstant.*;


@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentService {
    private final AuthClient authClient;
    private final TransactionClient transactionClient;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;
    private final CommonService commonService;
    private final BankAccountRepository bankRepository;
    private final BankAccountRepository bankAccountRepository;
    private final PropertyClient propertyClient;
    private final InvoiceRepository invoiceRepository;
    private final double BROKER_PERCENTAGE = 0.049375;

    @Transactional
    public void payment(PaymentRequest paymentRequest) {

        // Get buyer
        UserWrapper buyer = authClient.getUserByUsername
                (commonService.getUsernameFromToken(SecurityContextHolder.getContext().getAuthentication())).getBody();

        // Get Transection
        TransactionWrapper transaction = transactionClient.getTransactionById
                (paymentRequest.getTransectionId()).getBody();

        // Check if transection belongs to buyer and check transaction status
        validateTransectionPayments(buyer, transaction, paymentRequest);

        // Transfer
        transferPrice(buyer, transaction, paymentRequest);

        // Create ownership object
        pushToQueues(paymentRequest.getTransectionId(), transaction.getSellerId(), buyer.getUserId(), transaction.getPropertyId());

        log.info("New ownership created for the user with the id: {} and the property with the id: {}", transaction.getBuyerId(), transaction.getPropertyId());

        // Transfer ownership
        completeTransaction(transaction.getPropertyId(), transaction.getBuyerId(), transaction.getSellerId());

        // Create the invoice for the payment
        createInvoice(paymentRequest, transaction);

    }

    private void createInvoice(PaymentRequest paymentRequest, TransactionWrapper transaction) {
        Invoice invoice = Invoice.builder()
                .transactionId(paymentRequest.getTransectionId())
                .buyerId(transaction.getBuyerId())
                .sellerId(transaction.getSellerId())
                .brokerId(transaction.getBrokerId())
                .price(transaction.getAmount())
                .brokerCommission(transaction.getAmount() * BROKER_PERCENTAGE)
                .dateTime(LocalDateTime.now())
                .build();
        invoiceRepository.save(invoice);
    }

    private void pushToQueues(Long transactionId, Long oldOwnerId, Long newOwnerId, Long propertyId) {
        CompleteTransactionStruct completeTransactionStruct = CompleteTransactionStruct.builder()
                .transactionId(transactionId)
                .oldOwnerId(oldOwnerId)
                .newOwnerId(newOwnerId)
                .propertyId(propertyId)
                .build();

        // Create Ownership
        rabbitTemplate.convertAndSend(rabbitMQProperties.getExchangeName(),
                rabbitMQProperties.getOwnerShipQueue().getRoutingOwnerShipQueueKeyName(), completeTransactionStruct);

        // Update Transaction
        rabbitTemplate.convertAndSend(rabbitMQProperties.getExchangeName(),
                rabbitMQProperties.getTransactionQueue().getRoutingTransactionQueueKeyName(), transactionId);
    }

    private void validateTransectionPayments(UserWrapper buyer, TransactionWrapper transaction, PaymentRequest paymentRequest) {
        if (transaction == null) {
            log.info("User: {} tried to pay for a non existing transaction", buyer.getUsername());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No transection with the id " + paymentRequest.getTransectionId() + " exists");
        }
        // Check the card expiry date
        if (paymentRequest.getExpiryDate().isBefore(LocalDate.now())) {
            log.info("User: {} tried to pay for the transaction: {} with an expired card", buyer.getUsername(), paymentRequest.getTransectionId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Card expired");
        }

        // Check if transection belongs to buyer
        if (!buyer.getUserId().equals(transaction.getBuyerId())) {
            log.info("User: {} tried to pay for the transaction: {} which they do not own", buyer.getUsername(), paymentRequest.getTransectionId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "The transection does not belongs to the buyer");
        }
        // Check transection status
        if (!transaction.getStatus().equals(TransectionStatusEnum.APPROVED)) {
            log.info("User: {} tried to pay for the transaction: {} but failed due: {}", buyer.getUsername(), paymentRequest.getTransectionId(), transaction.getStatus().toString().toLowerCase());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transection: " + transaction.getStatus().toString().toLowerCase());
        }
    }

    private void transferPrice(UserWrapper buyer, TransactionWrapper transaction, PaymentRequest paymentRequest) {
        // Get bank accounts
        BankAccount buyerBank = getBankAccountOrThrow(buyer.getUserId());
        BankAccount brokerBank = getBankAccountOrThrow(transaction.getBrokerId());
        BankAccount sellerBank = getBankAccountOrThrow(transaction.getSellerId());

        double price = transaction.getAmount();

        // Check and deduct balance
        if (price > buyerBank.getBalance()) {
            log.info("User: {} failed to pay for the transaction: {} due to insufficient balance", buyer.getUsername(), paymentRequest.getTransectionId());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient balance");
        }

        buyerBank.setBalance(buyerBank.getBalance() - price);

        // Transfer to seller
        sellerBank.setBalance(sellerBank.getBalance() + price);
        log.info("{} transferred to the seller: {} from the buyer: {} for the transaction: {}"
                , price, transaction.getSellerId(), buyer.getUserId(), paymentRequest.getTransectionId());

        // Send email to the buyer
        sendEmail(buyer.getUserId(), buyer.getEmail(), buyer.getUsername(), PAYMENT_EMAIL_SUBJECT,
                String.format(BUYER_PAYMENT_MESSAGE, transaction.getPropertyId()));

        // Get the broker info. and send the email
        UserWrapper seller = authClient.getUserById(transaction.getSellerId()).getBody();
        sendEmail(seller.getUserId(), seller.getEmail(), seller.getUsername(), PAYMENT_EMAIL_SUBJECT,
                String.format(SELLER_PAYMENT_MESSAGE, buyer.getUsername(), transaction.getPropertyId()));

        double brokerCommission = price * BROKER_PERCENTAGE;

        // Deduct from seller
        sellerBank.setBalance(sellerBank.getBalance() - brokerCommission);
        // Transfer to broker
        brokerBank.setBalance(brokerBank.getBalance() + brokerCommission);

        log.info("{} transferred from: {} account to: {} account"
                , brokerCommission, transaction.getSellerId(), transaction.getBrokerId());

        // Get the broker info. and send the email
        UserWrapper broker = authClient.getUserById(transaction.getBrokerId()).getBody();
        sendEmail(broker.getUserId(), broker.getEmail(), broker.getUsername(), PAYMENT_EMAIL_SUBJECT,
                String.format(BROKER_PAYMENT_MESSAGE, transaction.getPropertyId(), brokerCommission));

        // Save changes
        bankAccountRepository.save(buyerBank);
        bankAccountRepository.save(brokerBank);
        bankAccountRepository.save(sellerBank);
    }

    private void sendEmail(Long receiverId, String receiverEmail,
                           String receiverUsername, String subject, String body) {
        EmailStruct emailStruct = new EmailStruct(receiverId, receiverEmail, receiverUsername, body, subject);
        rabbitTemplate.convertAndSend(rabbitMQProperties.getExchangeName(),
                rabbitMQProperties.getEmailQueue().getRoutingEmailKeyName(), emailStruct);
    }

    private void completeTransaction(Long propertyId, Long newOwnerId, Long oldOwnerId) {
        // Update the property
        propertyClient.updatePropertyOwnership(propertyId, newOwnerId);
        propertyClient.updatePropertyStatus(propertyId, "SOLD");
        log.info("Ownership changed for the property with the id: {} from the user with the id: {} to the user with the id: {}",
                propertyId, oldOwnerId, newOwnerId);
    }

    private BankAccount getBankAccountOrThrow(Long userId) {
        BankAccount bankAccount = bankRepository.findBankAccountByUserId(userId);
        if (bankAccount == null)
            throw new ResponseStatusException
                    (HttpStatus.NOT_FOUND, "No bank account for the user with the id: " + userId);
        return bankAccount;
    }
}
