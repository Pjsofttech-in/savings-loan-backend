package com.pjsoft.saving_loan_api.controller;

import com.pjsoft.saving_loan_api.model.Member;
import com.pjsoft.saving_loan_api.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@CrossOrigin(origins = "*") // Allows your React frontend to fetch data freely
public class MemberController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping
    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }
}
