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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender javaMailSender;

    private static final String TEMPLATE_ORDER_CONFIRMATION = "Order Confirmation.html";
    private static final String TEMPLATE_PAYMENT_LINK = "Payment Link.html";
    private static final String TEMPLATE_ACCOUNT_CONFIRMATION = "Account Confirmation.html";
    private static final String TEMPLATE_INVOICE = "Invoice.html";

    public static final String FULL_NAME_PLACEHOLDER = "{{FullName}}";
    public static final String CUSTOMER_NAME_PLACEHOLDER = "{{CustomerName}}";
    public static final String ORDER_ID_PLACEHOLDER = "{{OrderID}}";
    public static final String ORDER_DATE_PLACEHOLDER = "{{OrderDate}}";
    public static final String ADDRESS_PLACEHOLDER = "{{Address}}";
    public static final String TOTAL_AMOUNT_PLACEHOLDER = "{{TotalAmount}}";
    public static final String TRACK_ORDER_LINK_PLACEHOLDER = "{{TrackOrderLink}}";

    public static final String STAFF_ID_PLACEHOLDER = "{{StaffId}}";
    public static final String EMAIL_PLACEHOLDER = "{{Email}}";
    public static final String PHONE_NUMBER_PLACEHOLDER = "{{PhoneNumber}}";
    public static final String PASSWORD_PLACEHOLDER = "{{Password}}";
    public static final String ROLE_PLACEHOLDER = "{{Role}}";
    public static final String LEVEL_PLACEHOLDER = "{{Level}}";
    public static final String CREATED_AT_PLACEHOLDER = "{{CreatedAt}}";

    public static final String INVOICE_ID_PLACEHOLDER = "{{InvoiceID}}";
    public static final String KOI_QUANTITY_PLACEHOLDER = "{{KoiQuantity}}";
    public static final String SUBTOTAL_PLACEHOLDER = "{{Subtotal}}";
    public static final String CARE_FEE_PLACEHOLDER = "{{CareFee}}";
    public static final String INSURANCE_FEE_PLACEHOLDER = "{{InsuranceFee}}";
    public static final String PACKAGING_FEE_PLACEHOLDER = "{{PackagingFee}}";
    public static final String VAT_PLACEHOLDER = "{{VAT}}";


    @Async
    public void sendOrderConfirmationEmail(String recipientEmail, Orders order) {
        try {
            String template = loadEmailTemplate(TEMPLATE_ORDER_CONFIRMATION);

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put(CUSTOMER_NAME_PLACEHOLDER, order.getCustomer().getFullName());
            placeholders.put(ORDER_ID_PLACEHOLDER, String.valueOf(order.getOrderId()));
            placeholders.put(ORDER_DATE_PLACEHOLDER, order.getCreatedAt().toString());
            placeholders.put(ADDRESS_PLACEHOLDER, order.getDestinationLocation());
            placeholders.put(TOTAL_AMOUNT_PLACEHOLDER, String.format("%.2f", order.getTotalFee()));
            placeholders.put(TRACK_ORDER_LINK_PLACEHOLDER, "https://koiexpress.com/track-order/" + order.getOrderId());

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
            placeholders.put(CUSTOMER_NAME_PLACEHOLDER, order.getCustomer().getFullName());
            placeholders.put(ORDER_ID_PLACEHOLDER, String.valueOf(order.getOrderId()));
            placeholders.put("{{PaymentLink}}", paymentLink);
            placeholders.put(TOTAL_AMOUNT_PLACEHOLDER, String.format("%.2f", order.getTotalFee()));

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
                placeholders.put(FULL_NAME_PLACEHOLDER, deliveringStaff.getFullName());
                placeholders.put(STAFF_ID_PLACEHOLDER, String.valueOf(deliveringStaff.getId()));
                placeholders.put(EMAIL_PLACEHOLDER, deliveringStaff.getEmail());
                placeholders.put(PHONE_NUMBER_PLACEHOLDER, deliveringStaff.getPhoneNumber());
                placeholders.put(PASSWORD_PLACEHOLDER, rawPassword);
                placeholders.put(ROLE_PLACEHOLDER, deliveringStaff.getRole().toString());
                placeholders.put(LEVEL_PLACEHOLDER, deliveringStaff.getLevel().name());
                placeholders.put(CREATED_AT_PLACEHOLDER, deliveringStaff.getCreatedAt().toString());
            } else {
                SystemAccount systemAccount = (SystemAccount) account;
                placeholders.put(FULL_NAME_PLACEHOLDER, systemAccount.getFullName());
                placeholders.put(STAFF_ID_PLACEHOLDER, String.valueOf(systemAccount.getId()));
                placeholders.put(EMAIL_PLACEHOLDER, systemAccount.getEmail());
                placeholders.put(PHONE_NUMBER_PLACEHOLDER, systemAccount.getPhoneNumber());
                placeholders.put(PASSWORD_PLACEHOLDER, rawPassword);
                placeholders.put(ROLE_PLACEHOLDER, systemAccount.getRole().toString());
                placeholders.put(CREATED_AT_PLACEHOLDER, systemAccount.getCreatedAt().toString());
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
            String template = loadEmailTemplate(TEMPLATE_INVOICE);

            Map<String, String> placeholders = new HashMap<>();
            placeholders.put(CUSTOMER_NAME_PLACEHOLDER, order.getCustomer().getFullName());
            placeholders.put(INVOICE_ID_PLACEHOLDER, String.valueOf(order.getOrderId()));
            placeholders.put(ORDER_ID_PLACEHOLDER, String.valueOf(order.getOrderId()));
            placeholders.put("{{InvoiceDate}}", order.getCreatedAt().toString());
            placeholders.put(ADDRESS_PLACEHOLDER, order.getDestinationLocation());
            placeholders.put(
                    KOI_QUANTITY_PLACEHOLDER, String.valueOf(order.getOrderDetail().getKoiQuantity()));
            placeholders.put(SUBTOTAL_PLACEHOLDER, String.format("%.2f", calculationData.get("subtotal")));
            placeholders.put(CARE_FEE_PLACEHOLDER, String.format("%.2f", calculationData.get("careFee")));
            placeholders.put(INSURANCE_FEE_PLACEHOLDER, String.format("%.2f", calculationData.get("insuranceFee")));
            placeholders.put(PACKAGING_FEE_PLACEHOLDER, String.format("%.2f", calculationData.get("packagingFee")));
            placeholders.put(VAT_PLACEHOLDER, String.format("%.2f", calculationData.get("vat")));
            placeholders.put(TOTAL_AMOUNT_PLACEHOLDER, String.format("%.2f", calculationData.get("totalFee")));

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
            throws MessagingException {
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
