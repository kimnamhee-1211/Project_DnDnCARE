package com.kh.dndncare.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.vo.Member;

import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
	
	// 관리자 로그인 => 관리자 메인 페이지로 이동하는 메소드
	@GetMapping("adminMain.adm")
	public String adminMain() {
		
		return "adminMain";
	}
	
	// 간병정보(간병백과) 페이지로 이동하는 메소드
	@GetMapping("careInformation.adm")
	public String careInformation() {
		
		return "careInformation.adm";
	}
	
	//
	@GetMapping("writeCareInformation.adm")
	public String writeCareInformation(HttpSession session) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		if(loginUser != null) {
			if(loginUser.getMemberCategory().equals("A")) {
				return "writeCareInformation";
			} else {
				throw new MemberException("관리자로 로그인 후 이용해주세요.");
			}
		} else {
			throw new MemberException("로그인 후 이용해주세요");
		}
	}
	
	
}
