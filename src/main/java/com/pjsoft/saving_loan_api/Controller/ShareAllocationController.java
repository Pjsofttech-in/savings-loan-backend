package com.pjsoft.saving_loan_api.Controller;

import com.pjsoft.saving_loan_api.Respository.MemberRepository;
import com.pjsoft.saving_loan_api.Respository.ShareRepository;
import com.pjsoft.saving_loan_api.model.Member;
import com.pjsoft.saving_loan_api.model.Share;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/shares")
@CrossOrigin(origins = "*")
public class ShareAllocationController {
    private final ShareRepository shareRepository;
    private final MemberRepository memberRepository;

    public ShareAllocationController(ShareRepository shareRepository, MemberRepository memberRepository) {
        this.shareRepository = shareRepository;
        this.memberRepository = memberRepository;
    }

    @GetMapping
    public List<ShareAllocationResponse> getAllocations() {
        return shareRepository.findAll().stream().map(ShareAllocationResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<ShareAllocationResponse> createAllocation(@RequestBody ShareAllocationRequest request) {
        validate(request);
        Member member = memberRepository.findById(request.memberId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Member user ID was not found."));

        LocalDate applicationDate = parseDate(request.applicationDate(), "Application date");
        LocalDate dueDate = request.dueDate() == null || request.dueDate().isBlank()
                ? null
                : parseDate(request.dueDate(), "Due date");

        BigDecimal total = BigDecimal.valueOf(request.faceValue())
                .multiply(BigDecimal.valueOf(request.numberOfShares()))
                .setScale(2, RoundingMode.HALF_UP);

        Share allocation = new Share();
        allocation.setMember(member);
        allocation.setFullName(member.getFullName());
        allocation.setMobile(member.getMobile());
        allocation.setAddress(member.getAddress());
        allocation.setNumberOfShares(request.numberOfShares());
        allocation.setShareType(request.shareType().trim());
        allocation.setFaceValue(request.faceValue());
        allocation.setMonthlySaving(0);
        allocation.setPaymentAmount(total.doubleValue());
        allocation.setPaymentMethod(request.paymentMode());
        allocation.setPaymentStatus(request.paymentStatus());
        allocation.setDueDate(dueDate);
        allocation.setReferenceNumber(request.referenceNumber());
        allocation.setApplicationDate(applicationDate);

        Share saved = shareRepository.save(allocation);
        return ResponseEntity.status(HttpStatus.CREATED).body(ShareAllocationResponse.from(saved));
    }

    private void validate(ShareAllocationRequest request) {
        if (request == null || request.memberId() == null || request.memberId() < 1
                || request.numberOfShares() < 1 || !Double.isFinite(request.faceValue()) || request.faceValue() <= 0
                || blank(request.shareType()) || blank(request.applicationDate())
                || !List.of("Complete", "Pending", "Refunded").contains(request.paymentStatus())
                || !List.of("Cash", "UPI", "Bank Transfer", "Cheque").contains(request.paymentMode())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Required share allocation details are missing or invalid.");
        }
        if ("Pending".equals(request.paymentStatus()) && blank(request.dueDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Due date is required for pending payments.");
        }
        if (!"Cash".equals(request.paymentMode()) && blank(request.referenceNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transaction reference is required for non-cash payments.");
        }
    }

    private LocalDate parseDate(String value, String label) {
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, label + " must use YYYY-MM-DD format.");
        }
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    public record ShareAllocationRequest(
            Long memberId,
            String applicationDate,
            String shareType,
            int numberOfShares,
            double faceValue,
            String paymentStatus,
            String dueDate,
            String paymentMode,
            String referenceNumber
    ) {}

    public record ShareAllocationResponse(
            Long id,
            Long memberId,
            String memberName,
            String mobile,
            String shareType,
            int numberOfShares,
            double faceValue,
            double paymentAmount,
            String paymentStatus,
            String dueDate,
            String paymentMethod,
            String referenceNumber,
            String applicationDate
    ) {
        private static ShareAllocationResponse from(Share share) {
            return new ShareAllocationResponse(
                    share.getId(),
                    share.getMember().getId(),
                    share.getFullName(),
                    share.getMobile(),
                    share.getShareType(),
                    share.getNumberOfShares(),
                    share.getFaceValue(),
                    share.getPaymentAmount(),
                    share.getPaymentStatus(),
                    share.getDueDate() == null ? null : share.getDueDate().toString(),
                    share.getPaymentMethod(),
                    share.getReferenceNumber(),
                    share.getApplicationDate() == null ? null : share.getApplicationDate().toString()
            );
        }
    }
}
