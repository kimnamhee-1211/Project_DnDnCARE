package com.kh.dndncare.member.controller;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.kh.dndncare.common.dto.ChatGPTRequest;
import com.kh.dndncare.common.dto.ChatGPTResponse;

@RestController
@RequestMapping("/bot")
public class CustomBotController { // 챗 GPT 테스트용 컨트롤러
	@Value("${openai.model}")
	private String model;
	
	@Value("${openai.api.url}")
	private String apiURL;
	
	@Autowired
	private RestTemplate template;
	
	@GetMapping("/chat")
	public String chat(@RequestParam(name="prompt") String prompt) {
		
		ChatGPTRequest request = new ChatGPTRequest(model, prompt);
		ChatGPTResponse chatGPTResponse =  template.postForObject(apiURL, request, ChatGPTResponse.class);
		// RestTemplate의 postForObject 메소드를 사용하여, apiURL 주소로 request 객체를 JSON 형태로 전송하고, 
		// ChatGPTResponse 클래스의 인스턴스로 응답을 받는다. 
		// apiURL은 OpenAI의 ChatGPT API 엔드포인트 주소를 나타낸다. 
		// 이 메소드는 요청을 보내고, 응답을 받아 ChatGPTResponse 타입의 객체로 변환한다.
		
		//System.out.println("여기부터 챗 GPT");
		//System.out.println(chatGPTResponse.getChoices().get(0).getMessage().getContent());
		return chatGPTResponse.getChoices().get(0).getMessage().getContent();
	}
	
	
	
	
	
	
}
