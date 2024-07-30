package com.kh.dndncare.matching.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MatchingController {
	
	@GetMapping("publicMatching.mc")
	public String publicMatchingView() {
		return "publicMatching";
	}
}
