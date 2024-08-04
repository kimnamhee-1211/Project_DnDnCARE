package com.kh.dndncare.matching.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.kh.dndncare.matching.model.exception.MatchingException;
import com.kh.dndncare.matching.model.service.MatchingService;
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

import jakarta.servlet.http.HttpSession;

@Controller
public class MatchingController {
	
	@Autowired
	MatchingService mcService;
	
	@GetMapping("publicMatching.mc")
	public String publicMatchingView(HttpSession session,Model model) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		if(loginUser !=null) {
			int memberNo = loginUser.getMemberNo();
			Patient patient = mcService.selectPatient(memberNo);
			model.addAttribute("patient",patient);
			return "publicMatching";
			
		}
		throw new MatchingException("하하");
	}
	
	@PostMapping("publicMatching2.mc")
	public String publicMatching2() {
		return "publicMatching2";
	}
}
