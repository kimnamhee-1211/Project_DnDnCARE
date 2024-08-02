package com.kh.dndncare.matching.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MatchingController {
	
	@GetMapping("publicMatching.mc")
	public String publicMatchingView() {
		return "publicMatching";
	}
	
	@PostMapping("publicMatching2.mc")
	public String publicMatching2() {
		return "publicMatching2";
	}
}
