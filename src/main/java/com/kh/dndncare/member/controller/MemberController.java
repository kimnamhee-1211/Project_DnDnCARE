package com.kh.dndncare.member.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.service.MemberService;
import com.kh.dndncare.member.model.vo.CalendarEvent;
import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@SessionAttributes({"loginUser", "tempMemberCategory", "enrollmember"})
@Controller
public class MemberController {
	

	
	@Autowired
	private CustomBotController botController;
	
	
	 @Autowired
	 private MemberService mService;
	 
	 @Autowired
	 private BCryptPasswordEncoder bCrypt;
	 
	@GetMapping("loginView.me")
	public String loginView() {
		return "login";
	}

	@GetMapping("{memberType}.me")
	public String selectMemberType(@PathVariable("memberType") String memberType,Model model) {
		String tempMemberCategory;
		
		switch(memberType) {
		case "patient" :
				tempMemberCategory = "P";
				break;
		case "careGiver" :
				tempMemberCategory = "C";
				break;
		default : return "errorPage";
		}
		
		model.addAttribute("tempMemberCategory", tempMemberCategory);
		return "login";
		
	}

	@GetMapping("myInfo.me")
	public String myInfo(HttpSession session) {		//마이페이지  확인용
		ArrayList<Member> m =  mService.selectAllMember();	//노트북,피시방에 디비가없으니 접근용

		for(Member z : m) {
			System.out.println(z);
			System.out.println("");
		}
		
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		if(loginUser != null) {
			char check = loginUser.getMemberCategory().charAt(0);
			switch(check) {
				case 'C': return "myInfo";
				case 'P': return "myInfoP";
				case 'A': return "myInfoA";
			}
		}
		throw new MemberException("로그인이 필요합니다.인터셉터만드세요");
		
	}


	
	@PostMapping("login.me")
	public String login(@ModelAttribute Member m, Model model, RedirectAttributes ra) {
		Member loginUser = mService.login(m);
		
		if(bCrypt.matches(m.getMemberPwd(), loginUser.getMemberPwd())) {
			model.addAttribute("loginUser",loginUser);
			
			
			if(loginUser.getMemberCategory().equalsIgnoreCase("C")) {
				return "redirect:caregiverMain.me";
			} else if(loginUser.getMemberCategory().equalsIgnoreCase("P")) {
				return "redirect:patientMain.me";
			}
			
			
			return "redirect:home.do";
			
			
			
			
			
			
		}else {
			throw new MemberException("로그인을 실패하였습니다.");
		}		
		
//		if(loginUser !=null) { // 회원가입 기능 구현 전 암호화안한 테스트용
//			model.addAttribute("loginUser",loginUser);
//			return "redirect:home.do";
//		}else {
//			throw new MemberException("로그인에 실패했습니다");
//		}	
	}
	
	@GetMapping("logout.me")
	public String logout(SessionStatus status) {
		status.setComplete();
		return "redirect:home.do";
	}
	
