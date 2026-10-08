package com.pjsoft.saving_loan_api.service;

import com.pjsoft.saving_loan_api.Respository.RegistrationFeeRepository;
import com.pjsoft.saving_loan_api.model.RegistrationFee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class RazorpayPaymentService {
    private final RestClient restClient = RestClient.create();
    private final RegistrationFeeRepository registrationFeeRepository;

    @Value("${app.razorpay.key-id:}")
    private String keyId;

    @Value("${app.razorpay.key-secret:}")
    private String keySecret;

    @Value("${app.admin.password:}")
    private String adminPassword;

    public RazorpayPaymentService(RegistrationFeeRepository registrationFeeRepository) {
        this.registrationFeeRepository = registrationFeeRepository;
    }

    public Map<String, Object> checkoutConfiguration() {
        BigDecimal fee = getConfiguredRegistrationFee();
        Map<String, Object> configuration = new HashMap<>();
        configuration.put("keyId", keyId);
        configuration.put("amount", fee == null ? BigDecimal.ZERO : fee);
        configuration.put("feeConfigured", fee != null);
        configuration.put("ready", fee != null && !keyId.isBlank() && !keySecret.isBlank());
        return configuration;
    }

    public BigDecimal updateRegistrationFee(BigDecimal amount, String providedAdminPassword) {
        requireAdminPassword(providedAdminPassword);
        BigDecimal validatedAmount = validateRegistrationFee(amount);
        RegistrationFee fee = registrationFeeRepository.findById(RegistrationFee.SETTINGS_ID)
                .orElseGet(RegistrationFee::new);
        fee.setAmount(validatedAmount);
        return registrationFeeRepository.save(fee).getAmount();
    }

    public Map<String, Object> createOrder() {
        requireCredentials();
        long amountInPaise;
        try {
            amountInPaise = getRequiredRegistrationFee()
                    .setScale(2, RoundingMode.UNNECESSARY)
                    .movePointRight(2)
                    .longValueExact();
        } catch (ArithmeticException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration fee must have at most two decimal places.");
        }

        try {
            return restClient.post()
                    .uri("https://api.razorpay.com/v1/orders")
                    .headers(headers -> headers.setBasicAuth(keyId, keySecret))
                    .body(Map.of(
                            "amount", amountInPaise,
                            "currency", "INR",
                            "receipt", "member-" + UUID.randomUUID().toString().replace("-", "")
                    ))
                    .retrieve()
                    .body(Map.class);
        } catch (RuntimeException error) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unable to create a Razorpay order.");
        }
    }

    public void verifyCapturedPayment(String orderId, String paymentId, String signature) {
        requireCredentials();
        if (orderId == null || paymentId == null || signature == null || !isValidSignature(orderId, paymentId, signature)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment signature is invalid.");
        }

        try {
            Map payment = restClient.get()
                    .uri("https://api.razorpay.com/v1/payments/{paymentId}", paymentId)
                    .headers(headers -> headers.setBasicAuth(keyId, keySecret))
                    .retrieve()
                    .body(Map.class);

            if (payment == null
                    || !"captured".equals(payment.get("status"))
                    || !orderId.equals(payment.get("order_id"))) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment has not been captured for this order.");
            }
        } catch (ResponseStatusException error) {
            throw error;
        } catch (RuntimeException error) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Unable to verify the Razorpay payment.");
        }
    }

    private BigDecimal getRequiredRegistrationFee() {
        BigDecimal fee = getConfiguredRegistrationFee();
        if (fee == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Registration fee is not configured.");
        }
        return fee;
    }

    private BigDecimal getConfiguredRegistrationFee() {
        return registrationFeeRepository.findById(RegistrationFee.SETTINGS_ID)
                .map(RegistrationFee::getAmount)
                .filter(amount -> amount.signum() > 0)
                .orElse(null);
    }

    private static BigDecimal validateRegistrationFee(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration fee must be greater than zero.");
        }
        try {
            BigDecimal normalizedAmount = amount.setScale(2, RoundingMode.UNNECESSARY);
            if (normalizedAmount.precision() > 12) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration fee is too large.");
            }
            return normalizedAmount;
        } catch (ArithmeticException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration fee must have at most two decimal places.");
        }
    }

    private void requireAdminPassword(String providedPassword) {
        if (adminPassword.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Admin password is not configured on the backend.");
        }
        if (providedPassword == null || !MessageDigest.isEqual(
                adminPassword.getBytes(StandardCharsets.UTF_8),
                providedPassword.getBytes(StandardCharsets.UTF_8))) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin password is incorrect.");
        }
    }

    private void requireCredentials() {
        if (keyId.isBlank() || keySecret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Razorpay credentials are not configured.");
        }
    }

    private boolean isValidSignature(String orderId, String paymentId, String signature) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] expected = mac.doFinal((orderId + "|" + paymentId).getBytes(StandardCharsets.UTF_8));
            byte[] received = HexFormat.of().parseHex(signature);
            return MessageDigest.isEqual(expected, received);
        } catch (Exception error) {
            return false;
        }
    }
}