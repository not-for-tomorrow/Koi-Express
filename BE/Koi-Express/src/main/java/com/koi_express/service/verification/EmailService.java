package com.koi_express.service.verification;

import com.koi_express.entity.order.Orders;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOrderConfirmationEmail(String recipientEmail, Orders order) throws IOException {

        try {

            String htmlTemplate = loadEmailTemplate("Order Confirmation.html");

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{{CustomerName}}", order.getCustomer().getFullName());
            placeholders.put("{{OrderID}}", String.valueOf(order.getOrderId()));
            placeholders.put("{{OrderDate}}", order.getCreatedAt().toString());
            placeholders.put("{{Address}}", order.getDestinationLocation());
            placeholders.put("{{TotalAmount}}", String.format("%.2f", order.getTotalFee()));
            placeholders.put("{{TrackOrderLink}}", "https://koiexpress.com/track-order/" + order.getOrderId());

            htmlTemplate = replacePlaceholders(htmlTemplate, placeholders);

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(recipientEmail);
            helper.setSubject("Order Confirmation - Koi Express");
            helper.setText(htmlTemplate, true);
            javaMailSender.send(message);

            logger.info("Order confirmation email sent to: {}", recipientEmail);
        } catch (Exception e) {
            logger.error("Error sending order confirmation email: ", e);
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }

    private String loadEmailTemplate(String fileName) throws IOException {

        ClassPathResource resource = new ClassPathResource(fileName);

        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private String replacePlaceholders(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace(entry.getKey(), entry.getValue());
        }
        return template;
    }
}
