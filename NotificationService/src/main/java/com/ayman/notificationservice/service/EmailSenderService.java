package com.ayman.notificationservice.service;

import com.ayman.notificationservice.model.entity.Notification;
import com.ayman.notificationservice.model.struct.EmailStruct;
import com.ayman.notificationservice.repository.NotificationRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailSenderService {
    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;


    @Value("${EMAIL}")
    private String fromEmail;


    @RabbitListener(queues = {"${rabbitmq.email-queue.email-queue-name}"})
    private void EmailListener(EmailStruct emailStruct) {
        createNotificationAndSendEmail(emailStruct.getReceiverId(), emailStruct.getReceiverUsername(),
                emailStruct.getReceiverEmail(), emailStruct.getBody(), emailStruct.getSubject());
    }

    private void createNotificationAndSendEmail(Long receiverId, String receiverUsername, String receiverEmail,
                                                String subject, String body) {
        Notification notification = Notification.builder()
                .message(body)
                .date(LocalDateTime.now())
                .receiverId(receiverId)
                .build();

        notificationRepository.save(notification);
        log.info("Notification created for the user: {}", receiverUsername);
        try {
            sendEmail(receiverEmail, subject, body);
        } catch (MessagingException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendEmail(String toEmail, String subject, String body) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);

        // Load HTML template from resources
        ClassPathResource resource = new ClassPathResource("templates/email-template.html");
        String htmlTemplate;
        try (InputStream inputStream = resource.getInputStream()) {
            htmlTemplate = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        // Replace placeholders
        String htmlContent = htmlTemplate
                .replace("{{body}}", body);

        helper.setText(htmlContent, true); // true = HTML content

        // Attach inline image
        ClassPathResource logoImage = new ClassPathResource("templates/brand-inspire-logo-white.png");
        helper.addInline("brandLogo", logoImage);

        mailSender.send(message);
        log.info("Email sent to {}", toEmail);
    }


}
