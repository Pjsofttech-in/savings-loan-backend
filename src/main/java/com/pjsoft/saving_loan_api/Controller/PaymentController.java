package com.pjsoft.saving_loan_api.controller;

import com.pjsoft.saving_loan_api.model.Member;
import com.pjsoft.saving_loan_api.model.Share;
import com.pjsoft.saving_loan_api.repository.MemberRepository;
import com.pjsoft.saving_loan_api.repository.ShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ShareRepository shareRepository;

    @Value("${app.registration.fee:500.0}")
    private double configuredRegistrationFee;

    @Value("${app.monthly.fee:1000.0}")
    private double configuredMonthlyFee;

    @GetMapping("/config")
    public ResponseEntity<Map<String, Object>> getPaymentConfiguration() {
        Map<String, Object> config = new HashMap<>();
        config.put("feeConfigured", true);
        config.put("amount", configuredRegistrationFee);
        config.put("monthlyFee", configuredMonthlyFee);
        config.put("ready", true);
        config.put("keyId", "rzp_test_mockKeyId");
        return ResponseEntity.ok(config);
    }

    @PostMapping({"/create-order", "/orders"})
    public ResponseEntity<Map<String, Object>> createPaymentOrder() {
        Map<String, Object> order = new HashMap<>();
        order.put("id", "order_mock_" + System.currentTimeMillis());
        order.put("amount", (int) (configuredRegistrationFee * 100)); // Paisa conversion
        order.put("currency", "INR");
        return ResponseEntity.ok(order);
    }

    @PutMapping("/registration-fee")
    public ResponseEntity<Map<String, Object>> saveRegistrationFee(
            @RequestBody(required = false) Map<String, Object> body,
            @RequestHeader(value = "X-Admin-Password", required = false) String adminPassword) {
        
        if (body != null) {
            try {
                if (body.containsKey("amount")) {
                    configuredRegistrationFee = Double.parseDouble(body.get("amount").toString());
                }
                if (body.containsKey("monthlyFee")) {
                    configuredMonthlyFee = Double.parseDouble(body.get("monthlyFee").toString());
                }
            } catch (Exception ignored) {}
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Fee settings updated successfully.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/complete")
    @Transactional
    public ResponseEntity<Map<String, Object>> completeMemberRegistration(HttpServletRequest request) {
        try {
            MultipartHttpServletRequest multipartRequest = null;
            if (request instanceof MultipartHttpServletRequest) {
                multipartRequest = (MultipartHttpServletRequest) request;
            }

            String fullName = resolve(multipartRequest, request, "fullName", "fullname", "full_name");
            String fatherName = resolve(multipartRequest, request, "fatherName", "father_name");
            String dob = resolve(multipartRequest, request, "dob", "birthDate");
            String mobile = resolve(multipartRequest, request, "mobile", "phone", "contactNumber");
            String email = resolve(multipartRequest, request, "email");
            String password = resolve(multipartRequest, request, "password");
            String occupation = resolve(multipartRequest, request, "occupation");
            String gender = resolve(multipartRequest, request, "gender");
            String memberType = resolve(multipartRequest, request, "memberType", "member_type");
            String membershipYear = resolve(multipartRequest, request, "membershipYear", "membership_year", "year");
            String nomineeName = resolve(multipartRequest, request, "nomineeName", "nominee_name");
            String nomineeRelationship = resolve(multipartRequest, request, "nomineeRelationship", "nominee_relationship");
            String nomineeMobile = resolve(multipartRequest, request, "nomineeMobile", "nominee_mobile");
            
            String address = resolve(multipartRequest, request, "address");
            String street = resolve(multipartRequest, request, "street");
            String city = resolve(multipartRequest, request, "city");
            String taluka = resolve(multipartRequest, request, "taluka");
            String district = resolve(multipartRequest, request, "district");
            String state = resolve(multipartRequest, request, "state");
            String pincode = resolve(multipartRequest, request, "pincode", "pinCode");

            String regFeeStr = resolve(multipartRequest, request, "registrationFee", "registration_fee");
            double registrationFee = isBlank(regFeeStr) ? configuredRegistrationFee : Double.parseDouble(regFeeStr);

            String monthlyFeeStr = resolve(multipartRequest, request, "monthlyFee", "monthly_fee");
            double monthlyFee = isBlank(monthlyFeeStr) ? configuredMonthlyFee : Double.parseDouble(monthlyFeeStr);

            String paymentStatus = resolve(multipartRequest, request, "paymentStatus", "payment_status");
            if (isBlank(paymentStatus)) paymentStatus = "Complete";

            String paymentMode = resolve(multipartRequest, request, "paymentMode", "payment_mode", "mode");
            if (isBlank(paymentMode)) paymentMode = "Cash";

            String transactionId = resolve(multipartRequest, request, "transactionId", "transaction_id", "utr");
            String sourceBank = resolve(multipartRequest, request, "sourceBank", "source_bank");
            String destinationBank = resolve(multipartRequest, request, "destinationBank", "destination_bank");
            String documentType = resolve(multipartRequest, request, "documentType", "document_type");

            MultipartFile document = null;
            if (multipartRequest != null) {
                document = multipartRequest.getFile("document");
                if (document == null) document = multipartRequest.getFile("verificationDocument");
            }

            if (isBlank(fullName) || isBlank(mobile)) {
                return error(HttpStatus.BAD_REQUEST, "Full name and mobile are required.");
            }

            String cleanMobile = mobile.trim();
            if (memberRepository.existsByMobile(cleanMobile)) {
                return error(HttpStatus.CONFLICT, "A member with mobile " + cleanMobile + " is already registered.");
            }

            StringBuilder fullAddress = new StringBuilder();
            for (String part : new String[]{!isBlank(street) ? street : address, taluka, district, state}) {
                if (!isBlank(part)) {
                    if (fullAddress.length() > 0) fullAddress.append(", ");
                    fullAddress.append(part.trim());
                }
            }
            if (!isBlank(pincode)) fullAddress.append(" - ").append(pincode.trim());

            Member member = new Member();
            member.setFullName(fullName.trim());
            member.setFatherName(fatherName);
            member.setGender(gender);
            member.setMobile(cleanMobile);
            member.setEmail(email);
            member.setOccupation(occupation);
            member.setAddress(fullAddress.length() > 0 ? fullAddress.toString() : null);
            member.setCity(city);
            member.setMemberType(memberType);
            member.setMembershipYear(membershipYear);
            member.setNomineeName(nomineeName);
            member.setNomineeRelationship(nomineeRelationship);
            member.setNomineeMobile(nomineeMobile);
            member.setDocumentType(documentType);
            member.setStatus("Active");
            member.setJoiningDate(LocalDate.now());

            if (!isBlank(dob)) {
                try { member.setBirthDate(LocalDate.parse(dob)); } catch (Exception ignored) { }
            }
            if (!isBlank(password)) {
                member.setPasswordHash(new BCryptPasswordEncoder().encode(password));
            }
            if (document != null && !document.isEmpty()) {
                member.setVerificationDocument(document.getBytes());
            }

            Member savedMember = memberRepository.save(member);

            Share share = new Share();
            share.setMember(savedMember);
            share.setFullName(savedMember.getFullName());
            share.setMobile(savedMember.getMobile());
            share.setAddress(savedMember.getAddress());
            share.setNumberOfShares(1);
            share.setShareType(memberType != null ? memberType : "Standard");
            share.setFaceValue(registrationFee);
            share.setPaymentAmount(registrationFee);
            share.setMonthlySaving(monthlyFee);
            share.setPaymentMethod(paymentMode);
            share.setPaymentStatus(paymentStatus);
            share.setStatus(paymentStatus);
            share.setMode(paymentMode);
            share.setYear(String.valueOf(LocalDate.now().getYear()));
            share.setReferenceNumber(isBlank(transactionId) ? null : transactionId.trim());
            share.setApplicationDate(LocalDate.now());
            share.setSourceBank(sourceBank);
            share.setDestinationBank(destinationBank);
            shareRepository.save(share);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("memberId", savedMember.getId());
            response.put("message", "Member registered and saved successfully!");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return error(HttpStatus.INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }

    private String resolve(MultipartHttpServletRequest multipartRequest, HttpServletRequest request, String... altNames) {
        for (String name : altNames) {
            if (multipartRequest != null && multipartRequest.getParameter(name) != null && !multipartRequest.getParameter(name).trim().isEmpty()) {
                return multipartRequest.getParameter(name);
            }
            if (request.getParameter(name) != null && !request.getParameter(name).trim().isEmpty()) {
                return request.getParameter(name);
            }
        }
        return null;
    }

    private static boolean isBlank(String v) {
        return v == null || v.trim().isEmpty();
    }

    private static ResponseEntity<Map<String, Object>> error(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("success", false);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}