package com.kh.dndncare.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.service.MemberService;
import com.kh.dndncare.member.model.vo.Member;

import jakarta.servlet.http.HttpSession;

@SessionAttributes("loginUser")
@Controller
public class MemberController {
	
	 @Autowired
	 private MemberService mService;
	 
	 @Autowired
	 private BCryptPasswordEncoder bCrypt;
	 
	@GetMapping("loginView.me")
	public String loginView() {
		return "login";
	}


	@GetMapping("myInfo.me")
	public String myInfo() {		//마이페이지 확인용
		return "myInfo";
	}


	
	@PostMapping("login.me")
	public String login(@ModelAttribute Member m, Model model) {
		Member loginUser = mService.login(m);
		
//		if(bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
//			model.addAttribute("loginUser",loginUser);
//			return "redirect:home.do";
//		}else {
//			throw new MemberException("로그인을 실패하였습니다.");
//		}		
		
		System.out.println(loginUser);
		if(loginUser !=null) { // 회원가입 기능 구현 전 암호화안한 테스트용
			model.addAttribute("loginUser",loginUser);
			return "redirect:home.do";
		}else {
			throw new MemberException("로그인에 실패했습니다");
		}	
	}
	
	@GetMapping("logout.me")
	public String logout(SessionStatus status) {
		status.setComplete();
		return "redirect:home.do";
	}
	// 임시버튼 : 간병인 메인페이지로 가기 
	@GetMapping("caregiverMain.me")
	public String caregiverMain() {
		return "caregiverMain";
	}
	
	// 임시버튼 : 환자 메인페이지로 가기
	@GetMapping("patientMain.me")
	public String patientMain() {
		return "patientMain";
	}

	
	//회원가입 페이지 이동
	@GetMapping("enroll1View.me")
	public String enroll1View() {
		return "enroll1";
	}
	
	
	//회원가입	 검증
	@PostMapping("idCheck.me")
	@ResponseBody
	public String idCheck(@RequestParam("id") String id) {		
		int result = mService.idCheck(id);	
		if(result == 0) {
			return "usable";
		}else{
			return "unusable";
		}

	}
	
	//회원가입
	@PostMapping("enroll.me")
	public String enroll(@ModelAttribute Member m,
						@RequestParam("postcode") String postcode, @RequestParam("roadAddress") String roadAddress,@RequestParam("detailAddress") String detailAddress,
						@RequestParam("email") String email, @RequestParam("emailDomain") String emailDomain, 
						HttpSession session, Model model) {
		
		System.out.println(m);
		
		//간병인/환자 택
		//String memberCategory = (String) session.getAttribute(memberCategory);
		//m.setMemberCategory(memberCategory);
		String memberPwd = bCrypt.encode(m.getMemberPwd());
		m.setMemberPwd(memberPwd);
		String memberAddress = postcode +"//"+ roadAddress +"//"+ detailAddress;
		m.setMemberAddress(memberAddress);
		String memberEmail = email + "@" + emailDomain;
		m.setMemberEmail(memberEmail);
		
		model.addAttribute("m", m);
		int result = mService.enroll(m);
		if(result > 0) {
			return "enroll2";
		}else {
			throw new MemberException("회원가입에 실패했습니다.");
		}
		
		
		
	}
	
	
	//타입별 회원가입 페이지 이동
	@GetMapping("enroll2View.me")
	public String enrol21View(@ModelAttribute Member m) {
		System.out.println(m);
		String memberCategory = m.getMemberCategory();
		if(memberCategory.equals("c")) {
			return "enroll3";
		}else {
			return "enroll5";
		}
		
	}
	
	
	//간병인 회원가입 페이지 이동
	@GetMapping("enrollCaregiver.me")
	public String enrollCaregiver() {
		
		
		
		
		
		
		
		
		return "enroll1";
	}

	
}



	












