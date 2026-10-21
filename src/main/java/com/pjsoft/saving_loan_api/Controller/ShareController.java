package com.pjsoft.saving_loan_api.Controller;

import com.pjsoft.saving_loan_api.model.Share;
import com.pjsoft.saving_loan_api.repository.ShareRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate,
            @RequestParam(required = false) Long memberId) {

        // User portal calls /api/shares?memberId=.. - previously this was ignored and everyone's shares were returned
        if (memberId != null) {
            return shareRepository.findByMemberId(memberId);
        }
        return shareRepository.findByFilters(year, status, mode, dueDate == null ? null : dueDate.toString());
    }
}
