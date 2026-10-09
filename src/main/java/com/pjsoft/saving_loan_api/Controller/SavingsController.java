package com.pjsoft.saving_loan_api.controller;

import com.pjsoft.saving_loan_api.model.Savings;
import com.pjsoft.saving_loan_api.repository.SavingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/savings")
@CrossOrigin(origins = "*")
public class SavingsController {

    @Autowired
    private SavingsRepository savingsRepository;

    @GetMapping
    public List<Savings> getAllSavings() {
        return savingsRepository.findAll();
    }
}