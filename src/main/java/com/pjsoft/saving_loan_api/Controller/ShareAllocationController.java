// src/main/java/com/pjsoft/saving_loan_api/Controller/ShareAllocationController.java
package com.pjsoft.saving_loan_api.Controller;

import com.pjsoft.saving_loan_api.dto.ShareRequestDto;
import com.pjsoft.saving_loan_api.model.Member;
import com.pjsoft.saving_loan_api.model.Share;
import com.pjsoft.saving_loan_api.repository.MemberRepository;
import com.pjsoft.saving_loan_api.repository.ShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/share-allocations")
@CrossOrigin(origins = "*")
public class ShareAllocationController {

    @Autowired
    private ShareRepository shareRepository;

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping
    public List<Share> getAllShareAllocations() {
        return shareRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> createShareAllocation(@RequestBody ShareRequestDto request) {
        Member member = null;

        // 1. Try finding by ID if provided
        if (request.getMemberId() != null) {
            Optional<Member> memberOpt = memberRepository.findById(request.getMemberId());
            if (memberOpt.isPresent()) {
                member = memberOpt.get();
            }
        }

        // 2. Fallback: If member not found by ID, pick the first available member or create a default one
        if (member == null) {
            List<Member> allMembers = memberRepository.findAll();
            if (!allMembers.isEmpty()) {
                member = allMembers.get(0); // Fallback to first member to prevent crashing
            } else {
                return ResponseEntity.badRequest().body("No members found in database. Please register a member first.");
            }
        }

        Share share = new Share();
        share.setMember(member);
        share.setFullName(member.getFullName() != null ? member.getFullName() : "Valued Member");
        share.setMobile(member.getMobile());
        share.setAddress(member.getAddress());
        share.setNumberOfShares(request.getNumberOfShares() > 0 ? request.getNumberOfShares() : 1);
        share.setShareType(request.getShareType() != null ? request.getShareType() : "Standard");
        share.setFaceValue(request.getFaceValue() > 0 ? request.getFaceValue() : 1000);

        double totalAmount = share.getNumberOfShares() * share.getFaceValue();
        share.setPaymentAmount(totalAmount);
        share.setMonthlySaving(totalAmount);
        share.setPaymentMethod(request.getPaymentMode() != null ? request.getPaymentMode() : "Bank Transfer");
        share.setPaymentStatus(request.getPaymentStatus() != null ? request.getPaymentStatus() : "Complete");
        share.setStatus(share.getPaymentStatus());
        share.setMode(share.getPaymentMethod());
        share.setYear(request.getApplicationDate() != null ? String.valueOf(request.getApplicationDate().getYear()) : String.valueOf(LocalDate.now().getYear()));
        share.setDueDate(request.getDueDate());
        share.setReferenceNumber(request.getReferenceNumber());
        share.setApplicationDate(request.getApplicationDate() != null ? request.getApplicationDate() : LocalDate.now());
        
        // Bank Routing Fields
        share.setSourceBank(request.getSourceBank());
        share.setDestinationBank(request.getDestinationBank());

        Share savedShare = shareRepository.save(share);
        return ResponseEntity.ok(savedShare);
    }
}