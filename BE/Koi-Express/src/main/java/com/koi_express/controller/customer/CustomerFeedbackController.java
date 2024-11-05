package com.koi_express.controller.customer;

import com.koi_express.dto.request.FeedbackRequest;
import com.koi_express.entity.customer.CustomerFeedback;
import com.koi_express.jwt.JwtUtil;
import com.koi_express.service.customer.CustomerFeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
@Slf4j
public class CustomerFeedbackController {

    private final CustomerFeedbackService feedbackService;
    private final JwtUtil jwtUtil;

    @PostMapping("/submitFeedback")
    public ResponseEntity<?> submitFeedback(@RequestBody FeedbackRequest feedbackRequest) {
        try {
            CustomerFeedback feedback = feedbackService.submitFeedback(
                    feedbackRequest.getRating(),
                    feedbackRequest.getTags(),
                    feedbackRequest.getComments(),
                    feedbackRequest.getCustomerId()
            );
            return ResponseEntity.ok(feedback);
        } catch (Exception e) {
            log.error("Error submitting feedback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while submitting feedback: " + e.getMessage());
        }
    }

    @GetMapping("/order/{orderId}")
    public List<CustomerFeedback> getFeedbackByOrder(@PathVariable Long orderId) {
        return feedbackService.getFeedbackByOrder(orderId);
    }

    @GetMapping("/customer/{customerId}")
    public List<CustomerFeedback> getFeedbackByCustomer(@PathVariable Long customerId) {
        return feedbackService.getFeedbackByCustomer(customerId);
    }

    @GetMapping("/staff/{staffId}")
    public List<CustomerFeedback> getFeedbackByDeliveringStaff(@PathVariable Long staffId) {
        return feedbackService.getFeedbackByDeliveringStaff(staffId);
    }
}
