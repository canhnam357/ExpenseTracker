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

            String verifyUrl = frontendUrl + "/auth/verify-email?token=" + message.getToken();
            String subject = "Xác thực tài khoản của bạn";

            String htmlContent = emailService.buildEmailTemplate(
                    "Xác Thực Email",
                    message.getUsername(),
                    "Cảm ơn bạn đã đăng ký! Vui lòng xác thực email để kích hoạt tài khoản.",
                    verifyUrl,
                    "Xác Thực Email",
                    "Link này sẽ hết hạn sau 24 giờ."
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