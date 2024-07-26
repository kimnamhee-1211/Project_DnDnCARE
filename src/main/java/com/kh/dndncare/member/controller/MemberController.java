package com.kh.dndncare.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.service.MemberService;
import com.kh.dndncare.member.model.vo.Member;

@SessionAttributes("loginUser")
@Controller
public class MemberController {
	
	 @Autowired
	 private MemberService mService;
	 
	@GetMapping("loginView.me")
	public String loginView() {
		return "login";
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

	
	@GetMapping("enroll1View.me")
	public String enroll1View() {
		return "enroll1";
	}
	
	@GetMapping("enroll2View.me")
	public String enroll2View() {
		return "enroll2";
	}	
	
	@GetMapping("enroll3View.me")
	public String enroll3View() {
		return "enroll3";
	}
	
	
	@GetMapping("enroll31View.me")
	public String enroll31View() {
		return "enroll3_1";
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
	
}



	












