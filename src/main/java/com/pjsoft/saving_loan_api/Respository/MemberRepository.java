package com.pjsoft.saving_loan_api.Respository;

import com.pjsoft.saving_loan_api.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByMobile(String mobile);
    java.util.Optional<Member> findByMobile(String mobile);
}