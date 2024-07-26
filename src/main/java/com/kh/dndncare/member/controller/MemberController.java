package com.kh.dndncare.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.service.MemberService;
import com.kh.dndncare.member.model.vo.Member;

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
	
	@GetMapping("findId.me")
	public String findId() {
		return "findIdPage";
	}
	
	@GetMapping("findPwd.me")
	public String findPwd() {
		return "findPwdPage";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
