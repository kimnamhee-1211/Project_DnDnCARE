package com.kh.dndncare.common.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.dndncare.member.model.vo.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class CheckCareInformationUsage implements HandlerInterceptor {
	// 간병백과 페이지 방문자 수 계산을 위한 인터셉터
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		HttpSession session = request.getSession();
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		log.info(loginUser.getMemberId());
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
}
