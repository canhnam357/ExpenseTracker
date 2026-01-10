package xyz.erotskoob.expensetracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import xyz.erotskoob.expensetracker.config.RabbitMQConfig;
import xyz.erotskoob.expensetracker.dto.EmailMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailConsumer {

    @Value("${application.mail.frontend-url}")
    private String frontendUrl;

    private final EmailService emailService;

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void handleEmailMessage(EmailMessage message) {
        try {
            log.info("Processing email message for: {}", message.getEmail());
            log.info("Message type: {}", message.getType());

            String subject = (message.getType().equals("EMAIL_VERIFICATION") ? "Verify Account" : "Reset Password");
            String url = (message.getType().equals("EMAIL_VERIFICATION") ? frontendUrl + "/auth/verify-email" : frontendUrl + "/auth/reset-password");
            String messageBody = (message.getType().equals("EMAIL_VERIFICATION") ? "Verify your account" : "Reset your password");
            int hourExpiration = (message.getType().equals("EMAIL_VERIFICATION") ? 24 : 1);
            String buttonText = (message.getType().equals("EMAIL_VERIFICATION") ? "Verify Account" : "Reset Password");
            String verifyUrl = frontendUrl + url + "?token=" + message.getToken();

            String htmlContent = emailService.buildEmailTemplate(
                    subject,
                    message.getUsername(),
                    messageBody,
                    verifyUrl,
                    buttonText,
                    "Link này sẽ hết hạn sau " + hourExpiration + " giờ."
            );

            emailService.sendHtmlEmail(message.getEmail(), subject, htmlContent);
            log.info("Successfully sent email to: {}", message.getEmail());

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", message.getEmail(), e.getMessage());
            throw new AmqpRejectAndDontRequeueException("Email sending failed", e);
        }
    }

    // Monitor Dead Letter Queue
    @RabbitListener(queues = RabbitMQConfig.EMAIL_DLQ)
    public void handleFailedMessages(EmailMessage message) {
        log.error("Message moved to DLQ - Email: {}, Token: {}",
                message.getEmail(), message.getToken());
    }
}