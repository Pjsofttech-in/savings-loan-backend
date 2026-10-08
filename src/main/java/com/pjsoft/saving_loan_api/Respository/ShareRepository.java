package com.pjsoft.saving_loan_api.Respository;

import com.pjsoft.saving_loan_api.model.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {
    List<Share> findByMemberId(Long memberId);
    boolean existsByReferenceNumber(String referenceNumber);
}