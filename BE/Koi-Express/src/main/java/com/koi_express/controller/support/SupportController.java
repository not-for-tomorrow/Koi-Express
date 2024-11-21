package com.koi_express.controller.support;

import com.koi_express.dto.request.SupportCreateRequest;
import com.koi_express.dto.request.SupportRequest;
import com.koi_express.jwt.JwtUtil;
import com.koi_express.service.support.SupportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support-request")
@RequiredArgsConstructor
public class SupportController {

    private final SupportService supportService;
    private final JwtUtil jwtUtil;

    @PostMapping("/create")
    public ResponseEntity<SupportRequest> createSupportRequest(@RequestBody @Valid SupportCreateRequest request, @RequestHeader("Authorization") String token) {

        if (token == null || !token.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid Authorization header format. Expected 'Bearer <token>'");
        }

        String jwt = token.substring(7);
        String customerId = jwtUtil.extractCustomerId(jwt);
        Long parsedCustomerId = Long.parseLong(customerId);

        SupportRequest response = supportService.createSupportRequest(request, parsedCustomerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
