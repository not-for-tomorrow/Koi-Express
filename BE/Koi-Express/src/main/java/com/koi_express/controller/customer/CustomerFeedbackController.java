package com.koi_express.controller.customer;

import java.util.List;

import com.koi_express.dto.request.FeedbackRequest;
import com.koi_express.entity.customer.CustomerFeedback;
import com.koi_express.jwt.JwtUtil;
import com.koi_express.service.customer.CustomerDetailsService;
import com.koi_express.service.customer.CustomerFeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@Slf4j
public class CustomerFeedbackController {

    private final CustomerFeedbackService feedbackService;
    private final JwtUtil jwtUtil;
    private final CustomerDetailsService userDetailsService;

    @PostMapping("/submitFeedback")
    public ResponseEntity<?> submitFeedback(
            @RequestHeader("Authorization") String token, @Valid @RequestBody FeedbackRequest feedbackRequest) {

        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        String phoneNumber = jwtUtil.extractPhoneNumber(jwtToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(phoneNumber);

        if (!jwtUtil.validateToken(jwtToken, userDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        boolean isOrderDelivered = feedbackService.isOrderDelivered(feedbackRequest.getOrderId());
        if (!isOrderDelivered) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Feedback can only be submitted for delivered orders.");
        }
        try {
            CustomerFeedback feedback = feedbackService.submitFeedback(
                    feedbackRequest.getRating(),
                    feedbackRequest.getTags(),
                    feedbackRequest.getComments(),
                    feedbackRequest.getCustomerId(),
                    feedbackRequest.getOrderId());
            return ResponseEntity.ok(feedback);
        } catch (IllegalArgumentException e) {
            log.error("Error submitting feedback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error submitting feedback: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while submitting feedback: " + e.getMessage());
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<CustomerFeedback>> getFeedbackByOrder(@PathVariable Long orderId) {
        List<CustomerFeedback> feedbackList = feedbackService.getFeedbackByOrder(orderId);
        if (feedbackList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(feedbackList);
        }
        return ResponseEntity.ok(feedbackList);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerFeedback>> getFeedbackByCustomer(@PathVariable Long customerId) {
        List<CustomerFeedback> feedbackList = feedbackService.getFeedbackByCustomer(customerId);
        if (feedbackList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(feedbackList);
        }
        return ResponseEntity.ok(feedbackList);
    }

    @GetMapping("/staff/{staffId}")
    public ResponseEntity<List<CustomerFeedback>> getFeedbackByDeliveringStaff(@PathVariable Long staffId) {
        List<CustomerFeedback> feedbackList = feedbackService.getFeedbackByDeliveringStaff(staffId);
        if (feedbackList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(feedbackList);
        }
        return ResponseEntity.ok(feedbackList);
    }
}
