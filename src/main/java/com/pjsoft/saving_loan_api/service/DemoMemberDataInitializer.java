package com.pjsoft.saving_loan_api.service;

import com.pjsoft.saving_loan_api.Respository.MemberRepository;
import com.pjsoft.saving_loan_api.Respository.ShareRepository;
import com.pjsoft.saving_loan_api.model.Member;
import com.pjsoft.saving_loan_api.model.Share;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@ConditionalOnProperty(name = "app.sample-data.enabled", havingValue = "true")
public class DemoMemberDataInitializer implements CommandLineRunner {
    private final MemberRepository memberRepository;
    private final ShareRepository shareRepository;

    public DemoMemberDataInitializer(MemberRepository memberRepository, ShareRepository shareRepository) {
        this.memberRepository = memberRepository;
        this.shareRepository = shareRepository;
    }

    @Override
    public void run(String... args) {
        List<Member> demoMembers = List.of(
                createMember("Ananya Kulkarni", "ananya.kulkarni@example.test", "9876500001", "Teacher", "Member", "Pune", "Active", "Aarav Kulkarni", "Spouse", "1990-04-12", "2025-01-15"),
                createMember("Rohan Deshmukh", "rohan.deshmukh@example.test", "9876500002", "Shop Owner", "Member", "Mumbai", "Active", "Vikram Deshmukh", "Brother", "1987-09-03", "2025-02-10"),
                createMember("Meera Patil", "meera.patil@example.test", "9876500003", "Accountant", "Senior Member", "Nashik", "Active", "Isha Patil", "Daughter", "1992-12-22", "2025-03-05"),
                createMember("Kiran Jadhav", "kiran.jadhav@example.test", "9876500004", "Farmer", "Member", "Satara", "Suspended", "Sonal Jadhav", "Spouse", "1979-06-18", "2025-04-21"),
                createMember("Neha Joshi", "neha.joshi@example.test", "9876500005", "Nurse", "Senior Member", "Kolhapur", "Active", "Madhav Joshi", "Parent", "1995-02-08", "2025-05-12"),
                createMember("Amit Shinde", "amit.shinde@example.test", "9876500006", "Driver", "Member", "Pune", "Inactive", "Kavita Shinde", "Spouse", "1983-11-30", "2025-06-09"),
                createMember("Sana Shaikh", "sana.shaikh@example.test", "9876500007", "Engineer", "Senior Member", "Nagpur", "Active", "Fatima Shaikh", "Parent", "1991-07-14", "2025-07-17"),
                createMember("Devendra Pawar", "devendra.pawar@example.test", "9876500008", "Tailor", "Member", "Solapur", "Active", "Nikhil Pawar", "Sibling", "1989-03-27", "2025-08-02"),
                createMember("Priya Nair", "priya.nair@example.test", "9876500009", "Consultant", "Senior Member", "Thane", "Active", "Arjun Nair", "Spouse", "1993-10-05", "2025-09-25")
        );

        for (int index = 0; index < demoMembers.size(); index++) {
            Member sample = demoMembers.get(index);
            String legacyMobile = "DEMO-MOBILE-%03d".formatted(index + 1);
            Member member = memberRepository.findByMobile(sample.getMobile())
                    .or(() -> memberRepository.findByMobile(legacyMobile))
                    .orElse(sample);
            if (member != sample) {
                copySampleDetails(sample, member);
            }
            member = memberRepository.save(member);
            List<Share> memberShares = shareRepository.findByMemberId(member.getId());
            for (Share share : memberShares) {
                share.setFullName(member.getFullName());
                share.setMobile(member.getMobile());
                share.setAddress(member.getAddress());
            }
            shareRepository.saveAll(memberShares);
        }

        List<DemoShare> demoShares = List.of(
                new DemoShare(1, "Ordinary Shares", 10, 1000, "Complete", "UPI", "DEMO-SHARE-001", "2025-01-20", null),
                new DemoShare(2, "Ordinary Shares", 5, 1000, "Complete", "Bank Transfer", "DEMO-SHARE-002", "2025-02-14", null),
                new DemoShare(3, "Preference Shares", 8, 1500, "Pending", "UPI", "DEMO-SHARE-003", "2025-03-10", "2025-04-10"),
                new DemoShare(4, "Ordinary Shares", 4, 1000, "Refunded", "Cheque", "DEMO-SHARE-004", "2025-04-25", null),
                new DemoShare(5, "Founder Shares", 12, 2000, "Complete", "Bank Transfer", "DEMO-SHARE-005", "2025-05-16", null),
                new DemoShare(6, "Ordinary Shares", 6, 1000, "Complete", "Cash", "DEMO-SHARE-006", "2025-06-12", null),
                new DemoShare(7, "Preference Shares", 3, 1500, "Complete", "UPI", "DEMO-SHARE-007", "2025-07-20", null),
                new DemoShare(8, "Ordinary Shares", 7, 1000, "Pending", "Cheque", "DEMO-SHARE-008", "2025-08-06", "2025-09-06"),
                new DemoShare(9, "Founder Shares", 2, 2000, "Complete", "Bank Transfer", "DEMO-SHARE-009", "2025-09-29", null)
        );

        for (DemoShare demoShare : demoShares) {
            String mobile = "987650000%d".formatted(demoShare.memberNumber());
            if (demoShare.referenceNumber() != null
                    && shareRepository.existsByReferenceNumber(demoShare.referenceNumber())) {
                continue;
            }
            Member member = memberRepository.findByMobile(mobile)
                    .orElse(null);
            if (member == null) {
                continue;
            }
            Share share = new Share();
            share.setMember(member);
            share.setFullName(member.getFullName());
            share.setMobile(member.getMobile());
            share.setAddress(member.getAddress());
            share.setNumberOfShares(demoShare.numberOfShares());
            share.setShareType(demoShare.shareType());
            share.setFaceValue(demoShare.faceValue());
            share.setMonthlySaving(0);
            share.setPaymentAmount(demoShare.numberOfShares() * demoShare.faceValue());
            share.setPaymentMethod(demoShare.paymentMethod());
            share.setPaymentStatus(demoShare.paymentStatus());
            share.setDueDate(demoShare.dueDate() == null ? null : LocalDate.parse(demoShare.dueDate()));
            share.setReferenceNumber(demoShare.referenceNumber());
            share.setApplicationDate(LocalDate.parse(demoShare.applicationDate()));
            shareRepository.save(share);
        }
    }

