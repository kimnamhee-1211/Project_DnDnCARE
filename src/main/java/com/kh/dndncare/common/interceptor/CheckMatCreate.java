package com.kh.dndncare.common.interceptor;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.kh.dndncare.member.model.vo.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CheckMatCreate implements HandlerInterceptor{
	Logger log = LoggerFactory.getLogger(CheckMatCreate.class);
	
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		HttpSession session = request.getSession();
		Member loginUser = (Member)session.getAttribute("loginUser");
		Integer matNo = (Integer)session.getAttribute("logMatNo");
		String matService = (String)session.getAttribute("logMatService");
		
		if(matNo != null && matNo != 0) {
			log.info("매칭번호 :" + matNo + ":"+ matService);
		}
		session.removeAttribute("logMatNo");
		session.removeAttribute("logMatService");
		
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
	
	
	
}
