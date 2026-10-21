package com.pjsoft.saving_loan_api.Controller;

import com.pjsoft.saving_loan_api.model.Member;
import com.pjsoft.saving_loan_api.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*")
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    // Needed by the Shares form ("Member User ID" lookup) and the user portal
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        return memberRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        if (member.getJoiningDate() == null) {
            member.setJoiningDate(LocalDate.now());
        }
        if (member.getStatus() == null) {
            member.setStatus("Active");
        }
        return ResponseEntity.ok(memberRepository.save(member));
    }

    // Needed by the Edit button in the Member list / Registration form (edit mode)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        return memberRepository.findById(id).<ResponseEntity<?>>map(member -> {
            String newMobile = str(body.get("mobile"));
            if (newMobile != null && !newMobile.equals(member.getMobile())
                    && memberRepository.existsByMobile(newMobile)) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(Map.of("message", "Another member already uses mobile " + newMobile));
            }
            if (str(body.get("fullName")) != null) member.setFullName(str(body.get("fullName")));
            if (newMobile != null) member.setMobile(newMobile);
            if (body.containsKey("fatherName")) member.setFatherName(str(body.get("fatherName")));
            if (body.containsKey("email")) member.setEmail(str(body.get("email")));
            if (body.containsKey("gender")) member.setGender(str(body.get("gender")));
            if (body.containsKey("occupation")) member.setOccupation(str(body.get("occupation")));
            if (body.containsKey("city")) member.setCity(str(body.get("city")));
            if (body.containsKey("memberType")) member.setMemberType(str(body.get("memberType")));
            if (body.containsKey("membershipYear")) member.setMembershipYear(str(body.get("membershipYear")));
            if (body.containsKey("nomineeName")) member.setNomineeName(str(body.get("nomineeName")));
            if (body.containsKey("nomineeRelationship")) member.setNomineeRelationship(str(body.get("nomineeRelationship")));
            if (body.containsKey("nomineeMobile")) member.setNomineeMobile(str(body.get("nomineeMobile")));
            if (body.containsKey("status") && str(body.get("status")) != null) member.setStatus(str(body.get("status")));
            String addr = str(body.get("address")) != null ? str(body.get("address")) : str(body.get("street"));
            if (addr != null) member.setAddress(addr);
            String dob = str(body.get("dob"));
            if (dob != null) {
                try { member.setBirthDate(LocalDate.parse(dob)); } catch (Exception ignored) { }
            }
            return ResponseEntity.ok(memberRepository.save(member));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    private static String str(Object o) {
        if (o == null) return null;
        String s = o.toString().trim();
        return s.isEmpty() ? null : s;
    }
}
