package com.kh.dndncare.common.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;

import com.kh.dndncare.member.model.vo.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CheckLoginInterceptor implements HandlerInterceptor{
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		
		HttpSession session = request.getSession();
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		if(loginUser == null) {
			String url = request.getRequestURI();
			
			String msg = null;
			if(url.contains("moreWorkInfo.me") || url.contains("")) {	//여긴 간병인 로그인페이지
				msg = "로그인 후 이용하세요";
				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().write("<script> alert('"+ msg +"'); location.href='careGiver.me';</script>");
				return false;
			}else if(url.contains("moreCaregiverInfo.me") || url.contains("joinMatchingMainView.jm")) {	//여긴 환자페이지로 이동시키자
				msg = "로그인 후 이용하세요";
				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().write("<script> alert('"+ msg +"'); location.href='patient.me';</script>");
				return false;
			}else {
				msg = "로그인 후 확인 가능합니다";
			}
			//예쩐 서블릿햇을때 jsp이전 사용방식. 생각해보니 <html도넣는데 스크립트도가능하잖아
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().write("<script> alert('"+ msg +"'); location.href='home.do';</script>");		
			return false;
		}
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
	
}
