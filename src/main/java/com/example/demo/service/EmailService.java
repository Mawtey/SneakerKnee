package com.example.demo.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendOrderStatusUpdate(String to, String orderId, String newStatus) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject("Order Status Update - Order #" + orderId);
            helper.setText("<p>Your order <strong>#" + orderId + "</strong> status has been updated to <strong>" + newStatus + "</strong>.</p>", true);

            mailSender.send(message);
            logger.info("Email sent to {} for order ID {} with new status {}", to, orderId, newStatus);
        } catch (MessagingException e) {
            logger.error("Failed to send email to {} for order ID {}: {}", to, orderId, e.getMessage());
        }
    }
}
