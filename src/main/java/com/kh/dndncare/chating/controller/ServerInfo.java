package com.kh.dndncare.chating.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class ServerInfo {
	 @GetMapping("/api/server-ip")
	    public String getServerIp(HttpServletRequest request) {
	        String serverIp = request.getLocalAddr();
	        return serverIp + ":8096"; // 포트 번호도 함께 반환
	    }
}
