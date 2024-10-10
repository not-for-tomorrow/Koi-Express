package com.koi_express.controller;

import com.koi_express.dto.response.ApiResponse;
import com.koi_express.service.staffAssignment.StaffAssignmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assignment")
public class StaffAssignmentController {

    @Autowired
    private StaffAssignmentService staffAssignmentService;

    @PostMapping("/assign")
    public ResponseEntity<String> assignOrder(@RequestBody Long orderId) throws Exception {

        String message = staffAssignmentService.assignOrder(orderId);
        return ResponseEntity.ok(message);
    }

    public ResponseEntity<ApiResponse<String>> autoAssignOrder(Long orderId) throws Exception {
        String message = staffAssignmentService.assignOrder(orderId);
        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), message), HttpStatus.OK);
    }
}