	// ai추천 : 간병인의 입장 : 환자 추천 목록 조회
	public ArrayList<Patient> openAiPatientChoice(int memberNo, int selectNum) {
		// 1. 간병인 본인 정보 조회 
//		조회할 항목
//			필수입력 : 원하는 서비스, 공동간병 참여여부, 경력, 적정비용, 성별, 나이, 주소
//			선택입력 : 서비스 경험, 돌봄경험, 자격증
//		항목의 출처
//			CAREGIVER : 공동간병 희망여부(필수,CARE_JOIN_STATUS), 최소비용(필수, MIN_MONEY), 
//			MEMBER_INFO : 경력기간(필수,1개), 서비스경험(선택, 0~3개), 돌봄경험(선택, 0~10개), 자격증(선택, 0~3개), 원하는 서비스(필수, 1~3개)
//			MEMBER: 성별(필수, MEMBER_GENDER), 나이(필수, MEMBER_AGE), 주소(필수, MEMBER_ADDRESS), 국적(필수, MEMBER_NATIONAL)
		HashMap<String, String> infoMap =  mService.getCaregiverInfo(memberNo); 
							// {국적=내국인, 주소=경기 성남시 분당구 내정로 54 3동 301호, CARE_JOIN_STATUS=Y, 나이=69, 성별=남성, 최소금액=50000}
		
		ArrayList<HashMap<String, String>> cExpList = mService.getCaregiverExp(memberNo); 
		//[{S_CATEGORY=병원돌봄, L_CATEGORY=service}, {S_CATEGORY=가정돌봄, L_CATEGORY=service}, 
		//	{S_CATEGORY=동행서비스, L_CATEGORY=service}, {S_CATEGORY=3, L_CATEGORY=career}, 
		//	{S_CATEGORY=섬망, L_CATEGORY=disease}, {S_CATEGORY=기저귀 케어, L_CATEGORY=disease}, 
		//	{S_CATEGORY=간병사, L_CATEGORY=license}, {S_CATEGORY=요양보호사, L_CATEGORY=license}]
		
		ArrayList<HashMap<String, String>> cWantList = mService.getCaregiverWant(memberNo); // 마이페이지에서 선택적으로 입력
		
		
		// 2. 간병인 정보 가공
		String[] address = infoMap.get("주소").split(" ");
		infoMap.put("주소", address[0] + " " + address[1]);
		String service = ""; // 선택, 0~3개
		String career = ""; // 필수, 1개
		String disease = ""; // 선택, 0~10개
		String license = ""; // 선택, 0~3개
		
		for(HashMap<String, String> m : cExpList) {
			switch(m.get("L_CATEGORY")) {
			case "service" : service += m.get("S_CATEGORY") + "/"; break;
			case "career" : 
				switch(m.get("S_CATEGORY")) {
				case "0" : career = "없음"; break;
				case "1" : career = "1년미만"; break;
				case "3" : career = "3년미만"; break;
				case "5" : career = "5년미만"; break;
				case "8" : career = "5년이상"; break;
				}
			case "disease" : disease += m.get("S_CATEGORY") + "/"; break;
			case "license" : license += m.get("S_CATEGORY") + "/"; break;
			}
		}
		
		if(service.length() > 0) { // 간병인의 서비스 경력이 존재하는 경우를 가르킴
			service = service.substring(0, service.lastIndexOf("/"));
			infoMap.put("서비스경험", service);
		}
		if(disease.length() > 0) {
			disease = disease.substring(0, disease.lastIndexOf("/"));
			infoMap.put("돌봄질환경험", disease);
		}
		if(!license.isEmpty()) { // isEmpty연습하기
			license = license.substring(0, license.lastIndexOf("/"));
			infoMap.put("자격증", license); 
		}
		if(!career.isEmpty()) {
			infoMap.put("경력", career); 
		} else {
			throw new MemberException("OpenAi요청을 위한 필수항목(경력) 조회에 실패하였습니다.");
		} 
		
		if(!cWantList.isEmpty()) {
			String wantService = ""; // 선택, 0~3개
			String wantDisease = ""; // 선택, 0~10개
			
			for(HashMap<String, String> m : cWantList) {
				switch(m.get("L_CATEGORY")) {
				case "service" : wantService += m.get("S_CATEGORY") + "/"; break;
				case "disease" : wantDisease += m.get("S_CATEGORY") + "/"; break;
				}
			}
			
			if(wantService.length() > 0) { // 간병인이 원하는 서비스
				wantService = wantService.substring(0, wantService.lastIndexOf("/"));
				infoMap.put("제공하고 싶은 서비스", wantService);
			}
			
			if(wantDisease.length() > 0) { // 간병인이 원하는 돌봄질환
				wantDisease = wantDisease.substring(0, wantDisease.lastIndexOf("/"));
				infoMap.put("돌보고 싶은 질환", wantDisease);
			}
		}
		if(Integer.parseInt(String.valueOf(infoMap.get("나이"))) < 0) {
			infoMap.put("나이", (Integer.parseInt(infoMap.get("나이"))+100)+"");
		}
		
		// 가공 종료! => infoMap
		// {국적=내국인, 자격증=간병사/요양보호사, 서비스경험=병원돌봄/가정돌봄/동행서비스, 주소=경기 성남시, CARE_JOIN_STATUS=Y, 돌봄질환경험=3/섬망/기저귀 케어, 나이=69, 성별=남성, 경력=3년미만, 최소금액=50000}
		
		
		// 3. 환자 목록 조회
		ArrayList<HashMap<String, Object>> promptPatientList = new ArrayList<HashMap<String, Object>>(); // 프롬프트에 전달할 최종 후보군 리스트
		String joinStatus = infoMap.remove("CARE_JOIN_STATUS"); // 공동 간병 참여여부
		String careGiverAddress = infoMap.get("주소").contains("서울") ? "서울" : (infoMap.get("주소").contains("제주") ? "제주" : infoMap.get("주소"));
		// 환자 기본정보 조회 : 회원번호, 이름, 성별, 나이, 간병장소, 국적, 키, 몸무게, 요청서비스, 요청사항, 요청장소, 매칭 시작일, 매칭 종료일, 금액
//		PATIENT : 멤버번호(PT_NO), 이름(PT_NAME), 성별(PT_GENDER), 나이(PT_GENDER), 국적, 키(PT_HEIGHT), 몸무게(PT_WEIGHT)
//		MATCHING: 매칭번호(MAT_NO), 시작날짜(BEGIN_DT), 종료날짜(END_DT), 지불가능한 금액(MONEY)  
//		MAT_PT_INFO: 신청서비스(SERVICE), 매칭장소(MAT_ADDRESS_INFO), 매칭종류(MAT_MODE)
		
		HashMap<String, Object> condition = new HashMap<String, Object>();
		condition.put("address", careGiverAddress);
		condition.put("selectNum", selectNum*2);
		condition.put("joinStatus", joinStatus);
		ArrayList<Patient> pList = mService.selectPatientList(condition); // 길이 : 0~10
		//[Patient(ptNo=0, memberNo=15, ptName=서은호, ptGender=M, ptAge=null, ptWeight=79, ptHeight=180, ptService=null, 
		//ptAddress=서울 동대문구 망우로 82 202호777, ptRequest=null, ptUpdateDate=null, infoCategory=null, ptRealAge=52, matNo=51, 
		//matType=0, hosInfo=0, memberNational=내국인, service=개인간병, matRequest=일단없음, beginDt=2024-08-14, endDt=2024-08-14, 
		//money=50000, ptDisease=null), 
		
		if(pList.isEmpty()) { // 조건에 맞는 후보 환자가 없을 땐 null로 넘겨야 한다.
			return null;
		}
		
		
		ArrayList<Integer> mNoList = new ArrayList<Integer>();
		for(Patient p : pList) {
			mNoList.add(p.getMemberNo());
		}
		
		ArrayList<HashMap<String, Object>> pExpList = mService.getPatientInfo(mNoList);
//		[{S_CATEGORY=병원돌봄, L_CATEGORY=service, MEMBER_NO=55}, {S_CATEGORY=병원돌봄, L_CATEGORY=service, MEMBER_NO=15}, {S_CATEGORY=병원돌봄, L_CATEGORY=service, MEMBER_NO=54}, {S_CATEGORY=가정돌봄, L_CATEGORY=service, MEMBER_NO=15}, {S_CATEGORY=치매, L_CATEGORY=disease, MEMBER_NO=15}, {S_CATEGORY=치매, L_CATEGORY=disease, MEMBER_NO=54}, {S_CATEGORY=욕창, L_CATEGORY=disease, MEMBER_NO=54}, {S_CATEGORY=하반신 마비, L_CATEGORY=disease, MEMBER_NO=15}, {S_CATEGORY=와상 환자, L_CATEGORY=disease, MEMBER_NO=15}, {S_CATEGORY=기저귀 케어, L_CATEGORY=disease, MEMBER_NO=15}, {S_CATEGORY=기저귀 케어, L_CATEGORY=disease, MEMBER_NO=55}, {S_CATEGORY=기저귀 케어, L_CATEGORY=disease, MEMBER_NO=54}, {S_CATEGORY=의식 없음, L_CATEGORY=disease, MEMBER_NO=54}, {S_CATEGORY=중증, L_CATEGORY=diseaseLevel, MEMBER_NO=55}, {S_CATEGORY=중증, L_CATEGORY=diseaseLevel, MEMBER_NO=54}, {S_CATEGORY=경증, L_CATEGORY=diseaseLevel, MEMBER_NO=15}]
		
		// ArrayList<HashMap<String, Object>> promptPatientList 에 담아야함
		
		for(Patient p : pList) {
			HashMap<String, Object> m = new HashMap<String, Object>(); 
			m.put("매칭번호", p.getMatNo());
			m.put("회원번호", p.getMemberNo());
			m.put("성별", p.getPtGender().equals("M") ? "남자" : "여자");
			m.put("나이", p.getPtRealAge()+"세");
			String[] add = p.getPtAddress().split(" ");
			m.put("간병장소", add[0] + " " + add[1]);
			m.put("국적", p.getMemberNational());
			m.put("키", p.getPtHeight()+"cm");
			m.put("몸무게", p.getPtWeight()+"kg");
			m.put("요청서비스", p.getService());
			m.put("요청사항", p.getMatRequest());
			m.put("간병시작일", p.getBeginDt());
			m.put("간병종료일", p.getEndDt());
			m.put("간병비용", p.getMoney());
			
			String pDisease = "";
			String pDiseaseLevel = "";
			if(!pExpList.isEmpty()) {
				for(HashMap<String, Object> info : pExpList) {
					String mNo = info.get("MEMBER_NO").toString();
					if(Integer.parseInt(mNo) == p.getMemberNo()) {
						switch((String)info.get("L_CATEGORY")) {
						case "disease" : pDisease +=  (String)info.get("S_CATEGORY") + "/"; break;
						case "diseaseLevel": pDiseaseLevel += (String)info.get("S_CATEGORY") + "/"; break;
						}
					}
				}
				m.put("보유질환", pDisease.substring(0, pDisease.lastIndexOf("/")));	
				m.put("중증도", pDiseaseLevel.substring(0, pDiseaseLevel.lastIndexOf("/")));	
			}	
			promptPatientList.add(m);
		}// 후보에 대한 정보 가공 끝
		
			
		// 5. 프롬프트 작성
		String prompt = "간병인 정보는" + infoMap.toString() + "이고" + "환자 목록은" + promptPatientList.toString() + "이다."
						+ "간병인의 정보를 바탕으로 가장 적절한 매칭번호 " + selectNum + "개만 숫자로만 짧게 대답해줘.";
		
		// 6. 프롬프트를 전달하고 결과값 받아오기
		String result = botController.chat(prompt); // "2, 4, 8, 10, 14"
		System.out.println("GPT가 추천한 매칭번호 : " + result);
		String[] choice = result.split(", ");
		ArrayList<Integer> choiceNoList = new ArrayList<Integer>();
		for(int i = 0; i < choice.length; i++) {
			if(choice[i].contains(".")) {
				choiceNoList.add(Integer.parseInt(choice[i].split(".")[0]));
			} else {
				choiceNoList.add(Integer.parseInt(choice[i]));
			}
		} // [2, 5, 8, 10, 14] 
//			
		// 7. View로 전달한 결과값만 추리기
		// 이름, 성별, 나이, 지역, 질환, 금액, 매칭번호, 멤버번호
		ArrayList<Patient> completeList = mService.choicePatientList(choiceNoList);
		ArrayList<HashMap<String, Object>> diseaseList = mService.getPatientInfo(choiceNoList);
		
		for(Patient p : completeList) {
			String[] add = p.getPtAddress().split(" ");
			p.setPtAddress(add[0] + " " + add[1]);
			for(HashMap<String, Object> m : promptPatientList) {
				String matNo = m.get("매칭번호").toString();
				if(p.getMatNo() == Integer.parseInt(matNo)) {
					p.setPtDisease(m.get("보유질환").toString());
				}
			}
		}
		
		return completeList;
	}
	
