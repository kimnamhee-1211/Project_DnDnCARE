package com.kh.dndncare.joinMaching.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.kh.dndncare.joinMaching.model.service.JoinMachingService;

@Controller
public class JoinMachingController {

	@Autowired
	private JoinMachingService jmService;
	
	
	@GetMapping("joinMachingMainView.jm")
	public String joinMachingMainView(Model model) {
		model.addAttribute("currentPage", 1);
		return "joinMachingMain";
	}
	
	

	
	
	
	
}
