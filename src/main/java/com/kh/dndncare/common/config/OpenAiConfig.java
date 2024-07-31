package com.kh.dndncare.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class OpenAiConfig {
	@Value("${openai.api.key}")
	private String openAiKey;
	
	@Bean
	RestTemplate template() {
		RestTemplate restTemplate = new RestTemplate();
		
		restTemplate.getInterceptors().add((request, body, execution) -> {
			// Authorization : OpenAI API에 대한 인증
			// Bearer : OAuth 2.0 인증 방식에서 사용되는 토큰 타입
			// openAiKey : OpenAI API의 인증 키
			request.getHeaders().add("Authorization", "Bearer " + openAiKey);
			//request.getHeaders().setContentType(APPLICATION_JSON);
            return execution.execute(request, body);
		});
		
		return restTemplate;
	}
}
