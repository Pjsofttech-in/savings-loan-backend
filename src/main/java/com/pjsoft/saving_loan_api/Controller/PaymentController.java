package com.pjsoft.saving_loan_api.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*") // Prevents CORS blocking
public class PaymentController {

    // 1. Get Payment & Fee Configuration
    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getPaymentConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("feeConfigured", true);
        config.put("amount", 500); // Registration fee amount in INR
        config.put("ready", true);
        config.put("keyId", "rzp_test_mockKeyId"); // Replace with live/test Razorpay key if needed
        return ResponseEntity.ok(config);
    }

    // 2. Create Razorpay Payment Order
    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createPaymentOrder() {
        Map<String, Object> order = new HashMap<>();
        order.put("id", "order_mock_" + System.currentTimeMillis());
        order.put("amount", 50000); // Amount in paisa (₹500.00)
        order.put("currency", "INR");
        return ResponseEntity.ok(order);
    }

    // 3. Complete Member Registration & Payment Verification
    @PostMapping("/complete")
    public ResponseEntity<Map<String, Object>> completeMemberRegistration(
            @RequestParam("fullName") String fullName,
            @RequestParam(value = "fatherName", required = false) String fatherName,
            @RequestParam(value = "dob", required = false) String dob,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam("mobile") String mobile,
            @RequestParam("occupation") String occupation,
            @RequestParam(value = "memberType", required = false) String memberType,
            @RequestParam(value = "membershipYear", required = false) String membershipYear,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam("address") String address,
            @RequestParam("password") String password,
            @RequestParam("nomineeName") String nomineeName,
            @RequestParam("nomineeRelationship") String nomineeRelationship,
            @RequestParam("nomineeMobile") String nomineeMobile,
            @RequestParam("documentType") String documentType,
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "paymentId", required = false) String paymentId,
            @RequestParam(value = "signature", required = false) String signature,
            @RequestParam(value = "document", required = false) MultipartFile document
    ) {
        // Save registration and payment record to database here
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Member registration and payment completed successfully!");
        response.put("paymentId", paymentId != null ? paymentId : "DIRECT_REG_" + System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}