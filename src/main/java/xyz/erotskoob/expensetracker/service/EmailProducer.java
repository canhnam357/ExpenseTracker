package xyz.erotskoob.expensetracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import xyz.erotskoob.expensetracker.config.RabbitMQConfig;
import xyz.erotskoob.expensetracker.dto.EmailMessage;
import xyz.erotskoob.expensetracker.entity.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendEmailVerificationMessage(User user, String token, String type) {
        EmailMessage message = new EmailMessage(
                user.getEmail(),
                user.getUsername(),
                token,
                type
        );

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EMAIL_EXCHANGE,
                    RabbitMQConfig.EMAIL_ROUTING_KEY,
                    message
            );
            log.info("Sent email verification message to queue for: {}", user.getEmail());
        } catch (Exception e) {
            log.error("Failed to send message to queue: {}", e.getMessage());
            throw new RuntimeException("Failed to queue email", e);
        }
    }
}
