// src/main/java/com/pjsoft/saving_loan_api/controller/PaymentController.java
package com.pjsoft.saving_loan_api.Controller;

import com.pjsoft.saving_loan_api.model.Member;
import com.pjsoft.saving_loan_api.model.Share;
import com.pjsoft.saving_loan_api.repository.MemberRepository;
import com.pjsoft.saving_loan_api.repository.ShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    @Transactional
    public ResponseEntity<Map<String, Object>> completeMemberRegistration(
            @RequestParam(value = "fullName", required = false) String fullName,
            @RequestParam(value = "fatherName", required = false) String fatherName,
            @RequestParam(value = "dob", required = false) String dob,
            @RequestParam(value = "mobile", required = false) String mobile,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam(value = "occupation", required = false) String occupation,
            @RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "memberType", required = false) String memberType,
            @RequestParam(value = "membershipYear", required = false) String membershipYear,
            @RequestParam(value = "nomineeName", required = false) String nomineeName,
            @RequestParam(value = "nomineeRelationship", required = false) String nomineeRelationship,
            @RequestParam(value = "nomineeMobile", required = false) String nomineeMobile,
            // Address fields
            @RequestParam(value = "address", required = false) String address,
            @RequestParam(value = "street", required = false) String street,
            @RequestParam(value = "city", required = false) String city,
            @RequestParam(value = "taluka", required = false) String taluka,
            @RequestParam(value = "district", required = false) String district,
            @RequestParam(value = "state", required = false) String state,
            @RequestParam(value = "pincode", required = false) String pincode,
            // Witness 1 & Witness 2 fields added to prevent 400 Bad Request
            @RequestParam(value = "witnessName", required = false) String witnessName,
            @RequestParam(value = "witnessMemberNo", required = false) String witnessMemberNo,
            @RequestParam(value = "witnessPhone", required = false) String witnessPhone,
            @RequestParam(value = "witnessEmail", required = false) String witnessEmail,
            @RequestParam(value = "witnessIdProofType", required = false) String witnessIdProofType,
            @RequestParam(value = "2witnessName", required = false) String witness2Name,
            @RequestParam(value = "2witnessMemberNo", required = false) String witness2MemberNo,
            @RequestParam(value = "2witnessPhone", required = false) String witness2Phone,
            @RequestParam(value = "2witnessEmail", required = false) String witness2Email,
            @RequestParam(value = "2witnessIdProofType", required = false) String witness2IdProofType,
            // Payment & Files
            @RequestParam(value = "documentType", required = false) String documentType,
            @RequestParam(value = "registrationFee", required = false, defaultValue = "500") Double registrationFee,
            @RequestParam(value = "monthlyFee", required = false, defaultValue = "1000") Double monthlyFee,
            @RequestParam(value = "paymentStatus", required = false, defaultValue = "Complete") String paymentStatus,
            @RequestParam(value = "paymentMode", required = false, defaultValue = "Cash") String paymentMode,
            @RequestParam(value = "transactionId", required = false) String transactionId,
            @RequestParam(value = "sourceBank", required = false) String sourceBank,
            @RequestParam(value = "destinationBank", required = false) String destinationBank,
            @RequestParam(value = "document", required = false) MultipartFile document,
            @RequestParam(value = "witnessDocument", required = false) MultipartFile witnessDocument,
            @RequestParam(value = "witness2Document", required = false) MultipartFile witness2Document
    ) throws IOException {
        if (isBlank(fullName) || isBlank(mobile)) {
            return error(HttpStatus.BAD_REQUEST, "Full name and mobile are required.");
        }
        String cleanMobile = mobile.trim();
        if (memberRepository.existsByMobile(cleanMobile)) {
            return error(HttpStatus.CONFLICT, "A member with mobile " + cleanMobile + " is already registered.");
        }

        // Build address string
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

        // Registration fee entry
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