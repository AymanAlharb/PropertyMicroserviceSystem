package com.ayman.transactionservice.service;

import com.ayman.transactionservice.feign.AuthClient;
import com.ayman.transactionservice.feign.PropertyClient;
import com.ayman.transactionservice.model.dto.external.PropertyWrapper;
import com.ayman.transactionservice.model.dto.external.UserWrapper;
import com.ayman.transactionservice.model.dto.requset.CreateApprovalRequest;
import com.ayman.transactionservice.model.dto.response.TransactionWrapper;
import com.ayman.transactionservice.model.entity.Transaction;
import com.ayman.transactionservice.model.enums.PropertyStatusEnum;
import com.ayman.transactionservice.model.enums.TransectionStatusEnum;
import com.ayman.transactionservice.model.struct.EmailStruct;
import com.ayman.transactionservice.properties.RabbitMQProperties;
import com.ayman.transactionservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static com.ayman.transactionservice.constant.EmailConstant.*;


@RequiredArgsConstructor
@Slf4j
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitMQProperties;
    private final AuthClient authClient;
    private final PropertyClient propertyClient;

    @Transactional
    public void requestProperty(Long propertyId) {
        // Get buyer
        UserWrapper buyer = authClient.getUserByUsername
                (getUsernameFromToken(SecurityContextHolder.getContext().getAuthentication())).getBody();
        // Get the property
        PropertyWrapper property = propertyClient.getPropertyById(propertyId).getBody();
        // Check the property status
        checkPropertyStatus(property, buyer.getUsername());

        Transaction transaction = Transaction.builder()
                .amount(property.getPrice())
                .status(TransectionStatusEnum.PENDING)
                .date(LocalDateTime.now())
                .propertyId(propertyId)
                .buyerId(buyer.getUserId())
                .sellerId(property.getOwnerId())
                .brokerId(property.getBrokerId())
                .build();

        log.info("Buyer: {} has made a request to buy: {}", buyer.getUsername(), property.getTitle());
        property.setStatus(PropertyStatusEnum.LOCKED);
        propertyClient.updatePropertyStatus(propertyId, "LOCKED");
        transactionRepository.save(transaction);

        // Send emails
        sendEmail(buyer.getUserId(), buyer.getEmail(), buyer.getUsername(), String.format(REQUEST_EMAIL_SUBJECT, property.getTitle()),
                String.format(BUYER_REQUEST_MESSAGE, property.getTitle()));

        UserWrapper owner = authClient.getUserById(property.getOwnerId()).getBody();
        sendEmail(owner.getUserId(), owner.getEmail(), owner.getUsername(), String.format(REQUEST_EMAIL_SUBJECT, property.getTitle()),
                String.format(SELLER_REQUEST_MESSAGE, buyer.getUsername(), property.getTitle()));
    }

    public TransactionWrapper getTransactionById(Long transactionId) {
        log.info("In the getter");
        Transaction transaction = transactionRepository.findTransactionById(transactionId);
        if(transaction == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found");
        log.info("In the getter and found");

        return TransactionWrapper.builder()
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .reasonOfFailure(transaction.getReasonOfFailure())
                .date(transaction.getDate())
                .propertyId(transaction.getPropertyId())
                .buyerId(transaction.getBuyerId())
                .sellerId(transaction.getSellerId())
                .brokerId(transaction.getBrokerId())
                .build();
    }

    public void sellerApproveOrDissApprove(CreateApprovalRequest approvalRequest) {
        // Get transection
        Transaction transaction = getTransectionOrThrow(approvalRequest);

        // Get seller
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserWrapper seller = authClient.getUserByUsername(getUsernameFromToken(auth)).getBody();

        PropertyWrapper property = propertyClient.getPropertyById(transaction.getPropertyId()).getBody()
                ;
        // Check if the property belongs to the seller
        checkIfSellerOwnProperty(seller, property);

        // Check if transection status
        if (!transaction.getStatus().equals(TransectionStatusEnum.PENDING)) {
            log.info("User: {} tried to approve a request to the property: {} that can not be approved that is because {}",
                    seller.getUsername(), property.getTitle(), property.getStatus());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transection " + transaction.getStatus().toString().toLowerCase() + " and can not be approved by the seller");
        }

        // Get users
        UserWrapper buyer = authClient.getUserById(transaction.getBuyerId()).getBody();
        UserWrapper broker = authClient.getUserById(transaction.getBrokerId()).getBody();

        modifyAndSendEmail(approvalRequest, transaction, buyer, seller, broker, property);

    }

   @RabbitListener(queues = {"${rabbitmq.transaction-queue.transaction-queue-name}"})
    public void updateTransactionListener(Long transactionId){
        Transaction transaction = transactionRepository.findTransactionById(transactionId);
        transaction.setStatus(TransectionStatusEnum.COMPLETED);
        transactionRepository.save(transaction);
    }

    private void modifyAndSendEmail(CreateApprovalRequest approvalRequest, Transaction transaction,
                                    UserWrapper buyer, UserWrapper seller, UserWrapper broker,
                                    PropertyWrapper property) {
        if (approvalRequest.getApproval()) {
            // Modify transaction
            transaction.setStatus(TransectionStatusEnum.APPROVED_BY_SELLER);
            transactionRepository.save(transaction);

            // Send emails
            sendEmail(buyer.getUserId(), buyer.getEmail(), buyer.getUsername(), REQUEST_APPROVAL_EMAIL_SUBJECT,
                    String.format(BUYER_APPROVAL_MESSAGE, property.getTitle(), seller.getUsername()));

            sendEmail(broker.getUserId(), broker.getEmail(), broker.getUsername(),
                    String.format(REQUEST_EMAIL_SUBJECT, property.getTitle()),
                    String.format(BROKER_REQUEST_MESSAGE, buyer.getUsername(), property.getTitle()));

            log.info("User: {} approved the transaction with the id: {}", seller.getUsername(), approvalRequest.getTransectionId());
        } else {
            // Modify transaction
            transaction.setStatus(TransectionStatusEnum.FAILED);
            transaction.setReasonOfFailure(String.valueOf(approvalRequest.getReasonOfFailure()));

            // Send email
            sendEmail(buyer.getUserId(), buyer.getEmail(), buyer.getUsername(), REQUEST_DENIED_EMAIL_SUBJECT,
                    String.format(BUYER_DENIAL_MESSAGE, property.getTitle(), seller, approvalRequest.getReasonOfFailure()));

            log.info("User: {} unapproved the transaction with the id: {}", seller.getUsername(), approvalRequest.getTransectionId());
        }
    }

    private void checkIfSellerOwnProperty(UserWrapper seller, PropertyWrapper property) {
        if (!property.getOwnerId().equals(seller.getUserId())) {
            log.info("User: {} tried to approve a request to the property: {} they do not own",
                    seller.getUsername(), property.getTitle());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Property does not belong to seller");
        }

    }

    public void brokerApproveOrDissApprove(CreateApprovalRequest approvalRequest) {
        // Get transection
        Transaction transaction = getTransectionOrThrow(approvalRequest);

        // Get broker
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserWrapper broker = authClient.getUserByUsername(getUsernameFromToken(auth)).getBody();

        // Check if the broker has authorities on the property
        PropertyWrapper property = propertyClient.getPropertyById(transaction.getPropertyId()).getBody();
        if (!property.getBrokerId().equals(broker.getUserId())) {
            log.info("User: {} tried to approve a request to the property: {} that they do not have authorities on",
                    broker.getUsername(), property.getTitle());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Broker has no authorities on the property");
        }

        // Check if seller approved
        if (!transaction.getStatus().equals(TransectionStatusEnum.APPROVED_BY_SELLER)) {
            log.info("User: {} tried to approve a request to the property: {} that can not be approved, that is because {}",
                    broker.getUsername(), property.getTitle(), property.getStatus());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction " + transaction.getStatus() + " and can not be approved by the broker");
        }

        UserWrapper buyer = authClient.getUserById(transaction.getBuyerId()).getBody();
        if (approvalRequest.getApproval()) {
            // Modify transaction
            transaction.setStatus(TransectionStatusEnum.APPROVED);
            transactionRepository.save(transaction);

            // Send email
            sendEmail(buyer.getUserId(), buyer.getEmail(), buyer.getUsername(), REQUEST_APPROVAL_EMAIL_SUBJECT,
                    String.format(BUYER_APPROVAL_MESSAGE, property.getTitle(), broker.getUsername()));

            log.info("User: {} approved the transection with the id: {}", broker.getUsername(), approvalRequest.getTransectionId());
        } else {
            // Modify transaction
            transaction.setStatus(TransectionStatusEnum.FAILED);
            transaction.setReasonOfFailure(approvalRequest.getReasonOfFailure());

            // Send email
            sendEmail(buyer.getUserId(), buyer.getEmail(), buyer.getUsername(), REQUEST_DENIED_EMAIL_SUBJECT,
                    String.format(BUYER_DENIAL_MESSAGE, property.getTitle(), broker, approvalRequest.getReasonOfFailure()));

            log.info("User: {} unapproved the transection with the id: {}", broker.getUsername(), approvalRequest.getTransectionId());
        }
    }

    private Transaction getTransectionOrThrow(CreateApprovalRequest approvalRequest) {
        Transaction transaction = transactionRepository.findTransactionById(approvalRequest.getTransectionId());
        if (transaction == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No transection with the id " + approvalRequest.getTransectionId() + " exists");
        return transaction;
    }

    private void checkPropertyStatus(PropertyWrapper property, String username) {
        switch (property.getStatus()) {
            case HIDDEN -> {
                log.info("User: {} requested the hidden property: {}", username, property.getTitle());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This is a hidden property");
            }
            case LOCKED -> {
                log.info("User: {} requested the locked property: {}", username, property.getTitle());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This is a locked property");
            }
            case SOLD -> {
                log.info("User: {} requested the sold property: {}", username, property.getTitle());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This is a sold property");
            }
        }
    }

    private String getUsernameFromToken(Authentication auth) {
        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Token not available");
        }
        Jwt jwt = jwtAuth.getToken();
        String username = jwt.getClaim("preferred_username");

        log.info("Username: {} extracted from the jwt token", username);

        return username;
    }

    private void sendEmail(Long receiverId, String receiverEmail,
                           String receiverUsername, String subject, String body) {
        EmailStruct emailStruct = new EmailStruct(receiverId, receiverEmail, receiverUsername, body, subject);
        rabbitTemplate.convertAndSend(rabbitMQProperties.getExchangeName(),
                rabbitMQProperties.getEmailQueue().getRoutingEmailKeyName(), emailStruct);
    }
}
