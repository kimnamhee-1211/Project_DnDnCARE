package com.kh.dndncare.common.interceptor;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.kh.dndncare.member.model.vo.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CheckLoginUser implements HandlerInterceptor{
	Logger log = LoggerFactory.getLogger(CheckLoginUser.class);
	
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		HttpSession session = request.getSession();
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		if(loginUser != null && !loginUser.getMemberCategory().equals("A")) {
			log.info(loginUser.getMemberId()); // 관리자가 아닌 사용자의 아이디를 기록한다.
		}
		
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
	
	
	
}
