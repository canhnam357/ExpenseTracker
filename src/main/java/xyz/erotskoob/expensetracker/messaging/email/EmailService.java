package xyz.erotskoob.expensetracker.messaging.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    @Value("${application.mail.from}")
    private String fromEmail;

    String buildEmailTemplate(String title, String username,
                              String message, String actionUrl,
                              String buttonText, String footer) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "  body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                "  .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "  .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; border-radius: 10px 10px 0 0; }" +
                "  .content { background: #ffffff; padding: 30px; border: 1px solid #e0e0e0; }" +
                "  .button { display: inline-block; background-color: #667eea; color: white !important; padding: 14px 28px; text-decoration: none; border-radius: 5px; font-weight: bold; margin: 20px 0; }" +
                "  .button:hover { background-color: #5568d3; }" +
                "  .footer { background: #f5f5f5; padding: 20px; text-align: center; font-size: 12px; color: #666; border-radius: 0 0 10px 10px; }" +
                "  .url-box { background: #f5f5f5; padding: 10px; border-radius: 5px; word-break: break-all; font-size: 12px; margin: 10px 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "  <div class='header'>" +
                "    <h1 style='margin: 0;'>" + title + "</h1>" +
                "  </div>" +
                "  <div class='content'>" +
                "    <h2>Xin chào " + username + "!</h2>" +
                "    <p>" + message + "</p>" +
                "    <div style='text-align: center;'>" +
                "      <a href='" + actionUrl + "' class='button'>" + buttonText + "</a>" +
                "    </div>" +
                "    <p style='font-size: 14px; color: #666;'>Hoặc copy link sau vào trình duyệt:</p>" +
                "    <div class='url-box'>" + actionUrl + "</div>" +
                "  </div>" +
                "  <div class='footer'>" +
                "    <p>" + footer + "</p>" +
                "    <p>© 2024 Your Company. All rights reserved.</p>" +
                "  </div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
