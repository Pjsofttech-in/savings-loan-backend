package com.pjsoft.saving_loan_api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "registration_fee")
public class RegistrationFee {
    public static final String SETTINGS_ID = "registration";

    @Id
    private String id = SETTINGS_ID;

    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    public RegistrationFee() {
    }

    public RegistrationFee(BigDecimal amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
