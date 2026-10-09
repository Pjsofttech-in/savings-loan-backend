package com.pjsoft.saving_loan_api.controller;

import com.pjsoft.saving_loan_api.model.Share;
import com.pjsoft.saving_loan_api.repository.ShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shares")
@CrossOrigin(origins = "*")
public class ShareController {

    @Autowired
    private ShareRepository shareRepository;

    @GetMapping
    public List<Share> getFilteredShares(
            @RequestParam(required = false) String year,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String dueDate) {
        
        return shareRepository.findByFilters(year, status, mode, dueDate);
    }
}