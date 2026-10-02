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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
