package com.kh.dndncare.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kh.dndncare.common.interceptor.CheckLoginInterceptor;


@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 임시 경로로 테스트 수행 후 공유 폴더로 변경할 것																				//종규 프로필 추가
		registry.addResourceHandler("/**").addResourceLocations("file:///c:/uploadFinalFiles/", "classpath:static/", "file://192.168.40.37/sharedFolder/dndnCare/","file://192.168.40.37/sharedFolder/dndnCare/profile/");  
		WebMvcConfigurer.super.addResourceHandlers(registry);
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		
		//기본적인 로그인 인터셉터 넣기
		registry.addInterceptor(new CheckLoginInterceptor())
		.addPathPatterns("/myInfo.me","/patientMain.me","/caregiverMain.me","/moreCaregiverInfo.me","/joinMatchingMainView.jm","/communityBoardList.bo");
		
		
		
		WebMvcConfigurer.super.addInterceptors(registry);
	}
	
//	
//	@Override
//	public void addResourceHandlers(ResourceHandlerRegistry registry) {
//		// 임시 경로로 테스트 수행 후 공유 폴더로 변경할 것 				file:///C:/uploadFinalFiles/						file://192.168.40.37/sharedFolder/dndnCare/admin/board/2024081315105949985.png
//		//registry.addResourceHandler("/**").addResourceLocations("file:///C:/uploadFinalFiles/", "classpath:static/", "file://192.168.40.37/sharedFolder/dndnCare/admin/board/");  
//		registry.addResourceHandler("/**").addResourceLocations("file:///C:/uploadFinalFiles/", "classpath:static/");  
//		WebMvcConfigurer.super.addResourceHandlers(registry);
//	}
//	
	
	
}
