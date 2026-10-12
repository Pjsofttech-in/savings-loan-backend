package com.pjsoft.saving_loan_api.repository;

import com.pjsoft.saving_loan_api.model.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShareRepository extends JpaRepository<Share, Long> {

    @Query("SELECT s FROM Share s WHERE " +
           "(:year IS NULL OR s.year = :year) AND " +
           "(:status IS NULL OR s.status = :status) AND " +
           "(:mode IS NULL OR s.mode = :mode) AND " +
           "(:dueDate IS NULL OR s.dueDate = :dueDate)")
    List<Share> findByFilters(
            @Param("year") String year,
            @Param("status") String status,
            @Param("mode") String mode,
            @Param("dueDate") String dueDate
    );

    List<Share> findByMemberId(Long memberId);
    boolean existsByReferenceNumber(String referenceNumber);
}