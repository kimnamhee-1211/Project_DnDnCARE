package com.kh.dndncare.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kh.dndncare.common.interceptor.CheckCareInfomationAiSearch;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer{
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 임시 경로로 테스트 수행 후 공유 폴더로 변경할 것 				file:///C:/uploadFinalFiles/						file://192.168.40.37/sharedFolder/dndnCare/admin/board/2024081315105949985.png
		registry.addResourceHandler("/**").addResourceLocations("file:///C:/uploadFinalFiles/", "classpath:static/", "file://192.168.40.37/sharedFolder/dndnCare/admin/board/");  
		//registry.addResourceHandler("/**").addResourceLocations("file:///C:/uploadFinalFiles/", "classpath:static/");  
		WebMvcConfigurer.super.addResourceHandlers(registry);
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		// 간병백과 페이지에서 ai검색 요청을 가로챌 인터셉터를 등록한다.
		registry.addInterceptor(new CheckCareInfomationAiSearch())
				.addPathPatterns("/searchOpenAi.bo"); // 인터셉터 등록
		
		
		WebMvcConfigurer.super.addInterceptors(registry);
	}
	
}
