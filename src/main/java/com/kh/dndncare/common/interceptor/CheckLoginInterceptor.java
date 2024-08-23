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
		
		String url = request.getRequestURI();
		
		String msg = null;
		System.out.println(url);
		if(loginUser == null) {
			if(url.contains("moreWorkInfo.me") || url.contains("")) {	//여긴 간병인 로그인페이지
				msg = "로그인 후 이용하세요!";
				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().write("<script> alert('"+ msg +"'); location.href='home.do';</script>");
				return false;
			}else if(url.contains("moreCaregiverInfo.me") || url.contains("joinMatchingMainView.jm")) {	//여긴 환자페이지로 이동시키자
				msg = "로그인 후 이용하세요";
				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().write("<script> alert('"+ msg +"'); location.href='home.do';</script>");
				return false;
			}else {
				System.out.println("안걸린거");
				msg = "로그인 후 확인 가능합니다";
			}
			//예쩐 서블릿햇을때 jsp이전 사용방식. 생각해보니 <html도넣는데 스크립트도가능하잖아
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().write("<script> alert('"+ msg +"'); location.href='home.do';</script>");		
			return false;
		}else if(loginUser.getMemberCategory() == "C"){
			System.out.println("ㅎ간병인로그인시 인터셉터 체크");
			//간병인에서 환자페이지는 막도록 하자
			if(url.contains("moreCaregiverInfo.me") ||url.contains("patientMain.me") || url.contains("joinMatchingMainView.jm") || url.contains("joinMatching.jm")|| url.contains("/joinMatchingEnrollView.jm")) {
				msg = "환자 페이지는 이동할 수 없습니다";
				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().write("<script> alert('"+ msg +"'); location.href='home.do';</script>");
				return false;
			}
		}else if(loginUser.getMemberCategory() == "P") {
			System.out.println("환자로그인시 인터셉터 체크");
			//환자에서 간병인페이지는 막도록 하자
			if(url.contains("caregiverMain.me") ||url.contains("myInfoMatchingHistory.me")||url.contains("myInfoMatchingReview.me")||url.contains("moreWorkInfo.me")) {
				msg = "간병인 페이지는 이동할 수 없습니다";
				response.setContentType("text/html; charset=UTF-8");
				response.getWriter().write("<script> alert('"+ msg +"'); location.href='home.do';</script>");
				return false;
			}
			
		}
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
	
}
