package com.pjsoft.saving_loan_api;

import com.pjsoft.saving_loan_api.Respository.MemberRepository;
import com.pjsoft.saving_loan_api.Respository.ShareRepository;
import com.pjsoft.saving_loan_api.model.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
class SavingLoanApiApplicationTests {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private MemberRepository memberRepository;

	@Autowired
	private ShareRepository shareRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void exposesRegistrationPaymentConfiguration() throws Exception {
		mockMvc.perform(get("/api/payments/config"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.amount").value(0))
				.andExpect(jsonPath("$.feeConfigured").value(false))
				.andExpect(jsonPath("$.ready").value(false));
	}

	@Test
	@Transactional
	void allowsAdminToSetPersistentRegistrationFee() throws Exception {
		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
					.put("/api/payments/registration-fee")
					.header("X-Admin-Password", "test-admin-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"amount\":1250.50}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.amount").value(1250.50));

		mockMvc.perform(get("/api/payments/config"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.amount").value(1250.50))
				.andExpect(jsonPath("$.feeConfigured").value(true));
	}

	@Test
	void rejectsRegistrationFeeChangesWithoutAdminPassword() throws Exception {
		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
					.put("/api/payments/registration-fee")
					.header("X-Admin-Password", "incorrect")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"amount\":1250}"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void rejectsInvalidRegistrationFee() throws Exception {
		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
					.put("/api/payments/registration-fee")
					.header("X-Admin-Password", "test-admin-password")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"amount\":0}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void returnsPersistedMemberDetailsWithoutSensitiveFields() throws Exception {
		Member member = new Member();
		member.setFullName("Member Detail Test");
		member.setMobile("9876543210");
		member.setFatherName("Parent Name");
		member.setBirthDate(LocalDate.of(1990, 4, 12));
		member.setNomineeName("Nominee Name");
		member.setNomineeRelationship("Spouse");
		member.setNomineeMobile("9876543211");
		member.setStatus("Suspended");
		member.setPaymentId("pay_test_123");
		member.setDocumentType("Aadhaar Card");
		member.setPasswordHash("hashed-value");
		member = memberRepository.save(member);

		mockMvc.perform(get("/api/members"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].nomineeName").doesNotExist())
				.andExpect(jsonPath("$[*].paymentId").doesNotExist());

		mockMvc.perform(get("/api/members/{id}", member.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.fatherName").value("Parent Name"))
				.andExpect(jsonPath("$.birthDate").value("1990-04-12"))
				.andExpect(jsonPath("$.nomineeRelationship").value("Spouse"))
				.andExpect(jsonPath("$.status").value("Suspended"))
				.andExpect(jsonPath("$.paymentId").value("pay_test_123"))
				.andExpect(jsonPath("$.passwordHash").doesNotExist())
				.andExpect(jsonPath("$.verificationDocument").doesNotExist());
	}

	@Test
	void rejectsUnsupportedRegistrationDocument() throws Exception {
		MockMultipartFile document = new MockMultipartFile(
				"document", "proof.txt", "text/plain", "proof".getBytes(StandardCharsets.UTF_8));

		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
					.multipart("/api/payments/complete")
					.file(document)
					.param("orderId", "order_test")
					.param("paymentId", "payment_test")
					.param("signature", "signature")
					.param("fullName", "Test Member")
					.param("mobile", "9876543210")
					.param("password", "password123")
					.param("nomineeName", "Test Nominee")
					.param("nomineeRelationship", "Parent")
					.param("nomineeMobile", "9876543211")
					.param("documentType", "Aadhaar Card"))
				.andExpect(status().isBadRequest());
	}

	@Test
	void createsShareAllocationForGeneratedMemberId() throws Exception {
		Member member = new Member();
		member.setFullName("Test Member");
		member.setMobile("9876543210");
		member = memberRepository.save(member);

		String request = """
				{
				  "memberId": %d,
				  "applicationDate": "2026-10-02",
				  "shareType": "Ordinary Shares",
				  "numberOfShares": 2,
				  "faceValue": 125.5,
				  "paymentStatus": "Complete",
				  "paymentMode": "Cash"
				}
				""".formatted(member.getId());

		mockMvc.perform(post("/api/shares")
					.contentType(MediaType.APPLICATION_JSON)
					.content(request))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.memberId").value(member.getId()))
				.andExpect(jsonPath("$.memberName").value("Test Member"))
				.andExpect(jsonPath("$.paymentAmount").value(251.0));

		org.junit.jupiter.api.Assertions.assertEquals(1, shareRepository.count());
	}

}
