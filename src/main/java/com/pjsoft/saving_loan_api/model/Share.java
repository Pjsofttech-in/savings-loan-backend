package com.pjsoft.saving_loan_api.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "shares")
public class Share {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "mobile", nullable = false, length = 15)
    private String mobile;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "number_of_shares", nullable = false)
    private int numberOfShares;

    @Column(name = "monthly_saving", nullable = false)
    private double monthlySaving;

    @Column(name = "payment_amount", nullable = false)
    private double paymentAmount;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "application_date")
    private LocalDate applicationDate;

    // --- CONSTRUCTORS ---

    public Share() {}

    public Share(Member member, String fullName, String mobile, String address,
                 int numberOfShares, double monthlySaving, double paymentAmount,
                 String paymentMethod, LocalDate applicationDate) {
        this.member = member;
        this.fullName = fullName;
        this.mobile = mobile;
        this.address = address;
        this.numberOfShares = numberOfShares;
        this.monthlySaving = monthlySaving;
        this.paymentAmount = paymentAmount;
        this.paymentMethod = paymentMethod;
        this.applicationDate = applicationDate;
    }

    // --- GETTERS AND SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getNumberOfShares() { return numberOfShares; }
    public void setNumberOfShares(int numberOfShares) { this.numberOfShares = numberOfShares; }

    public double getMonthlySaving() { return monthlySaving; }
    public void setMonthlySaving(double monthlySaving) { this.monthlySaving = monthlySaving; }

    public double getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(double paymentAmount) { this.paymentAmount = paymentAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDate getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }
}