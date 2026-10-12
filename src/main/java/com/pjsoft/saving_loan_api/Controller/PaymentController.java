package com.pjsoft.saving_loan_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getPaymentConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("feeConfigured", true);
        config.put("amount", 500);
        config.put("ready", true);
        config.put("keyId", "rzp_test_mockKeyId");
        return ResponseEntity.ok(config);
    }

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createPaymentOrder() {
        Map<String, Object> order = new HashMap<>();
        order.put("id", "order_mock_" + System.currentTimeMillis());
        order.put("amount", 50000);
        order.put("currency", "INR");
        return ResponseEntity.ok(order);
    }

    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> completeMemberRegistration(
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "fatherName", required = false) String fatherName,
            @RequestParam(value = "dob", required = false) String dob,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "mobile", required = false) String mobile,
            @RequestParam(value = "occupation", required = false) String occupation,
            @RequestParam(value = "memberType", required = false) String memberType,
            @RequestParam(value = "membershipYear", required = false) String membershipYear,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "nomineeName", required = false) String nomineeName,
            @RequestParam(value = "nomineeRelationship", required = false) String nomineeRelationship,
            @RequestParam(value = "nomineeMobile", required = false) String nomineeMobile,
            @RequestParam(value = "documentType", required = false) String documentType,
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "paymentId", required = false) String paymentId,
            @RequestParam(value = "signature", required = false) String signature,
            @RequestParam(value = "document", required = false) MultipartFile document
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Member registration and payment completed successfully!");
        return ResponseEntity.ok(response);
    }
}