package com.pjsoft.saving_loan_api.Controller;

import com.pjsoft.saving_loan_api.model.Member;
import com.pjsoft.saving_loan_api.Respository.MemberRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    // Create a new member
    @PostMapping
    public Member createMember(@RequestBody Member member) {
        return memberRepository.save(member);
    }

    // Get all members (Required for Dashboard & List views)
    @GetMapping
    public List<MemberSummary> getAllMembers() {
        return memberRepository.findAll().stream().map(MemberSummary::from).toList();
    }

    // Get a single member by ID
    @GetMapping("/{id}")
    public ResponseEntity<MemberSummary> getMemberById(@PathVariable Long id) {
        Optional<Member> member = memberRepository.findById(id);
        return member.map(MemberSummary::from).map(ResponseEntity::ok)
                     .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update an existing member
    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody Member memberDetails) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            // Update fields as necessary (add or adjust setters based on your Member entity)
            member.setFullName(memberDetails.getFullName());
            member.setMobile(memberDetails.getMobile());
            member.setEmail(memberDetails.getEmail());
            member.setAddress(memberDetails.getAddress());
            member.setStatus(memberDetails.getStatus());
            member.setMemberType(memberDetails.getMemberType());
            member.setMembershipYear(memberDetails.getMembershipYear());
            member.setCity(memberDetails.getCity());
            member.setJoiningDate(memberDetails.getJoiningDate());
            
            Member updatedMember = memberRepository.save(member);
            return ResponseEntity.ok(updatedMember);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete a member
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long id) {
        if (memberRepository.existsById(id)) {
            memberRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public record MemberSummary(
            Long id,
            String fullName,
            String email,
            String mobile,
            String occupation,
            String address,
            String memberType,
            String membershipYear,
            String city,
            String joiningDate,
            String status
    ) {
        private static MemberSummary from(Member member) {
            return new MemberSummary(
                    member.getId(),
                    member.getFullName(),
                    member.getEmail(),
                    member.getMobile(),
                    member.getOccupation(),
                    member.getAddress(),
                    member.getMemberType(),
                    member.getMembershipYear(),
                    member.getCity(),
                    member.getJoiningDate() == null ? null : member.getJoiningDate().toString(),
                    member.getStatus()
            );
        }
    }
}