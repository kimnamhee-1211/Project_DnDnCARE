package com.kh.dndncare;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
	
	
	@GetMapping("home.do")
	public String home() {
		return "home";
	}
	
	public String generateState()
	{
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}
	
	@GetMapping("naver.lo")
	public String naver() {
		return "naverTest";
	}
	
	@GetMapping("callback.lo")
	public String callback() {
		return "callback";
	}
	
	@GetMapping("kakao.lo")
	public String kakao(@RequestParam(value="code", required = false)String code,Model model) {
		if(code != null) {
			model.addAttribute("code",code);
		}
		return "kakao";
	}
	
	@GetMapping("google.lo")
	public String google() {
		return "google";
	}

	@GetMapping("map.lo")
	public String map() {
		return "map";
	}
}
