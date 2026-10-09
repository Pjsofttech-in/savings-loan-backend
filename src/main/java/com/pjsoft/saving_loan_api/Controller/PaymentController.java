package com.pjsoft.saving_loan_api.controller;

import com.pjsoft.saving_loan_api.repository.MemberRepository;
import com.pjsoft.saving_loan_api.service.RazorpayPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private RazorpayPaymentService razorpayPaymentService;

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getConfig() {
        return ResponseEntity.ok(razorpayPaymentService.checkoutConfiguration());
    }

    @PostMapping("/create-order")
    public ResponseEntity<Map<String, Object>> createOrder() {
        return ResponseEntity.ok(razorpayPaymentService.createOrder());
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyPayment(@RequestBody Map<String, String> payload) {
        String orderId = payload.get("orderId");
        String paymentId = payload.get("paymentId");
        String signature = payload.get("signature");

        razorpayPaymentService.verifyCapturedPayment(orderId, paymentId, signature);
        return ResponseEntity.ok(Map.of("success", true, "message", "Payment verified successfully"));
    }

    @PostMapping("/update-fee")
    public ResponseEntity<BigDecimal> updateFee(@RequestBody Map<String, Object> payload) {
        BigDecimal amount = new BigDecimal(payload.get("amount").toString());
        String adminPassword = (String) payload.get("adminPassword");
        BigDecimal updated = razorpayPaymentService.updateRegistrationFee(amount, adminPassword);
        return ResponseEntity.ok(updated);
    }
}