package com.pjsoft.saving_loan_api.dto;

import java.time.LocalDate;

public class ShareRequestDto {
    private Long memberId;
    private String memberName;
    private int numberOfShares;
    private String shareType;
    private String bankAccount;
    private double amountPerShare;
    private double faceValue;
    
    // Controller specific fields & methods mapping
    private String paymentMode;
    private String paymentStatus;
    private LocalDate dueDate;
    private String referenceNumber;
    private LocalDate applicationDate;
    private String sourceBank;
    private String destinationBank;

    // Getters and Setters
    public Long getMemberId() {
        return memberId;
    }
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }
    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public int getNumberOfShares() {
        return numberOfShares;
    }
    public void setNumberOfShares(int numberOfShares) {
        this.numberOfShares = numberOfShares;
    }

    public String getShareType() {
        return shareType;
    }
    public void setShareType(String shareType) {
        this.shareType = shareType;
    }

    public String getBankAccount() {
        return bankAccount;
    }
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public double getAmountPerShare() {
        return amountPerShare;
    }
    public void setAmountPerShare(double amountPerShare) {
        this.amountPerShare = amountPerShare;
    }

    public double getFaceValue() {
        return faceValue;
    }
    public void setFaceValue(double faceValue) {
        this.faceValue = faceValue;
    }

    public String getPaymentMode() {
        return paymentMode;
    }
    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }
    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getSourceBank() {
        return sourceBank;
    }
    public void setSourceBank(String sourceBank) {
        this.sourceBank = sourceBank;
    }

    public String getDestinationBank() {
        return destinationBank;
    }
    public void setDestinationBank(String destinationBank) {
        this.destinationBank = destinationBank;
    }
}