    private record DemoShare(
            int memberNumber,
            String shareType,
            int numberOfShares,
            double faceValue,
            String paymentStatus,
            String paymentMethod,
            String referenceNumber,
            String applicationDate,
            String dueDate) {
    }

    private static Member createMember(
            String name,
            String email,
            String mobile,
            String occupation,
            String memberType,
            String city,
            String status,
            String nomineeName,
            String nomineeRelationship,
            String birthDate,
            String joiningDate) {
        Member member = new Member();
        member.setFullName(name);
        member.setEmail(email);
        member.setMobile(mobile);
        member.setOccupation(occupation);
        member.setMemberType(memberType);
        member.setMembershipYear("2025");
        member.setCity(city);
        member.setStatus(status);
        member.setFatherName("Sample Parent");
        member.setBirthDate(LocalDate.parse(birthDate));
        member.setGender("Not specified");
        member.setAddress("Sample address for demonstration, " + city);
        member.setJoiningDate(LocalDate.parse(joiningDate));
        member.setNomineeName(nomineeName);
        member.setNomineeRelationship(nomineeRelationship);
        member.setNomineeMobile("987651" + mobile.substring(mobile.length() - 4));
        member.setDocumentType("Sample document");
        return member;
    }

    private static void copySampleDetails(Member source, Member target) {
        target.setFullName(source.getFullName());
        target.setEmail(source.getEmail());
        target.setMobile(source.getMobile());
        target.setOccupation(source.getOccupation());
        target.setMemberType(source.getMemberType());
        target.setMembershipYear(source.getMembershipYear());
        target.setCity(source.getCity());
        target.setStatus(source.getStatus());
        target.setFatherName(source.getFatherName());
        target.setBirthDate(source.getBirthDate());
        target.setGender(source.getGender());
        target.setAddress(source.getAddress());
        target.setJoiningDate(source.getJoiningDate());
        target.setNomineeName(source.getNomineeName());
        target.setNomineeRelationship(source.getNomineeRelationship());
        target.setNomineeMobile(source.getNomineeMobile());
        target.setDocumentType(source.getDocumentType());
    }
}
