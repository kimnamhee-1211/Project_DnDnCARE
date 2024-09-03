package com.kh.dndncare.common.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kh.dndncare.common.interceptor.CheckCareInformationAiSearch;
import com.kh.dndncare.common.interceptor.CheckCareInformationUsage;
import com.kh.dndncare.common.interceptor.CheckLoginInterceptor;
import com.kh.dndncare.common.interceptor.CheckLoginUser;
import com.kh.dndncare.common.interceptor.CheckMatCreate;
@ComponentScan
@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 임시 경로로 테스트 수행 후 공유 폴더로 변경할 것																				//종규 프로필 추가
		//registry.addResourceHandler("/**").addResourceLocations("file:///c:/uploadFinalFiles/", "classpath:static/", "file://192.168.40.37/sharedFolder/dndnCare/","file://192.168.40.37/sharedFolder/dndnCare/profile/");  
		// 임시 경로로 테스트 수행 후 공유 폴더로 변경할 것 				file:///C:/uploadFinalFiles/						file://192.168.40.37/sharedFolder/dndnCare/admin/board/2024081315105949985.png
		//registry.addResourceHandler("/**").addResourceLocations("file:///C:/uploadFinalFiles/", "classpath:static/", "file://192.168.40.37/sharedFolder/dndnCare/admin/board/");  
		registry.addResourceHandler("/**").addResourceLocations("file:///C:/uploadFinalFiles/", "classpath:static/","file:///C:/uploadFiles/careInformation/","file:///C:/uploadFiles/profileImage/");  
		//registry.addResourceHandler("/**").addResourceLocations("file:///C:/uploadFinalFiles/", "classpath:static/");  
		WebMvcConfigurer.super.addResourceHandlers(registry);
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		
		//기본적인 로그인 인터셉터 넣기
		registry.addInterceptor(new CheckLoginInterceptor())
		.addPathPatterns("/myInfo.me","/patientMain.me","/caregiverMain.me","/moreCaregiverInfo.me","/joinMatchingMainView.jm","/communityBoardList.bo")
		.excludePathPatterns("/login.me","/**.do","/careGiver.me","/patient.me","/**.lo","/socialLogin.me");
		
		
		// 간병백과 페이지에서 ai검색 요청을 가로챌 인터셉터를 등록한다.
		registry.addInterceptor(new CheckCareInformationAiSearch())
		.addPathPatterns("/searchOpenAi.bo"); // 인터셉터 등록
		
		registry.addInterceptor(new CheckCareInformationUsage())
		.addPathPatterns("/careInformation.bo");
		
		// 로그인에 성공시 요청을 가로챌 인터셉터를 등록한다.
		registry.addInterceptor(new CheckLoginUser())
				.addPathPatterns("/login.me");

		// 매칭 방 생성시 로그파일을 생성한다 공동,개인
		registry.addInterceptor(new CheckMatCreate())
		.addPathPatterns("/enrollJoinMatching.jm","/publicMatchingApply.mc");
		
		
		
		WebMvcConfigurer.super.addInterceptors(registry);
	}
	
}