	// 임시 : 메인페이지 이동시 캘린더 이벤트 조회
	// 메인페이지 이동 후에 환자 메인페이지 렌더링 도중에 캘린더 이벤트를 조회하게 된다.
	@GetMapping("caregiverCalendarEvent.me")
	@ResponseBody
	public void caregiverCalendarEvent(Model model, HttpServletResponse response) {
		Member loginUser = (Member)model.getAttribute("loginUser");
		
		// 일정 조회 
		ArrayList<CalendarEvent> eList = new ArrayList<CalendarEvent>();
		if(loginUser != null) {
			eList = mService.caregiverCalendarEvent(loginUser.getMemberNo());
			//[CalendarEvent(matNo=69, title=null, start=null, end=null, money=150000, hospitalNo=0, hospitalAddress=null, hospitalName=null, beginTime=12:00, endTime=15:00, matMode=2, matchingType=null, ptCount=1, matAddressInfo=서울 동대문구 망우로 82 202호777, ptNo=61, matDate=2024-08-09,2024-08-16,2024-08-15,2024-08-22,2024-08-23, beginDt=2024-08-09, endDt=2024-08-23)]
		}
//		for(CalendarEvent c : eList) {
//			c.setStart(c.getBeginDt());
//			c.setEnd(c.getEndDt());
//			
//			if(c.getPtCount() > 1) {
//				if(c.getMatMode() == 1) c.setTitle("개인 기간제 간병");
//				else c.setTitle("개인 시간제 간병");
//			} else {
//				if(c.getMatMode() == 1) c.setTitle("공동 기간제 간병");
//				else c.setTitle("공동 시간제 간병");
//			}
//		}
		
		JSONArray array = new JSONArray();
		
		for(CalendarEvent c : eList) {
			JSONObject obj = new JSONObject();
			obj.put("start", c.getBeginDt());
			obj.put("end", c.getEndDt());
			obj.put("title", "테스트");
		}
		
		
		
		
		
		
		// GSON
		response.setContentType("application/json; charset=UTF-8");
		//GsonBuilder gb = new GsonBuilder().setDateFormat("YYYY-MM-DD");
		Gson gson = new Gson();
		try {
			gson.toJson(eList, response.getWriter());
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	// 간병인 메인페이지로 가기 
	@GetMapping("caregiverMain.me")
	public String caregiverMain(HttpSession session, Model model) {
		// 1. 자동 추천 목록 받아오기
		Member loginUser = (Member)session.getAttribute("loginUser");
		int memberNo = 0;
		if(loginUser != null) {
			memberNo = loginUser.getMemberNo(); 
			ArrayList<Patient> completeList = openAiPatientChoice(memberNo, 5); // 추천목록이 없으면 null로 넘어옴
			System.out.println(completeList);
			model.addAttribute("completeList", completeList);
		}
		
		
		
		return "caregiverMain";
	}
	
	// 자동추천을 비동기 통신으로 요청
	@GetMapping("refreshPatientChoice.me")
	@ResponseBody
	public void refreshChoice(@RequestParam("memberNo") int memberNo, @RequestParam("selectNum") int selectNum,
								HttpServletResponse response) {
		// 후보군이 있을 때만 새로고침 버튼이 활성화 되기 때문에 null로 넘어오는 경우는 배제함
		ArrayList<Patient> completeList = openAiPatientChoice(memberNo, selectNum); 
		
		Gson gson = new Gson();
		response.setContentType("application/json; charset=UTF-8;");
		try {
			gson.toJson(completeList, response.getWriter());
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	// 임시버튼 : 환자 메인페이지로 가기
	@GetMapping("patientMain.me")
	public String patientMain(HttpSession session, Model model) {
		// 1. 자동 추천 목록 받아오기
		Member loginUser = (Member)session.getAttribute("loginUser");
		int memberNo = 0;
		if(loginUser != null) {
			memberNo = loginUser.getMemberNo(); 
			ArrayList<CareGiver> completeList = openAiCaregiverChoice(memberNo, 5); // 추천목록이 없으면 null로 넘어옴
			model.addAttribute("completeList", completeList);
		}
		
		
		
		return "patientMain";
	}
	
	//회원가입 페이지 이동
	@GetMapping("enroll1View.me")
	public String enroll1View(HttpSession session) {
		
		
		//멤버 테이블만 있고 환자/ 간병인 테이블에 insert됮 않은 경우 멤버 테이블 삭제 -> 회원가입 도충 탈출 등
		String memberCategory = (String)session.getAttribute("tempMemberCategory");		
		
		String table = "";
		if(memberCategory.equals("C")) {
			table = "caregiver";
		}else {
			table = "patient";
		}
		
		int resultNoInfo = mService.noInfomemberdle();		
		System.out.println(resultNoInfo);
		
		return "enroll1";
	}
	
	
	//아이디 중복체크
	@ResponseBody
	@PostMapping("idCheck.me")
	public String idCheck(@RequestParam("id") String id) {		
		int result = mService.idCheck(id);	
		if(result == 0) {
			return "usable";
		}else{
			return "unusable";
		}

	}
	
	//닉네임 중복 체크
	@ResponseBody
	@PostMapping("nickNameCheck.me")
	public String nickNameCheck(@RequestParam("nickName") String nickName) {		
		int result = mService.nickNameCheck(nickName);	
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
						@RequestParam("email") String email, @RequestParam("emailDomain") String emailDomain, 
						HttpSession session, Model model) {
		
		//간병인/환자 택
		String memberCategory = (String)session.getAttribute("tempMemberCategory");		
		m.setMemberCategory(memberCategory);
		
		//대문 등록 카테고리 session삭제
		session.removeAttribute("tempMemberCategory");
		
		String memberPwd = bCrypt.encode(m.getMemberPwd().toLowerCase());
		m.setMemberPwd(memberPwd);
		
		String memberAddress = postcode +"//"+ roadAddress +"//"+ detailAddress;
		m.setMemberAddress(memberAddress);
		
		String memberEmail = email + "@" + emailDomain;
		m.setMemberEmail(memberEmail);
		
		System.out.println("회원가입 검증=" + m);
		
		int result = mService.enroll(m);
		
		//회원가입용 session데이터
		model.addAttribute("enrollmember", m);
		System.out.println("회원가입 데이터 전송 검증 =" +m);
		
		
		if(result > 0) {
			if(m.getMemberCategory().equals("C")) {
				return "enroll2";
			}else {
				return "enroll3";
			}
		}else {
			throw new MemberException("회원가입에 실패했습니다.");
		}		
		
	}
	
	
	@GetMapping("myInfoMatching.me")
	public String myInfoMatching() {		//마이페이지 현재매칭정보 확인용
		return "myInfoMatching";
	}
	
	@GetMapping("myInfoMatchingHistory.me")
	public String myInfoMatchingHistory(HttpSession session) {		//마이페이지 매칭 이력 확인용
		
		
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		if(loginUser != null) {
			char check = loginUser.getMemberCategory().charAt(0);
			switch(check) {
				case 'C': return "myInfoMatchingHistory";
				case 'P': return "myInfoMatchingHistoryP";
				case 'A': return null;
			}
		}
		
		throw new MemberException("로그인없음. 인터셉터설정");
	}
	
	@GetMapping("myInfoMatchingReview.me")
	public String myInfoMatchingReview(HttpSession session) {		//마이페이지 매칭 이력 확인용
		
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		if(loginUser != null) {
			char check = loginUser.getMemberCategory().charAt(0);
			switch(check) {
				case 'C': return "myInfoMatchingReview";
				case 'P': return "myInfoMatchingReviewP";
				case 'A': return null;
			}
		}
		throw new MemberException("로그인없음. 인터셉터설정");
	}
	
	@GetMapping("myInfoBoardList.me")
	public String myInfoBoardList() {		//마이페이지 보드작성 확인용
		return "myInfoBoardList";
	}
	
	
	//간병인 회원가입(간병인 정보 입력)
	@PostMapping("enrollCaregiver.me")
	public String enrollCaregiver(@ModelAttribute CareGiver cg, @RequestParam("careService") String[] careServiceArr, HttpSession session) {
		System.out.println("데이터 확인"+cg);
			
		//간병인 memberNo 세팅
		cg.setMemberNo(((Member)session.getAttribute("enrollmember")).getMemberNo());		
		
		//간병인 기본 정보 세팅
		String careService = "";		
		for(int i = 0; i < careServiceArr.length; i++) {
			if(i < careServiceArr.length -1 ) {
				careService += careServiceArr[i] + "//";
			}else {
				careService += careServiceArr[i];
			}
		}
		cg.setCareService(careService);
		
		System.out.println("간병인 정보=" + cg);
		
		int result1 = mService.enrollCareGiver(cg);
		System.out.println("result1" + result1);
		
		int result2 = mService.enrollInfoCategory(cg);
		System.out.println("result2" + result2);
		
		if(result1 > 0 || result2 > 0 ) {			
			session.removeAttribute("enrollmember");
			return "enroll4";
		}else {
			throw new MemberException("회원가입에 실패했습니다.");
		}		
	}
	
	
	//환자 회원가입
	@PostMapping("enrollPatient.me")
	public String enrollPatient(@ModelAttribute Patient pt, 
							@RequestParam("postcode") String postcode, @RequestParam("roadAddress") String roadAddress, @RequestParam("detailAddress") String detailAddress,
							@RequestParam("ptService") String[] ptServiceArr, HttpSession session) {
		
		//간병인 memberNo 세팅
		pt.setMemberNo(((Member)session.getAttribute("enrollmember")).getMemberNo());	
		
		
		//돌봄 주소 세팅
		String ptAddress = postcode +"//"+ roadAddress +"//"+ detailAddress;
		pt.setPtAddress(ptAddress);
		
		//간병인 기본 정보 세팅
		String ptService = "";		
		for(int i = 0; i < ptServiceArr.length; i++) {
			if(i < ptServiceArr.length -1 ) {
				ptService += ptServiceArr[i] + "//";
			}else {
				ptService += ptServiceArr[i];
			}
		}
		pt.setPtService(ptService);

		System.out.println("간병인 정보=" + pt);
		
		int result1 = mService.enrollPatient(pt);
		System.out.println("result1" + result1);
		
		int result2 = mService.enrollInfoCategory(pt);
		System.out.println("result2" + result2);
		
		if(result1 > 0 || result2 > 0 ) {			
			session.removeAttribute("enrollmember");
			return "enroll4";
		}else {
			
			throw new MemberException("회원가입에 실패했습니다.");
		}		
		
		

	}
	
	
	
	
	
	
	
	
	
	
	@GetMapping("findId.me")
	public String findId() {
		return "findIdPage";
	}
	
	@GetMapping("findPwd.me")
	public String findPwd() {
		return "findPwdPage";
	}
	

	
		
		
		@PostMapping("findIdResult.me")
	public String findIdResult(@RequestParam("memberId") String memberId,@RequestParam("memberPhone") String memberPhone,Model model) {
		Member member = new Member();
		member.setMemberId(memberId);
		member.setMemberPhone(memberPhone);
		System.out.println(member);
		
		Member findMember = mService.findIdResult(member);
		System.out.println(findMember);
		if(findMember !=null) {
			model.addAttribute("findMember",findMember);
			return "findIdResult";
		} else {
			throw new MemberException("해당 로그인정보로 가입된 아이디를 찾을 수 없습니다.");
		}
		
		
		
	}
	
	@PostMapping("/api/verify-member") //입력한 아이디로 등록된 핸드폰번호 있는지 확인
	@ResponseBody
	public Map<String, Object> verifyMember(@RequestBody Member member) {
	    Map<String, Object> response = new HashMap<>();
	    
	    Member findMember = mService.findIdResult(member);
	    response.put("success", findMember != null);
	    
	    return response;
	}
	
	
	@PostMapping("/api/send-auth-code") // 인증번호 전송
	@ResponseBody
	public Map<String, Object> sendAuthCode(@RequestBody Map<String, String> request,HttpSession session) {
	    String phoneNumber = request.get("phoneNumber");
	    String authCode = generateAuthCode(); // 6자리 랜덤 숫자 생성
	    
	    boolean success = mService.sendSms(phoneNumber, "인증번호: " + authCode);
	    
	    Map<String, Object> response = new HashMap<>();
	    response.put("success", success);
	    
	    if (success) {        
	       session.setAttribute("authCode", authCode);
	    }
	    
	    return response;
	}

	private String generateAuthCode() { // 인증번호 생성
	    Random random = new Random();
	    StringBuilder sb = new StringBuilder();
	    for (int i = 0; i < 6; i++) {
	        sb.append(random.nextInt(10));
	    }
	    return sb.toString();
	}
	
	@PostMapping("/api/verify-auth-code") // 인증번호 확인
	@ResponseBody
	public Map<String, Object> verifyAuthCode(@RequestBody Map<String, String> request, HttpSession session) {
	    String inputAuthCode = request.get("authCode");
	    String sessionAuthCode = (String) session.getAttribute("authCode");

	    Map<String, Object> response = new HashMap<>();
	    boolean isVerified = sessionAuthCode != null && sessionAuthCode.equals(inputAuthCode);
	    response.put("success", isVerified);

	    if (isVerified) {
	        // 인증 성공 시 세션에 인증 상태 저장
	        session.setAttribute("isVerified", true);
	    }

	    return response;
	}

	@PostMapping("findPwdResult.me") // 비밀번호 재설정 페이지로 이동
	public String findPwdResult(HttpSession session) {
	    Boolean isVerified = (Boolean) session.getAttribute("isVerified");
	    
	    if (isVerified != null && isVerified) {
	        // 인증된 경우 비밀번호 재설정 페이지로 이동
	        return "findPwdResult";
	    } else {
	        // 인증되지 않은 경우 다시 비밀번호 찾기 페이지로 리다이렉트
	        return "redirect:findPwd.me";
	    }
	}
	
	@GetMapping("moreWorkInfo.me")
	public String moreWorkInfo(HttpSession session, Model model) {
		// 자동추천 목록 받아오기
		Member loginUser = (Member)session.getAttribute("loginUser");
		int memberNo = 0;
		if(loginUser != null) {
			memberNo = loginUser.getMemberNo();
			ArrayList<Patient> completeList = openAiPatientChoice(memberNo, 10);
			
			if(!completeList.isEmpty()) {
				model.addAttribute("completeList", completeList);
			} 
		}
		
		
		return "moreWorkInfo";
	}
	
	
	//무한스크롤 테스트 중 : 성공
	@PostMapping("workInfoTest.me")
	@ResponseBody
	public void workInfoTest(HttpServletResponse response, @RequestParam(value="page", defaultValue="1") int currentPage) {
		// 페이지 첫 로드시 또는 검색조건이 하나도 없이 검색버튼을 눌렀을 때 이곳으로 요청이 들어옴
		
		Gson gson = new Gson();
		ArrayList<String> list = new ArrayList<String>();
		list.add("기본1");
		list.add("기본2");
		list.add("기본3");
		list.add("기본4");
		list.add("기본5");
		list.add("기본6");
		list.add("기본7");
		list.add("기본8");
		response.setContentType("application/json; charset=UTF-8");
		try {
			gson.toJson(list, response.getWriter());
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	// 간병인 일감찾기 페이지에서의 검색 요청을 처리
	@PostMapping("searchPatientList.me")
	@ResponseBody
	public void searchPatientList(@RequestParam("condition") String obj, @RequestParam(value="page", defaultValue="1") int page,
									HttpServletResponse response) {
		// 검색 조건이 하나라도 있는 경우만 이곳으로 들어온다
		
		System.out.println(obj);
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, String> map =
			               mapper.readValue(obj, new TypeReference<Map<String, String>>(){});
			
			// 검색조건과 페이지에 맞게 조회해와야함
			
			Gson gson = new Gson();
			ArrayList<String> list = new ArrayList<String>();
			list.add("search1");
			list.add("search2");
			list.add("search3");
			list.add("search4");
			list.add("search5");
			list.add("search6");
			list.add("search7");
			list.add("search8");
			response.setContentType("application/json; charset=UTF-8");
			gson.toJson(list, response.getWriter());
			
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping("moreCaregiverInfo.me")
	public String moreCaregiverInfo(@RequestParam(value = "page", defaultValue="1") int page, HttpSession session, Model model) {
		// 자동추천 목록 받아오기
		Member loginUser = (Member)session.getAttribute("loginUser");
		int memberNo = 0;
		if(loginUser != null) {
			memberNo = loginUser.getMemberNo();
			ArrayList<CareGiver> completeList = openAiCaregiverChoice(memberNo, 10);
			
			if(!completeList.isEmpty()) {
				model.addAttribute("completeList", completeList);
			} 
		}
		
		return "moreCaregiverInfo";
	}
	
	
	// 간병인 일감찾기 페이지에서의 검색 요청을 처리
		@PostMapping("searchCaregiverList.me")
		@ResponseBody
		public void searchCaregiverList(@RequestParam("condition") String obj, @RequestParam(value="page", defaultValue="1") int page,
										HttpServletResponse response) {
			// 검색 조건이 하나라도 있는 경우만 이곳으로 들어온다
			System.out.println(obj);
			
			ObjectMapper mapper = new ObjectMapper();
			try {
				Map<String, String> map =
				               mapper.readValue(obj, new TypeReference<Map<String, String>>(){});
				
				// 검색조건과 페이지에 맞게 조회해와야함
				
				Gson gson = new Gson();
				ArrayList<String> list = new ArrayList<String>();
				list.add("search1");
				list.add("search2");
				list.add("search3");
				list.add("search4");
				list.add("search5");
				list.add("search6");
				list.add("search7");
				list.add("search8");
				response.setContentType("application/json; charset=UTF-8");
				gson.toJson(list, response.getWriter());
				
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (JsonIOException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
	
		@PostMapping("moreCaregiverInfo.me")
		@ResponseBody
		public void moreCaregiverInfo(HttpServletResponse response, @RequestParam(value="page", defaultValue="1") int currentPage) {
			// 페이지 첫 로드시 또는 검색조건이 하나도 없이 검색버튼을 눌렀을 때 이곳으로 요청이 들어옴
			
			Gson gson = new Gson();
			ArrayList<String> list = new ArrayList<String>();
			list.add("기본1");
			list.add("기본2");
			list.add("기본3");
			list.add("기본4");
			list.add("기본5");
			list.add("기본6");
			list.add("기본7");
			list.add("기본8");
			response.setContentType("application/json; charset=UTF-8");
			try {
				gson.toJson(list, response.getWriter());
			} catch (JsonIOException | IOException e) {
				e.printStackTrace();
			}
		}
	
	
	public ArrayList<CareGiver> openAiCaregiverChoice(int memberNo, int selectNum) {
		// 1. 환자 본인 정보 조회 
//		조회할 항목
//			필수입력 : 원하는 서비스(1~3개), 적정비용, 성별, 나이, 주소, 키, 몸무게, 국적
//			선택입력 : 보유질환(0~10개), 중증도 
//		항목의 출처
//			MEMBER_INFO : 원하는 서비스(필수, 1~3개)
//			PATIENT: 성별(필수, MEMBER_GENDER), 나이(필수, MEMBER_AGE), 주소(필수, MEMBER_ADDRESS), 키, 몸무게
//			MEMBER : 국적(필수, MEMBER_NATIONAL)
		
		System.out.println("멤버넘버 : " + memberNo);
		
		HashMap<String, String> infoMap =  mService.getPatientMyInfo(memberNo); 
					//{연령=40, 국적=내국인, 키=180, 몸무게=79, 주소=서울 동대문구 망우로 82 202호777, 성별=여성}
		
		
		System.out.println("인포맵 : " + infoMap);
		
		
		if(Integer.parseInt(String.valueOf(infoMap.get("연령"))) < 0 ) {
			infoMap.put("연령", (Integer.parseInt(infoMap.get("연령").toString() + 100)) + "");
		}
		
		ArrayList<HashMap<String, String>> myExpList = mService.getPatientMyExp(memberNo); 
//		[{S_CATEGORY=병원돌봄, L_CATEGORY=service}, {S_CATEGORY=섬망, L_CATEGORY=disease}, 
//			{S_CATEGORY=경증, L_CATEGORY=diseaseLevel}]

		
		
		ArrayList<HashMap<String, String>> myWantList = mService.getCaregiverMyWant(memberNo); // 마이페이지에서 선택적으로 입력
		
		// 2. 환자 본인 정보 가공
		String[] address = infoMap.get("주소").split(" ");
		infoMap.put("주소", address[0] + " " + address[1]);
		String service = ""; // 필수, 1~3개
		String disease = ""; // 선택, 0~10개
		String diseaseLevel = ""; // 선택, 0~3개
		
		for(HashMap<String, String> m : myExpList) {
			switch(m.get("L_CATEGORY")) {
			case "service" : service += m.get("S_CATEGORY") + "/"; break;
			case "disease" : disease += m.get("S_CATEGORY") + "/"; break;
			case "diseaseLevel" : diseaseLevel += m.get("S_CATEGORY") + "/"; break;
			}
		}
		
		if(service.length() > 0) { // 간병인의 서비스 경력이 존재하는 경우를 가르킴
			service = service.substring(0, service.lastIndexOf("/"));
			infoMap.put("원하는 서비스", service);
		}
		if(disease.length() > 0) {
			disease = disease.substring(0, disease.lastIndexOf("/"));
			infoMap.put("보유질환", disease);
		}
		if(!diseaseLevel.isEmpty()) { // isEmpty연습하기
			diseaseLevel = diseaseLevel.substring(0, diseaseLevel.lastIndexOf("/"));
			infoMap.put("중증도", diseaseLevel); 
		}
		
		if(!myWantList.isEmpty()) {
			String wantCareer = ""; // 선택
			String wantLicense = ""; // 선택
			
			for(HashMap<String, String> m : myWantList) {
				switch(m.get("L_CATEGORY")) {
				case "career" : wantCareer += m.get("S_CATEGORY") + "/"; break;
				case "license" : wantLicense += m.get("S_CATEGORY") + "/"; break;
				}
			}
			
			if(wantCareer.length() > 0) { // 간병인이 원하는 서비스
				wantCareer = wantCareer.substring(0, wantCareer.lastIndexOf("/"));
				infoMap.put("원하는 간병인 경력", wantCareer);
			}
			
			if(wantLicense.length() > 0) { // 간병인이 원하는 돌봄질환
				wantLicense = wantLicense.substring(0, wantLicense.lastIndexOf("/"));
				infoMap.put("원하는 간병인의 자격증", wantLicense);
			}
		}
		// 가공 종료! => infoMap
		// {연령=40, 국적=내국인, 중증도=경증, 키=180, 몸무게=79, 보유질환=섬망, 주소=서울 동대문구, 성별=여성, 원하는 서비스=병원돌봄}
		
		
		// 3. 간병인 목록 조회
//		ArrayList<HashMap<String, Object>> promptCaregiverList = new ArrayList<HashMap<String, Object>>(); // 프롬프트에 전달할 최종 후보군 리스트
		String myAddress = infoMap.get("주소").contains("서울") ? "서울" : (infoMap.get("주소").contains("제주") ? "제주" : infoMap.get("주소"));
//		간병인 기본정보 조회 : 회원번호, 성별, 나이, 주소, 국적, 제공하려는 서비스, 경력, 서비스한 경험, 돌봄경험, 자격증, 최소비용
//		MEMBER : MEMBER_NO, MEMBER_GENDER, MEMBER_AGE, MEMBER_ADDRESS, MEMBER_NATIONAL => 필수
//		CAREGIVER: MIN_MONEY (필수)
//		MEMBER_INFO: 제공하려는 서비스(필수, 1~3개), 경력(필수,1개), 서비스한 경험(선택,0~3개), 돌봄경험(선택, 0~10개), 자격증(선택, 0~3개)
		
		
		HashMap<String, Object> condition = new HashMap<String, Object>();
		condition.put("address", myAddress);
		condition.put("selectNum", selectNum*2);
		ArrayList<HashMap<String, Object>> cList = mService.selectCaregiverList(condition); // 길이 : 0~10 // **프롬프트**
		//[{연령=58, 국적=내국인, 최소요구금액=10000, 주소=서울 강북구 삼양로22길 4 102호, 성별=남성, 회원번호=49},
		
		if(cList.isEmpty()) { // 조건에 맞는 후보 환자가 없을 땐 null로 넘겨야 한다.
			return null;
		}
		
		
		ArrayList<Integer> mNoList = new ArrayList<Integer>();
		for(HashMap<String,Object> m : cList) {
			int age = Integer.parseInt(m.get("연령").toString());
			if(age < 0) {
				m.put("연령", age+100); // 연령 계산 오류났을 때 재조정시킴
			}
			mNoList.add(Integer.parseInt(m.get("회원번호").toString()));
		} // [79, 42, 22, 49, 50, 46, 23, 14, 83, 84]
		
		
		ArrayList<HashMap<String, Object>> cExpList = mService.selectCaregiverInfo(mNoList);
//		[{S_CATEGORY=병원돌봄, L_CATEGORY=service, MEMBER_NO=55}, {S_CATEGORY=병원돌봄, L_CATEGORY=service, MEMBER_NO=15}, {S_CATEGORY=병원돌봄, L_CATEGORY=service, MEMBER_NO=54}, {S_CATEGORY=가정돌봄, L_CATEGORY=service, MEMBER_NO=15}, {S_CATEGORY=치매, L_CATEGORY=disease, MEMBER_NO=15}, {S_CATEGORY=치매, L_CATEGORY=disease, MEMBER_NO=54}, {S_CATEGORY=욕창, L_CATEGORY=disease, MEMBER_NO=54}, {S_CATEGORY=하반신 마비, L_CATEGORY=disease, MEMBER_NO=15}, {S_CATEGORY=와상 환자, L_CATEGORY=disease, MEMBER_NO=15}, {S_CATEGORY=기저귀 케어, L_CATEGORY=disease, MEMBER_NO=15}, {S_CATEGORY=기저귀 케어, L_CATEGORY=disease, MEMBER_NO=55}, {S_CATEGORY=기저귀 케어, L_CATEGORY=disease, MEMBER_NO=54}, {S_CATEGORY=의식 없음, L_CATEGORY=disease, MEMBER_NO=54}, {S_CATEGORY=중증, L_CATEGORY=diseaseLevel, MEMBER_NO=55}, {S_CATEGORY=중증, L_CATEGORY=diseaseLevel, MEMBER_NO=54}, {S_CATEGORY=경증, L_CATEGORY=diseaseLevel, MEMBER_NO=15}]
		
		// ArrayList<HashMap<String, Object>> promptCaregiverList 에 담아야함
		
		for(HashMap<String, Object> m : cList) {
			for(HashMap<String, Object> e : cExpList) {
				String wantService = "";
				String haveService = "";
				String career = "";
				String haveDisease = "";
				String haveLicense = "";
				if(m.get("회원번호").toString().equals(e.get("MEMBER_NO").toString())) {
					switch(e.get("L_CATEGORY").toString()) {
					case "service": wantService += e.get("S_CATEGORY").toString() + "/"; break;
					case "serviceCareer": haveService += e.get("S_CATEGORY").toString() + "/"; break;
					case "career": career += e.get("S_CATEGORY").toString() + "/"; break;
					case "disease": haveDisease += e.get("S_CATEGORY").toString() + "/"; break;
					case "license": haveLicense += e.get("S_CATEGORY").toString() + "/"; break;
					}
				}
				if(wantService.length() > 0) {
					m.put("제공하려는 서비스", wantService.substring(0, wantService.lastIndexOf("/")));	
				}
				if(haveService.length() > 0) {
					m.put("제공해봤던 서비스", haveService.substring(0, haveService.lastIndexOf("/")));	
				}
				if(career.length() > 0) {
					m.put("경력", career.substring(0, career.lastIndexOf("/")));	
				}
				if(haveDisease.length() > 0) {
					m.put("돌봄해봤던 질환", haveDisease.substring(0, haveDisease.lastIndexOf("/")));	
				}
				if(haveLicense.length() > 0) {
					m.put("보유한 자격증", haveLicense.substring(0, haveLicense.lastIndexOf("/")));	
				}
			}
		}// [{연령=69, 국적=내국인, 최소요구금액=50000, 보유한 자격증=요양보호사, 주소=서울 중랑구 망우로74가길 16 3층, 성별=남성, 돌봄해봤던 질환=기저귀 케어, 회원번호=85, 경력=3, 제공하려는 서비스=동행서비스}, {연령=12, 국적=내국인, 최소요구금액=10000, 주소=서울 강북구 4.19로12길 8 1231, 성별=남성, 돌봄해봤던 질환=섬망, 회원번호=84, 경력=0, 제공하려는 서비스=가정돌봄}, {연령=30, 국적=외국인, 최소요구금액=15000, 보유한 자격증=간병사, 주소=서울 강북구 4.19로32길 69 수유동101호, 성별=남성, 회원번호=14, 경력=0, 제공하려는 서비스=병원돌봄}, {연령=42, 국적=내국인, 최소요구금액=100000, 보유한 자격증=간병사, 주소=서울 영등포구 63로 7 101호, 성별=남성, 돌봄해봤던 질환=기저귀 케어, 회원번호=23, 경력=3, 제공하려는 서비스=동행서비스}, {연령=24, 국적=내국인, 제공해봤던 서비스=동행서비스, 최소요구금액=50000, 주소=서울 중구 을지로 6 3층, 성별=남성, 돌봄해봤던 질환=석션, 회원번호=46, 경력=0, 제공하려는 서비스=동행서비스}, {연령=0, 국적=내국인, 최소요구금액=10000, 주소=서울 도봉구 노해로 133 집, 성별=남성, 회원번호=22, 경력=0}, {연령=45, 국적=외국인, 최소요구금액=10000, 보유한 자격증=간병사, 주소=서울 강서구 곰달래로22길 8 12312, 성별=남성, 돌봄해봤던 질환=치매, 회원번호=50, 경력=0}, {연령=58, 국적=내국인, 제공해봤던 서비스=동행서비스, 최소요구금액=10000, 보유한 자격증=간호조무사, 주소=서울 강북구 삼양로22길 4 102호, 성별=남성, 돌봄해봤던 질환=의식 없음, 회원번호=49, 경력=3, 제공하려는 서비스=가정돌봄}, {연령=34, 국적=내국인, 최소요구금액=50000, 주소=서울 강북구 방학로 384 201번지, 성별=남성, 회원번호=79, 경력=0}, {연령=44, 국적=내국인, 최소요구금액=20000, 보유한 자격증=요양보호사, 주소=서울 종로구 경희궁3가길 7 1, 성별=남성, 돌봄해봤던 질환=피딩, 회원번호=83, 경력=8, 제공하려는 서비스=가정돌봄}]
		// 후보에 대한 정보 가공 끝 => cList
		
			
		// 5. 프롬프트 작성
		String prompt = "환자의 정보는" + infoMap.toString() + "이고" + "환자 목록은" + cList.toString() + "이다."
						+ "환자의 정보를 바탕으로 가장 적절한 회원번호 " + selectNum + "개만 숫자로만 짧게 대답해줘.";
		
		// 6. 프롬프트를 전달하고 결과값 받아오기
		String result = botController.chat(prompt); // "2, 4, 8, 10, 14"
		System.out.println("GPT가 추천한 매칭번호 : " + result);
		String[] choice = result.split(", ");
		ArrayList<Integer> choiceNoList = new ArrayList<Integer>();
		for(int i = 0; i < choice.length; i++) {
			if(choice[i].contains(".")) {
				choiceNoList.add(Integer.parseInt(choice[i].split(".")[0]));
			} else {
				choiceNoList.add(Integer.parseInt(choice[i]));
			}
		} // [2, 5, 8, 10, 14] 
//			
		// 7. View로 전달한 결과값만 추리기
		// cList : {연령=69, 국적=내국인, 최소요구금액=50000, 보유한 자격증=요양보호사, 주소=서울 중랑구 망우로74가길 16 3층, 성별=남성, 돌봄해봤던 질환=기저귀 케어, 회원번호=85, 경력=3, 제공하려는 서비스=동행서비스}
		ArrayList<CareGiver> completeList = mService.choiceCaregiverList(choiceNoList);
		ArrayList<HashMap<String, Object>> completeInfoList = mService.selectCaregiverInfo(choiceNoList);
		
		System.out.println(completeInfoList);
		
		for(CareGiver c : completeList) {
			String[] cAddress = c.getCaregiverAddress().split(" ");
			c.setCaregiverAddress(cAddress[0] + " " + cAddress[1]);
			
			for(HashMap<String, Object> m : completeInfoList) {
				if(c.getMemberNo() == Integer.parseInt(m.get("MEMBER_NO").toString())) {
					
					String wantService = "";
					String haveService = "";
					String career = "";
					String haveDisease = "";
					String haveLicense = "";
					
					switch(m.get("L_CATEGORY").toString()) {
					case "service": wantService += m.get("S_CATEGORY").toString() + "/"; break;
					case "serviceCareer": haveService += m.get("S_CATEGORY").toString() + "/"; break;
					case "career": career += m.get("S_CATEGORY").toString() + "/"; break;
					case "disease": haveDisease += m.get("S_CATEGORY").toString() + "/"; break;
					case "license": haveLicense += m.get("S_CATEGORY").toString() + "/"; break;
					}
					
					if(wantService.length() > 0) {
						c.setWantService(wantService.substring(0, wantService.lastIndexOf("/")));
					}
					if(haveService.length() > 0) {
						c.setHaveService(haveService.substring(0, haveService.lastIndexOf("/")));	
					}
					if(career.length() > 0) {
						c.setCareer(career.substring(0, career.lastIndexOf("/")));	
					}
					if(haveDisease.length() > 0) {
						c.setHaveDisease(haveDisease.substring(0, haveDisease.lastIndexOf("/")));	
					}
					if(haveLicense.length() > 0) {
						c.setHaveLicense(haveLicense.substring(0, haveLicense.lastIndexOf("/")));	
					}
				}
			}
		}
		
		System.out.println("completeList : " + completeList);
		return completeList;
	}
	
	@GetMapping("refreshCaregiverChoice.me")
	@ResponseBody
	public void refreshCaregiverChoice(@RequestParam("memberNo") int memberNo, @RequestParam("selectNum") int selectNum,
										HttpServletResponse response) {
		// 후보군이 있을 때만 새로고침 버튼이 활성화 되기 때문에 null로 넘어오는 경우는 배제함
		
		ArrayList<CareGiver> completeList = openAiCaregiverChoice(memberNo, selectNum); 
		Gson gson = new Gson();
		response.setContentType("application/json; charset=UTF-8;");
		try {
			gson.toJson(completeList, response.getWriter());
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}



	












