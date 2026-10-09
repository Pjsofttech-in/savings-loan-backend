package com.pjsoft.saving_loan_api.controller;

import com.pjsoft.saving_loan_api.model.Share;
import com.pjsoft.saving_loan_api.repository.MemberRepository;
import com.pjsoft.saving_loan_api.repository.ShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}