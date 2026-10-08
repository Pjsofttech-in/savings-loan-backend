package com.pjsoft.saving_loan_api.Respository;

import com.pjsoft.saving_loan_api.model.RegistrationFee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistrationFeeRepository extends JpaRepository<RegistrationFee, String> {
}
