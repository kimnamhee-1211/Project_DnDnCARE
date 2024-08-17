package com.kh.dndncare.common.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.dndncare.member.model.vo.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CheckCareInformationAiSearch implements HandlerInterceptor{
	// Ai 검색을 처리하기 이전에 로그를 작성하는 메소드
	private Logger log = LoggerFactory.getLogger(CheckCareInformationAiSearch.class);
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();
		Member loginUser = (Member)session.getAttribute("loginUser");
		if(loginUser != null) {
			log.info(loginUser.getMemberId()); // 검색을 요청한 사용자의 아이디를 기록한다.
		}
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
}
