package com.pjsoft.saving_loan_api.Controller; 

import com.pjsoft.saving_loan_api.Respository.MemberRepository;
import com.pjsoft.saving_loan_api.model.Member;
import com.pjsoft.saving_loan_api.service.RazorpayPaymentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    private static final long MAX_DOCUMENT_SIZE = 5L * 1024 * 1024;

    private final RazorpayPaymentService paymentService;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PaymentController(RazorpayPaymentService paymentService, MemberRepository memberRepository) {
        this.paymentService = paymentService;
        this.memberRepository = memberRepository;
    }

    @GetMapping("/config")
    public Map<String, Object> getConfiguration() {
        return paymentService.checkoutConfiguration();
    }

    @PostMapping("/orders")
    public Map<String, Object> createOrder() {
        return paymentService.createOrder();
    }

    @PostMapping(value = "/complete", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Member> completeRegistration(
            @RequestParam String orderId,
            @RequestParam String paymentId,
            @RequestParam String signature,
            @RequestParam String fullName,
            @RequestParam String mobile,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String occupation,
            @RequestParam(required = false) String memberType,
            @RequestParam(required = false) String membershipYear,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String joiningDate,
            @RequestParam String password,
            @RequestParam String nomineeName,
            @RequestParam String nomineeRelationship,
            @RequestParam String nomineeMobile,
            @RequestParam(required = false) String fatherName,
            @RequestParam(required = false) String dob,
            @RequestParam(required = false) String gender,
            @RequestParam String documentType,
            @RequestParam MultipartFile document) {
        validateDocument(document);
        paymentService.verifyCapturedPayment(orderId, paymentId, signature);

        Member member = new Member();
        member.setFullName(fullName);
        member.setFatherName(fatherName);
        member.setBirthDate(parseDate(dob));
        member.setGender(gender);
        member.setEmail(email);
        member.setMobile(mobile);
        member.setOccupation(occupation);
        member.setMemberType(memberType);
        member.setMembershipYear(membershipYear);
        member.setCity(city);
        member.setAddress(address);
        member.setJoiningDate(parseDate(joiningDate));
        member.setPasswordHash(passwordEncoder.encode(password));
        member.setNomineeName(nomineeName);
        member.setNomineeRelationship(nomineeRelationship);
        member.setNomineeMobile(nomineeMobile);
        member.setPaymentId(paymentId);
        member.setDocumentType(documentType);
        try {
            member.setVerificationDocument(document.getBytes());
        } catch (IOException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to read the verification document.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(memberRepository.save(member));
    }

    private static void validateDocument(MultipartFile document) {
        if (document == null || document.isEmpty() || document.getSize() > MAX_DOCUMENT_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A verification document up to 5 MB is required.");
        }
        String contentType = document.getContentType();
        if (!MediaType.APPLICATION_PDF_VALUE.equals(contentType)
                && !MediaType.IMAGE_JPEG_VALUE.equals(contentType)
                && !MediaType.IMAGE_PNG_VALUE.equals(contentType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Document must be a PDF, JPG, or PNG.");
        }
    }

    private static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return LocalDate.parse(value);
        } catch (RuntimeException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date must use YYYY-MM-DD format.");
        }
    }
}