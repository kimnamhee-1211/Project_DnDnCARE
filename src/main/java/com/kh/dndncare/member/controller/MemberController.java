package com.kh.dndncare.member.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.service.MemberService;
import com.kh.dndncare.member.model.vo.CalendarEvent;
import com.kh.dndncare.member.model.vo.Matching;
import com.kh.dndncare.member.model.vo.Member;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@SessionAttributes({"loginUser", "tempMemberCategory"})
@Controller
public class MemberController {
	
	@Autowired
	private MemberService mService;
	
	@Autowired
	private CustomBotController botController;
	
	
	@GetMapping("{memberType}.me")
	public String selectMemberType(@PathVariable("memberType") String memberType,Model model) {
		String tempMemberCategory;
		String viewName;
		
		switch(memberType) {
		case "patient" :
				tempMemberCategory = "P";
				viewName = "pLogin";
				break;
		case "careGiver" :
				tempMemberCategory = "C";
				viewName = "cLogin";
				break;
		default : return "errorPage";
		}
		
		model.addAttribute("tempMemberCategory", tempMemberCategory);
		return viewName;
		
	}

	@GetMapping("myInfo.me")
	public String myInfo() {		//마이페이지  확인용
		return "myInfo";
	}


	
	@PostMapping("login.me")
	public String login(@ModelAttribute Member m, Model model) {
		Member loginUser = mService.login(m);
		
//		if(bcrypt.matches(m.getPwd(), loginUser.getPwd())) {
//			model.addAttribute("loginUser",loginUser);
//			return "redirect:home.do";
//		}else {
//			throw new MemberException("로그인을 실패하였습니다.");
//		}		
		
		System.out.println(loginUser);
		if(loginUser !=null) { // 회원가입 기능 구현 전 암호화안한 테스트용
			model.addAttribute("loginUser",loginUser);
			return "redirect:home.do";
		}else {
			throw new MemberException("로그인에 실패했습니다");
		}	
	}
	
	@GetMapping("logout.me")
	public String logout(SessionStatus status) {
		status.setComplete();
		return "redirect:home.do";
	}
	// 임시버튼 : 간병인 메인페이지로 가기 
	@GetMapping("caregiverMain.me")
	public String caregiverMain() {
		// (1) 자동추천 기능구현 : 간병인이라 가정하고 테스트
		System.out.println("컨트롤러");
		// 1. 간병인 정보 설정
		HashMap<String, String> cMap = new HashMap<String, String>();
		//서비스경험=병원돌봄/경력=2년/환자유형=치매,재활/자격증=요양보호사/환자중증도=경증/거주지=서울시종로구/지불받기를바라는최소간병비용=60,000원이상
		cMap.put("서비스경험", "병원돌봄");
		cMap.put("경력", "2년");
		cMap.put("환자유형", "치매");
		cMap.put("자격증", "요양보호사");
		cMap.put("환자중증도", "경증");
		cMap.put("거주지", "서울시 종로구");
		cMap.put("지불받기를 바라는 최소 간병비용", "60,000원 이상");
		
		// 2. 환자 목록 설정
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
		
		// 3.
		String prompt = "간병인 정보는" + cMap.toString() + "환자 목록은 " + pStr + "간병인의 정보를 바탕으로 가장 적절한 환자번호 5개만 숫자로만 짧게 대답해줘.";
		
		String result = botController.chat(prompt); // "2, 4, 8, 10, 14"
		
		
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < 5; i++) {
			list.add(Integer.parseInt(result.split(", ")[i]));
		} // [2, 5, 8, 10, 14] 
		
		// 
		
		
		
		
		
		
		
		
		
