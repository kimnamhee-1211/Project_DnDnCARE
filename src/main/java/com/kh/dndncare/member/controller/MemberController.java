package com.kh.dndncare.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import com.kh.dndncare.member.model.service.MemberService;

@Controller
public class MemberController {
	/*
	 * @Autowired private MemberService mService;
	 */
	@GetMapping("loginView.me")
	public String loginView() {
		return "login";
	}
}
