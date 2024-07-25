package com.kh.dndncare.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.service.MemberService;
import com.kh.dndncare.member.model.vo.Member;

@Controller
public class MemberController {
	
	 @Autowired
	 private MemberService mService;
	 
	@GetMapping("loginView.me")
	public String loginView() {
		return "login";
	}
	
//	@PostMapping("login.me")
//	public String login(@ModelAttribute Member m, Model model) {
//		Member loginUser = mService.login(m);
//		
////		if(bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
////			model.addAttribute("loginUser",loginUser);
////			return "redirect:home.do";
////		}else {
////			throw new MemberException("로그인을 실패하였습니다.");
////		}		
//		
//		if(loginUser !=null) { // 회원가입 기능 구현 전 암호화안한 테스트용
//			model.addAttribute("loginUser",loginUser);
//			return "redirect:home.do";
//		}else {
//			throw new MemberException("로그인을 실패하였습니다.");
//		}	
//	}

	
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
	
	
	
	
	
	
	
	
	
	
	
	
}



	












