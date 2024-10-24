package com.koi_express.controller.order;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.Orders;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(OrderSessionManager.class);

    private static final String ROLE_SESSION_KEY = "role";
    private static final String USER_ID_SESSION_KEY = "userId";

    // Lưu session dữ liệu của order
    public void storeSessionData(HttpSession session, String role, String userId, Orders order) {
        String sessionKey = getSessionKey(role, userId);
        Customers customer = order.getCustomer();
        Map<String, Object> sessionData = Map.of(
                "koiQuantity", order.getOrderDetail().getKoiQuantity(),
                "distanceFee", order.getOrderDetail().getDistanceFee(),
                "commitmentFee", order.getOrderDetail().getCommitmentFee(),
                "orderId", order.getOrderId(),
                "customerId", customer.getCustomerId(),
                "email", customer.getEmail());
        session.setAttribute(sessionKey, sessionData);
        session.setAttribute(ROLE_SESSION_KEY, role);
        session.setAttribute(USER_ID_SESSION_KEY, userId);
        logger.info("Session data stored for key '{}': {}", sessionKey, sessionData);
    }

    // Lấy dữ liệu session của order
    public Map<String, Object> retrieveSessionData(HttpSession session, String role, String userId) {
        String sessionKey = getSessionKey(role, userId);
        Map<String, Object> sessionData = (Map<String, Object>) session.getAttribute(sessionKey);

        if (sessionData == null) {
            logger.warn("No session data found for key '{}'", sessionKey);
            return Collections.emptyMap();
        }

        logger.info("Session data retrieved for key '{}': {}", sessionKey, sessionData);
        return sessionData;
    }

    // Lưu dữ liệu tính toán vào session
    public void storeCalculationSessionData(
            HttpSession session, String role, String userId, Map<String, BigDecimal> calculationData) {
        String sessionKey = getSessionKey(role, userId) + "_calculation";
        session.setAttribute(sessionKey, calculationData);
        logger.info("Calculation session data stored for key '{}': {}", sessionKey, calculationData);
    }

    // Lấy dữ liệu tính toán từ session
    public Map<String, BigDecimal> retrieveCalculationSessionData(HttpSession session, String role, String userId) {
        String sessionKey = getSessionKey(role, userId) + "_calculation";
        Map<String, BigDecimal> calculationData = (Map<String, BigDecimal>) session.getAttribute(sessionKey);

        if (calculationData == null) {
            logger.warn("No calculation session data found for key '{}'", sessionKey);
            return Collections.emptyMap();
        }

        logger.info("Calculation session data retrieved for key '{}': {}", sessionKey, calculationData);
        return calculationData;
    }

    // Lấy role từ session
    public String getRoleFromSession(HttpSession session) {
        String role = (String) session.getAttribute(ROLE_SESSION_KEY);
        if (role == null) {
            logger.warn("No role found in session");
        }
        return role;
    }

    // Lấy userId từ session
    public String getUserIdFromSession(HttpSession session) {
        String userId = (String) session.getAttribute(USER_ID_SESSION_KEY);
        if (userId == null) {
            logger.warn("No userId found in session");
        }
        return userId;
    }

    private String getSessionKey(String role, String userId) {
        switch (role) {
            case "CUSTOMER":
                return "customer_" + userId;
            case "DELIVERING_STAFF":
                return "staff_" + userId;
            case "MANAGER":
                return "account_" + userId;
            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }
    }
}
