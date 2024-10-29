package com.koi_express.controller.staff_assign;

import com.koi_express.dto.request.AssignOrderRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.service.staff_assignment.StaffAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignment")
@RequiredArgsConstructor
public class StaffAssignmentController {

    private final StaffAssignmentService staffAssignmentService;

    @PostMapping("/assign")
    public ResponseEntity<ApiResponse<String>> assignOrder(@RequestBody AssignOrderRequest request) {
        try {
            String message = staffAssignmentService.assignOrder(request.getOrderId());
            return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), message), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to assign order", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/auto-assign")
    public ResponseEntity<ApiResponse<String>> autoAssignOrder(@RequestBody AssignOrderRequest request) {
        try {
            String message = staffAssignmentService.assignOrder(request.getOrderId());
            return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), message), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to auto-assign order", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
