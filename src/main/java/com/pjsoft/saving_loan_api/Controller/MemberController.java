package com.pjsoft.saving_loan_api.controller;

import com.pjsoft.saving_loan_api.model.Member;
import com.pjsoft.saving_loan_api.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    // --- ADDED: POST endpoint to save new member registrations ---
    @PostMapping
    public ResponseEntity<Member> createMember(@RequestBody Member member) {
        if (member.getJoiningDate() == null) {
            member.setJoiningDate(LocalDate.now());
        }
        if (member.getStatus() == null) {
            member.setStatus("Active");
        }
        Member savedMember = memberRepository.save(member);
        return ResponseEntity.ok(savedMember);
    }
}