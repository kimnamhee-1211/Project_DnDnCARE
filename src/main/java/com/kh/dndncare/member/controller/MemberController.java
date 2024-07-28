package com.kh.dndncare.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.service.MemberService;
import com.kh.dndncare.member.model.vo.Member;


import jakarta.servlet.http.HttpSession;

@SessionAttributes({"loginUser", "tempMemberCategory"})
@Controller
public class MemberController {
	
	@Autowired
	private MemberService mService;
	
	@GetMapping("{memberType}.me")
	public String selectMemberType(@PathVariable("memberType") String memberType,Model model) {
		String tempMemberCategory;
		String viewName;
		
		switch(memberType) {
		case "patient" :
				tempMemberCategory = "P";
				viewName = "pLogin";
				break;
		case "careGiver" :
				tempMemberCategory = "C";
				viewName = "cLogin";
				break;
		default : return "errorPage";
		}
		
		model.addAttribute("tempMemberCategory", tempMemberCategory);
		return viewName;
		
	}

	@GetMapping("myInfo.me")
	public String myInfo() {		//마이페이지  확인용
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
	public String enroll(@ModelAttribute Member m, 4
						@RequestParam("postcode") String postcode, @RequestParam("roadAddress") String roadAddress,@RequestParam("detailAddress") String detailAddress,
						@RequestParam("memberEmail") String memberEmail, @RequestParam("emailDomain") String emailDomain, 
						HttpSession ssession) {
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return null;
	}

	
	
	@GetMapping("enroll32View.me")
	public String enroll32View() {
		return "enroll3_2";
	}
	
	
	@GetMapping("enroll33View.me")
	public String enroll33View() {
		return "enroll3_3";
	}
	
	@GetMapping("myInfoMatching.me")
	public String myInfoMatching() {		//마이페이지 현재매칭정보 확인용
		return "myInfoMatching";
	}
	
	@GetMapping("myInfoMatchingHistory.me")
	public String myInfoMatchingHistory() {		//마이페이지 매칭 이력 확인용
		return "myInfoMatchingHistory";
	}
	
	@GetMapping("myInfoMatchingReview.me")
	public String myInfoMatchingReview() {		//마이페이지 매칭 이력 확인용
		return "myInfoMatchingReview";
	}
	
	@GetMapping("myInfoBoardList.me")
	public String myInfoBoardList() {		//마이페이지 보드작성 확인용
		return "myInfoBoardList";
	}
	
	@GetMapping("findId.me")
	public String findId() {
		return "findIdPage";
	}
	
	@GetMapping("findPwd.me")
	public String findPwd() {
		return "findPwdPage";
	}
	
	
	
	
	
	
	
	
	
}



	












