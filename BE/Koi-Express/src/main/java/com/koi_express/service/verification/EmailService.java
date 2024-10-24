package com.koi_express.service.verification;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.koi_express.entity.account.SystemAccount;
import com.koi_express.entity.order.Orders;
import com.koi_express.entity.shipment.DeliveringStaff;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender javaMailSender;

    private static final String TEMPLATE_ORDER_CONFIRMATION = "Order Confirmation.html";
    private static final String TEMPLATE_PAYMENT_LINK = "Payment Link.html";
    private static final String TEMPLATE_ACCOUNT_CONFIRMATION = "Account Confirmation.html";
    private static final String TEMPLATE_INVOICE = "Invoice.html";

    @Async
    public void sendOrderConfirmationEmail(String recipientEmail, Orders order) throws IOException {
        try {
            String template = loadEmailTemplate(TEMPLATE_ORDER_CONFIRMATION);

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{{CustomerName}}", order.getCustomer().getFullName());
            placeholders.put("{{OrderID}}", String.valueOf(order.getOrderId()));
            placeholders.put("{{OrderDate}}", order.getCreatedAt().toString());
            placeholders.put("{{Address}}", order.getDestinationLocation());
            placeholders.put("{{TotalAmount}}", String.format("%.2f", order.getTotalFee()));
            placeholders.put("{{TrackOrderLink}}", "https://koiexpress.com/track-order/" + order.getOrderId());

            sendEmail(recipientEmail, "Order Confirmation - Koi Express", template, placeholders);
        } catch (Exception e) {
            logger.error("Error sending order confirmation email: ", e);
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }

    @Async
    public void sendPaymentLink(String recipientEmail, String paymentLink, Orders order) {
        try {
            String template = loadEmailTemplate(TEMPLATE_PAYMENT_LINK);

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{{CustomerName}}", order.getCustomer().getFullName());
            placeholders.put("{{OrderID}}", String.valueOf(order.getOrderId()));
            placeholders.put("{{PaymentLink}}", paymentLink);
            placeholders.put("{{TotalAmount}}", String.format("%.2f", order.getTotalFee()));

            sendEmail(recipientEmail, "Payment for your Order - Koi Express", template, placeholders);
        } catch (Exception e) {
            logger.error("Error sending payment link email: ", e);
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }

    @Async
    public void sendAccountCreatedEmail(Object account, String rawPassword, boolean isDeliveringStaff) {
        try {
            String template = loadEmailTemplate(TEMPLATE_ACCOUNT_CONFIRMATION);

            Map<String, String> placeholders = new HashMap<>();
            if (isDeliveringStaff) {
                DeliveringStaff deliveringStaff = (DeliveringStaff) account;
                placeholders.put("{{FullName}}", deliveringStaff.getFullName());
                placeholders.put("{{StaffId}}", String.valueOf(deliveringStaff.getId()));
                placeholders.put("{{Email}}", deliveringStaff.getEmail());
                placeholders.put("{{PhoneNumber}}", deliveringStaff.getPhoneNumber());
                placeholders.put("{{Password}}", rawPassword);
                placeholders.put("{{Role}}", deliveringStaff.getRole().toString());
                placeholders.put("{{Level}}", deliveringStaff.getLevel().name());
                placeholders.put("{{CreatedAt}}", deliveringStaff.getCreatedAt().toString());
            } else {
                SystemAccount systemAccount = (SystemAccount) account;
                placeholders.put("{{FullName}}", systemAccount.getFullName());
                placeholders.put("{{StaffId}}", String.valueOf(systemAccount.getId()));
                placeholders.put("{{Email}}", systemAccount.getEmail());
                placeholders.put("{{PhoneNumber}}", systemAccount.getPhoneNumber());
                placeholders.put("{{Password}}", rawPassword);
                placeholders.put("{{Role}}", systemAccount.getRole().toString());
                placeholders.put("{{CreatedAt}}", systemAccount.getCreatedAt().toString());
            }

            sendEmail(
                    isDeliveringStaff ? ((DeliveringStaff) account).getEmail() : ((SystemAccount) account).getEmail(),
                    "Account Created - Koi Express",
                    template,
                    placeholders);
        } catch (Exception e) {
            logger.error("Error sending account creation email: ", e);
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }

    @Async
    public void sendInvoiceEmail(String recipientEmail, Orders order, Map<String, BigDecimal> calculationData) {
        try {
            String template = loadEmailTemplate(TEMPLATE_INVOICE); // Sử dụng template hóa đơn

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{{CustomerName}}", order.getCustomer().getFullName());
            placeholders.put("{{InvoiceID}}", String.valueOf(order.getOrderId()));
            placeholders.put("{{OrderID}}", String.valueOf(order.getOrderId()));
            placeholders.put("{{InvoiceDate}}", order.getCreatedAt().toString());
            placeholders.put("{{Address}}", order.getDestinationLocation());
            placeholders.put(
                    "{{KoiQuantity}}", String.valueOf(order.getOrderDetail().getKoiQuantity()));
            placeholders.put("{{Subtotal}}", String.format("%.2f", calculationData.get("subtotal")));
            placeholders.put("{{CareFee}}", String.format("%.2f", calculationData.get("careFee")));
            placeholders.put("{{InsuranceFee}}", String.format("%.2f", calculationData.get("insuranceFee")));
            placeholders.put("{{PackagingFee}}", String.format("%.2f", calculationData.get("packagingFee")));
            placeholders.put("{{VAT}}", String.format("%.2f", calculationData.get("vat")));
            placeholders.put("{{TotalAmount}}", String.format("%.2f", calculationData.get("totalFee")));

            sendEmail(recipientEmail, "Invoice - Koi Express", template, placeholders);
        } catch (Exception e) {
            logger.error("Error sending invoice email: ", e);
            throw new AppException(ErrorCode.EMAIL_SENDING_FAILED);
        }
    }

    private String loadEmailTemplate(String fileName) throws IOException {
        ClassPathResource resource = new ClassPathResource(fileName);

        try (BufferedReader reader =
                new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        }
    }

    private String replacePlaceholders(String template, Map<String, String> values) {
        for (Map.Entry<String, String> entry : values.entrySet()) {
            template = template.replace(entry.getKey(), entry.getValue());
        }
        return template;
    }

    private void sendEmail(String recipientEmail, String subject, String template, Map<String, String> placeholders)
            throws IOException, MessagingException {
        String populatedTemplate = replacePlaceholders(template, placeholders);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(recipientEmail);
        helper.setSubject(subject);
        helper.setText(populatedTemplate, true);

        javaMailSender.send(message);

        logger.info("Email sent to: {}", recipientEmail);
    }
}
