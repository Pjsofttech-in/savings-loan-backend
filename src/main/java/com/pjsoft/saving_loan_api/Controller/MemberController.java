package com.pjsoft.saving_loan_api.controller;

import com.pjsoft.saving_loan_api.model.Member;
import com.pjsoft.saving_loan_api.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<Member> getMemberById(@PathVariable Long id) {
        return memberRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        return memberRepository.findById(id).map(member -> {
            if (updates.containsKey("fullName")) member.setFullName((String) updates.get("fullName"));
            if (updates.containsKey("mobile")) member.setMobile((String) updates.get("mobile"));
            if (updates.containsKey("email")) member.setEmail((String) updates.get("email"));
            if (updates.containsKey("occupation")) member.setOccupation((String) updates.get("occupation"));
            if (updates.containsKey("address")) member.setAddress((String) updates.get("address"));
            if (updates.containsKey("city")) member.setCity((String) updates.get("city"));
            if (updates.containsKey("nomineeName")) member.setNomineeName((String) updates.get("nomineeName"));
            if (updates.containsKey("nomineeRelationship")) member.setNomineeRelationship((String) updates.get("nomineeRelationship"));
            if (updates.containsKey("nomineeMobile")) member.setNomineeMobile((String) updates.get("nomineeMobile"));
            
            Member updated = memberRepository.save(member);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }
}