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
	
	//@GetMapping("chatGpt.me") // chatGpt.me?memberNo=1
	public String chatGpt(int memberNo) {
		// 간병인이라 가정하고 테스트
		System.out.println("컨트롤러");
		// 간병인 정보 설정
		HashMap<String, String> cMap = new HashMap<String, String>();
		//서비스경험=병원돌봄/경력=2년/환자유형=치매,재활/자격증=요양보호사/환자중증도=경증/거주지=서울시종로구/지불받기를바라는최소간병비용=60,000원이상
		cMap.put("서비스경험", "병원돌봄");
		cMap.put("경력", "2년");
		cMap.put("환자유형", "치매");
		cMap.put("자격증", "요양보호사");
		cMap.put("환자중증도", "경증");
		cMap.put("거주지", "서울시 종로구");
		cMap.put("지불받기를 바라는 최소 간병비용", "60,000원 이상");
		
		// 환자 목록 설정
		String pStr = "{\r\n"
				+ "			환자번호=1/필요한서비스=병원돌봄/원하는간병인경력=0년이상/보유질환=치매/중증도=경증/간병받을장소=성북서울요양병원/지불가능한금액=50,000원이하,\r\n"
				+ "			환자번호=2/필요한서비스=가정돌봄/원하는간병인경력=1년이상/보유질환=호흡기질환/중증도=중증/간병받을장소=종로구관철동19-19/지불가능한금액=100,000원이하,\r\n"
				+ "			환자번호=3/필요한서비스=동행서비스/원하는간병인경력=3년이상/보유질환=거동불편/중증도=경증/간병받을장소=아산병원/지불가능한금액=60,000원이하,\r\n"
				+ "			환자번호=4/필요한서비스=병원돌봄/원하는간병인경력=5년이상/보유질환=외상환자/중증도=중증/입원병원=연세세브란스병원/지불가능한금액=70,000원이하,\r\n"
				+ "			환자번호=5/필요한서비스=가정돌봄/원하는간병인경력=8년이상/보유질환=파킨슨/중증도=경증/입원병원=부여중앙병원/지불가능한금액=80,000원이하,\r\n"
				+ "			환자번호=6/필요한서비스=동행서비스/원하는간병인경력=0년이상/보유질환=수면질환/중증도=중증/입원병원=삼성서울병원  /지불가능한금액=40,000원이하,\r\n"
				+ "			환자번호=7/필요한서비스=병원돌봄/원하는간병인경력=1년이상/보유질환=정신질환/중증도=경증/입원병원=성북서울요양병원/지불가능한금액=80,000원이하,\r\n"
				+ "			환자번호=8/필요한서비스=가정돌봄/원하는간병인경력=3년이상/보유질환=재활/중증도=중증/입원병원=서울대학교병원/지불가능한금액=70,000원이하,\r\n"
				+ "			환자번호=9/필요한서비스=동행서비스/원하는간병인경력=5년이상/보유질환=치매/중증도=경증/입원병원=서울성모병원/지불가능한금액=50,000원이하,\r\n"
				+ "			환자번호=10/필요한서비스=병원돌봄/원하는간병인경력=8년이상/보유질환=호흡기질환/중증도=중증/입원병원=연세세브란스병원/지불가능한금액=40,000원이하,\r\n"
				+ "			환자번호=11/필요한서비스=가정돌봄/원하는간병인경력=0년이상/보유질환=거동불편/중증도=경증/입원병원=강북삼성병원/지불가능한금액=70,000원이하,\r\n"
				+ "			환자번호=12/필요한서비스=동행서비스/원하는간병인경력=1년이상/보유질환=외상환자/중증도=중증/입원병원=반포서래여성의원/지불가능한금액=65,000원이하,\r\n"
				+ "			환자번호=13/필요한서비스=가정돌봄/원하는간병인경력=3년이상/보유질환=재활/중증도=경증/입원병원=서울병원/지불가능한금액=70,000원이하,\r\n"
				+ "			환자번호=14/필요한서비스=병원돌봄/원하는간병인경력=5년이상/보유질환=골절/중증도=중증/입원병원=서울중앙의원/지불가능한금액=80,000원이하,\r\n"
				+ "		}";
		String result = "간병인 정보는" + cMap.toString() + "환자 목록은 " + pStr + "간병인의 정보를 바탕으로 가장 적절한 환자번호 5개만 짧게 대답해줘.";
		return result;
	}
	
	
	@GetMapping("/chat")
	public String chat(@RequestParam(name="prompt") String prompt) {
		prompt = chatGpt(1);
		
		ChatGPTRequest request = new ChatGPTRequest(model, prompt);
		ChatGPTResponse chatGPTResponse =  template.postForObject(apiURL, request, ChatGPTResponse.class);
		// RestTemplate의 postForObject 메소드를 사용하여, apiURL 주소로 request 객체를 JSON 형태로 전송하고, 
		// ChatGPTResponse 클래스의 인스턴스로 응답을 받는다. 
		// apiURL은 OpenAI의 ChatGPT API 엔드포인트 주소를 나타낸다. 
		// 이 메소드는 요청을 보내고, 응답을 받아 ChatGPTResponse 타입의 객체로 변환한다.
		System.out.println("여기부터 챗 GPT");
		System.out.println(chatGPTResponse.getChoices().get(0).getMessage().getContent());
		return chatGPTResponse.getChoices().get(0).getMessage().getContent();
	}
	
	public String promptSetting() {
		
		
		return "";
	}
	
	
	
	
}
