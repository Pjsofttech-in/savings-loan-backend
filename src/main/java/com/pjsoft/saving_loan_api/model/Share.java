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

    @Column(name = "share_type")
    private String shareType;

    @Column(name = "face_value")
    private double faceValue;

    @Column(name = "monthly_saving", nullable = false)
    private double monthlySaving;

    @Column(name = "payment_amount", nullable = false)
    private double paymentAmount;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_status", length = 30)
    private String paymentStatus;

    @Column(name = "status", length = 30)
    private String status; // Added to support ShareRepository filter

    @Column(name = "mode", length = 50)
    private String mode; // Added to support ShareRepository filter

    @Column(name = "year", length = 10)
    private String year; // Added to support ShareRepository filter

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "reference_number", length = 100)
    private String referenceNumber;

    @Column(name = "application_date")
    private LocalDate applicationDate;

    // --- CONSTRUCTORS ---

    public Share() {}

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

    public String getShareType() { return shareType; }
    public void setShareType(String shareType) { this.shareType = shareType; }

    public double getFaceValue() { return faceValue; }
    public void setFaceValue(double faceValue) { this.faceValue = faceValue; }

    public double getMonthlySaving() { return monthlySaving; }
    public void setMonthlySaving(double monthlySaving) { this.monthlySaving = monthlySaving; }

    public double getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(double paymentAmount) { this.paymentAmount = paymentAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public LocalDate getApplicationDate() { return applicationDate; }
    public void setApplicationDate(LocalDate applicationDate) { this.applicationDate = applicationDate; }
}