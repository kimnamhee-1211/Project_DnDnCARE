package com.kh.dndncare.common.config;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebMvcConfig implements WebMvcConfigurer{
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// 임시 경로로 테스트 수행 후 공유 폴더로 변경할 것
		registry.addResourceHandler("/**").addResourceLocations("file:///c:/uploadFinalFiles/", "classpath:static/");  
		WebMvcConfigurer.super.addResourceHandlers(registry);
	}
	
	
	
}