		return "caregiverMain";
	}
	
	// 임시버튼 : 환자 메인페이지로 가기
	@GetMapping("patientMain.me")
	public String patientMain() {
		
		
		
		
		
		
		
		
		
		return "patientMain";
	}
	
	//회원가입 페이지 이동
	@GetMapping("enroll1View.me")
	public String enroll1View() {
		return "enroll1";
	}
	
	
	//회원가입	 검증
	@PostMapping("idCheck.me")
	@ResponseBody
	public String idCheck(@RequestParam("id") String id) {		
		int result = mService.idCheck(id);	
		if(result == 0) {
			return "usable";
		}else{
			return "unusable";
		}

	}
	
	//회원가입
	@PostMapping("enroll.me")
	public String enroll(@ModelAttribute Member m, 
						@RequestParam("postcode") String postcode, @RequestParam("roadAddress") String roadAddress,@RequestParam("detailAddress") String detailAddress,
						@RequestParam("memberEmail") String memberEmail, @RequestParam("emailDomain") String emailDomain, 
						HttpSession ssession) {
		
		return null;
	}

	
	
	@GetMapping("enroll32View.me")
	public String enroll32View() {
		return "enroll3_2";
	}
	
	
	@GetMapping("enroll33View.me")
	public String enroll33View() {
		return "enroll3_3";
	}
	
	@GetMapping("myInfoMatching.me")
	public String myInfoMatching() {		//마이페이지 현재매칭정보 확인용
		return "myInfoMatching";
	}
	
	@GetMapping("myInfoMatchingHistory.me")
	public String myInfoMatchingHistory() {		//마이페이지 매칭 이력 확인용
		return "myInfoMatchingHistory";
	}
	
	@GetMapping("myInfoMatchingReview.me")
	public String myInfoMatchingReview() {		//마이페이지 매칭 이력 확인용
		return "myInfoMatchingReview";
	}
	
	@GetMapping("myInfoBoardList.me")
	public String myInfoBoardList() {		//마이페이지 보드작성 확인용
		return "myInfoBoardList";
	}
	
	@GetMapping("findId.me")
	public String findId() {
		return "findIdPage";
	}
	
	@GetMapping("findPwd.me")
	public String findPwd() {
		return "findPwdPage";
	}
	
	// 메인페이지 이동 후에 간병인 메인페이지 렌더링 도중에 캘린더 이벤트를 조회하게 된다.
	@GetMapping("caregiverCalendarEvent.me")
	@ResponseBody
	public void calendarEvent(Model model, HttpServletResponse response, @RequestParam("memberNo") int memberNo) {
		Member loginUser = (Member)model.getAttribute("loginUser");
		
		// 일정 조회 
		ArrayList<CalendarEvent> eList = mService.caregiverCalendarEvent(loginUser);
		
		// GSON
		response.setContentType("application/json; charset=UTF-8");
		GsonBuilder gb = new GsonBuilder().setDateFormat("YYYY-MM-DD");
		Gson gson = gb.create();
		try {
			gson.toJson(eList, response.getWriter());
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	// 메인페이지 이동 후에 환자 메인페이지 렌더링 도중에 캘린더 이벤트를 조회하게 된다.
	
	
	
	
	// 자동추천을 비동기 통신으로 요청
	@GetMapping("refreshChoice.me")
	@ResponseBody
	public void refreshChoice(HttpServletResponse response) {
		// 1. 로그인한 회원 정보를 조회한다.
		HashMap<String, String> cMap = new HashMap<String, String>();
		//서비스경험=병원돌봄/경력=2년/환자유형=치매,재활/자격증=요양보호사/환자중증도=경증/거주지=서울시종로구/지불받기를바라는최소간병비용=60,000원이상
		cMap.put("서비스경험", "병원돌봄");
		cMap.put("경력", "2년");
		cMap.put("환자유형", "치매");
		cMap.put("자격증", "요양보호사");
		cMap.put("환자중증도", "경증");
		cMap.put("거주지", "서울시 종로구");
		cMap.put("지불받기를 바라는 최소 간병비용", "60,000원 이상");
		
		// 2. 후보 대상을 조회한다.
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
		
		// 3. GPT에게서 추천목록을 받아온다.
		String prompt = "간병인 정보는" + cMap.toString() + "환자 목록은 " + pStr + "간병인의 정보를 바탕으로 가장 적절한 환자번호 5개만 숫자로만 짧게 대답해줘.";
		
		String result = botController.chat(prompt); // "2, 4, 8, 10, 14"
		
		// 4. GPT에게서 받은 추천목록에서 회원번호를 추출한다.
		ArrayList<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < 5; i++) {
			list.add(Integer.parseInt(result.split(", ")[i]));
		} // [2, 5, 8, 10, 14] 
		
		// 5. 추출된 회원번호를 통해 회원 정보를 조회해온다. => List<Member>의 형식일 것이다.
		ArrayList<Member> resultList = new ArrayList<Member>();  // 회원정보를 받아왔다고 가정
		Gson gson = new Gson();
		response.setContentType("application/json; charset=UTF-8;");
		try {
			gson.toJson(resultList, response.getWriter());
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
}



	












