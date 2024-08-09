package com.kh.dndncare;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	
	
	@GetMapping("home.do")
	public String home() {
		return "home";
	}
	
	@GetMapping("naver.do")
	public String naver() {
		return "naverTest";
	}
}
