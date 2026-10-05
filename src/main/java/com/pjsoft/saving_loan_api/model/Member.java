package com.pjsoft.saving_loan_api.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender")
    private String gender;

    @Column(name = "email")
    private String email;

    @JsonIgnore
    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "mobile", nullable = false)
    private String mobile;

    @Column(name = "occupation")
    private String occupation;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "status")
    private String status = "Active";

    @Column(name = "member_type")
    private String memberType;

    @Column(name = "membership_year")
    private String membershipYear;

    @Column(name = "city")
    private String city;

    @Column(name = "joining_date")
    private LocalDate joiningDate;

    @Column(name = "nominee_name")
    private String nomineeName;

    @Column(name = "nominee_relationship")
    private String nomineeRelationship;

    @Column(name = "nominee_mobile")
    private String nomineeMobile;

    @Column(name = "payment_id", unique = true)
    private String paymentId;

    @Column(name = "document_type")
    private String documentType;

    @JsonIgnore
    @Lob
    @Column(name = "verification_document", columnDefinition = "LONGBLOB")
    private byte[] verificationDocument;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Share> shares;

    public Member() {}

    // --- GETTERS AND SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMemberType() { return memberType; }
    public void setMemberType(String memberType) { this.memberType = memberType; }

    public String getMembershipYear() { return membershipYear; }
    public void setMembershipYear(String membershipYear) { this.membershipYear = membershipYear; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public LocalDate getJoiningDate() { return joiningDate; }
    public void setJoiningDate(LocalDate joiningDate) { this.joiningDate = joiningDate; }

    @PrePersist
    public void setDefaultJoiningDate() {
        if (joiningDate == null) joiningDate = LocalDate.now();
    }

    public String getNomineeName() { return nomineeName; }
    public void setNomineeName(String nomineeName) { this.nomineeName = nomineeName; }

    public String getNomineeRelationship() { return nomineeRelationship; }
    public void setNomineeRelationship(String nomineeRelationship) { this.nomineeRelationship = nomineeRelationship; }

    public String getNomineeMobile() { return nomineeMobile; }
    public void setNomineeMobile(String nomineeMobile) { this.nomineeMobile = nomineeMobile; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getDocumentType() { return documentType; }
    public void setDocumentType(String documentType) { this.documentType = documentType; }

    public byte[] getVerificationDocument() { return verificationDocument; }
    public void setVerificationDocument(byte[] verificationDocument) { this.verificationDocument = verificationDocument; }

    public List<Share> getShares() { return shares; }
    public void setShares(List<Share> shares) { this.shares = shares; }
}