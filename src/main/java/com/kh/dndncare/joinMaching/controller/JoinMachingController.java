package com.kh.dndncare.joinMaching.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.kh.dndncare.joinMaching.model.service.JoinMachingService;
import com.kh.dndncare.joinMaching.model.vo.Hospital;

@Controller
public class JoinMachingController {

	@Autowired
	private JoinMachingService jmService;
	
	
	@GetMapping("joinMachingMainView.jm")
	public String joinMachingMainView() {
		
		return "joinMachingMain";
	}
	
	@GetMapping("joinMaching.jm")
	public String joinMachingMain(@ModelAttribute Hospital ho, Model model) {
		
		model.addAttribute("ho", ho);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

		return "joinMaching";
	}

	
	
	
	
}
