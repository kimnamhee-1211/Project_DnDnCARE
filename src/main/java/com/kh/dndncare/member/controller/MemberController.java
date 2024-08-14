package com.kh.dndncare.member.controller;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.board.model.vo.Reply;
import com.kh.dndncare.common.AgeCalculator;
import com.kh.dndncare.common.Pagination;
import com.kh.dndncare.matching.model.vo.CareReview;
import com.kh.dndncare.matching.model.vo.MatMatptInfo;
import com.kh.dndncare.matching.model.vo.MatMatptInfoPt;
import com.kh.dndncare.matching.model.vo.MatPtInfo;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.matching.model.vo.RequestMatPt;
import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.service.MemberService;
import com.kh.dndncare.member.model.vo.CalendarEvent;
import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.member.model.vo.Info;
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@SessionAttributes({ "loginUser", "tempMemberCategory", "enrollmember" })
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
	public String selectMemberType(@PathVariable("memberType") String memberType, Model model) {
		String tempMemberCategory;
		switch (memberType) {
		case "patient":
			tempMemberCategory = "P";
			break;
		case "careGiver":
			tempMemberCategory = "C";
			break;
		default:
			return "errorPage";
		}

		model.addAttribute("tempMemberCategory", tempMemberCategory);
		return "login";

	}

	@GetMapping("myInfo.me")
	public String myInfo(HttpSession session, Model model) { // 마이페이지 확인용
		// ArrayList<Member> m = mService.selectAllMember(); //노트북,피시방에 디비가없으니 접근용

		Member loginUser = (Member) session.getAttribute("loginUser");

		if (loginUser != null) {
			Patient p = mService.selectPatient(loginUser.getMemberNo());
			
			// List<Integer> memberInfo =
			// mService.selectMemberInfo(loginUser.getMemberNo());

			char check = loginUser.getMemberCategory().charAt(0);
			Info memberInfo = categoryFunction(loginUser.getMemberNo(), true); // 내정보
			Info wantInfo = categoryFunction(loginUser.getMemberNo(), false); // 원하는간병인정보
			model.addAttribute("memberInfo", memberInfo);
			System.out.println("여기가 멤버인포 : " + memberInfo);
			model.addAttribute("wantInfo", wantInfo);
			System.out.println("여기가 왠트인포 : " + wantInfo);


			switch (check) {
			case 'C':
				CareGiver cg = mService.selectCareGiver(loginUser.getMemberNo());
				model.addAttribute("cg", cg);
				return "myInfo";

			case 'P':
				model.addAttribute("p", p);
				System.out.println(wantInfo.getInfoDisease());

				// Patient pWant = categoryFunction()

				return "myInfoP";

			case 'A':
				return "myInfoA";

			}
		}
		throw new MemberException("로그인이 필요합니다.인터셉터만드세요");}

	

	public Info categoryFunction(int memberNo, boolean choice) {// 카테고리 가공 메소드

		if (choice) {
			ArrayList<HashMap<String, String>> category = mService.getCaregiverExp(memberNo); // 명훈님메소드이용
			Info memberInfo = new Info();

			memberInfo.setInfoService(new ArrayList<String>());
			memberInfo.setInfoCareer(new ArrayList<String>());
			memberInfo.setInfoDisease(new ArrayList<String>());
			memberInfo.setInfoLicense(new ArrayList<String>());
			memberInfo.setInfoDiseaseLevel(new ArrayList<String>());
			memberInfo.setInfoGender(new ArrayList<String>());
			memberInfo.setInfoNational(new ArrayList<String>());
			memberInfo.setInfoAgeGroup(new ArrayList<String>());

			
			for(HashMap<String,String> m : category) {
				System.out.println(m.get("S_CATEGORY"));
				 switch(m.get("L_CATEGORY")) {
					 case "service" : memberInfo.getInfoService().add(0,m.get("S_CATEGORY")); break;
					 //case "serviceCareer" : memberInfo.getInfoServiceCareer().add(0,m.get("S_CATEGORY")); break;
					 case "career" : memberInfo.getInfoCareer().add(0,m.get("S_CATEGORY")); break;
					 case "disease" : memberInfo.getInfoDisease().add(0,m.get("S_CATEGORY")); break;
					 case "license" : memberInfo.getInfoLicense().add(0,m.get("S_CATEGORY")); break;
					 case "diseaseLevel" : memberInfo.getInfoDiseaseLevel().add(0,m.get("S_CATEGORY")); break;
					 case "gender" : memberInfo.getInfoGender().add(0,m.get("S_CATEGORY")); break;
					 case "national" : memberInfo.getInfoNational().add(0,m.get("S_CATEGORY")); break;
					 case "ageGroup" : memberInfo.getInfoAgeGroup().add(0,m.get("S_CATEGORY")); break;
				 }
			};
		


			for (HashMap<String, String> m : category) {
				switch (m.get("L_CATEGORY")) {
				case "service":
					memberInfo.getInfoService().add(0, m.get("S_CATEGORY"));
					break;
				case "career":
					memberInfo.getInfoCareer().add(0, m.get("S_CATEGORY"));
					break;
				case "disease":
					memberInfo.getInfoDisease().add(0, m.get("S_CATEGORY"));
					break;
				case "license":
					memberInfo.getInfoLicense().add(0, m.get("S_CATEGORY"));
					break;
				case "diseaseLevel":
					memberInfo.getInfoDiseaseLevel().add(0, m.get("S_CATEGORY"));
					break;
				case "gender":
					memberInfo.getInfoGender().add(0, m.get("S_CATEGORY"));
					break;
				case "national":
					memberInfo.getInfoNational().add(0, m.get("S_CATEGORY"));
					break;
				case "ageGroup":
					memberInfo.getInfoAgeGroup().add(0, m.get("S_CATEGORY"));
					break;
				}
			}
			;


			return memberInfo;

		} else {
			ArrayList<HashMap<String, String>> category = mService.selectWantInfo(memberNo); // 명훈님메소드 wantInfo 재가공
			Info wantInfo = new Info();

			wantInfo.setInfoService(new ArrayList<String>());
			wantInfo.setInfoCareer(new ArrayList<String>());
			wantInfo.setInfoDisease(new ArrayList<String>());
			wantInfo.setInfoLicense(new ArrayList<String>());
			wantInfo.setInfoDiseaseLevel(new ArrayList<String>());
			wantInfo.setInfoGender(new ArrayList<String>());
			wantInfo.setInfoNational(new ArrayList<String>());
			wantInfo.setInfoAgeGroup(new ArrayList<String>());

			
			for(HashMap<String,String> m : category) {
				 switch(m.get("L_CATEGORY")) {
					 case "service" : wantInfo.getInfoService().add(0,m.get("S_CATEGORY")); break;
					 case "serviceCareer" : wantInfo.getInfoServiceCareer().add(0,m.get("S_CATEGORY")); break;
					 case "career" : wantInfo.getInfoCareer().add(0,m.get("S_CATEGORY")); break;
					 case "disease" : wantInfo.getInfoDisease().add(0,m.get("S_CATEGORY")); break;
					 case "license" : wantInfo.getInfoLicense().add(0,m.get("S_CATEGORY")); break;
					 case "diseaseLevel" : wantInfo.getInfoDiseaseLevel().add(0,m.get("S_CATEGORY")); break;
					 case "gender" : wantInfo.getInfoGender().add(0,m.get("S_CATEGORY")); break;
					 case "national" : wantInfo.getInfoNational().add(0,m.get("S_CATEGORY")); break;
					 case "ageGroup" : wantInfo.getInfoAgeGroup().add(0,m.get("S_CATEGORY")); break;
				 }
			};
			


			for (HashMap<String, String> m : category) {
				switch (m.get("L_CATEGORY")) {
				case "service":
					wantInfo.getInfoService().add(0, m.get("S_CATEGORY"));
					break;
				case "career":
					wantInfo.getInfoCareer().add(0, m.get("S_CATEGORY"));
					break;
				case "disease":
					wantInfo.getInfoDisease().add(0, m.get("S_CATEGORY"));
					break;
				case "license":
					wantInfo.getInfoLicense().add(0, m.get("S_CATEGORY"));
					break;
				case "diseaseLevel":
					wantInfo.getInfoDiseaseLevel().add(0, m.get("S_CATEGORY"));
					break;
				case "gender":
					wantInfo.getInfoGender().add(0, m.get("S_CATEGORY"));
					break;
				case "national":
					wantInfo.getInfoNational().add(0, m.get("S_CATEGORY"));
					break;
				case "ageGroup":
					wantInfo.getInfoAgeGroup().add(0, m.get("S_CATEGORY"));
					break;
				}
			}
			;


			return wantInfo;
		}
	}

	@PostMapping("login.me")
	public String login(@ModelAttribute Member m, Model model, RedirectAttributes ra) {
		Member loginUser = mService.login(m);

		if (bCrypt.matches(m.getMemberPwd(), loginUser.getMemberPwd())) {
			model.addAttribute("loginUser", loginUser);

			if (loginUser.getMemberCategory().equalsIgnoreCase("C")) {
				ra.addAttribute("memberNo", loginUser.getMemberNo());

				return "redirect:caregiverMain.me";
			} else if(loginUser.getMemberCategory().equalsIgnoreCase("P")) {
				
				
				return "redirect:patientMain.me";
			}

			return "redirect:patientMain.me";

		} else {
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
//			조회할 항목
//				필수입력 : 원하는 서비스, 공동간병 참여여부, 경력, 적정비용, 성별, 나이, 주소
//				선택입력 : 서비스 경험, 돌봄경험, 자격증
//			항목의 출처
//				CAREGIVER : 공동간병 희망여부(필수,CARE_JOIN_STATUS), 최소비용(필수, MIN_MONEY), 
//				MEMBER_INFO : 경력기간(필수,1개), 서비스경험(선택, 0~3개), 돌봄경험(선택, 0~10개), 자격증(선택, 0~3개), 원하는 서비스(필수, 1~3개)
//				MEMBER: 성별(필수, MEMBER_GENDER), 나이(필수, MEMBER_AGE), 주소(필수, MEMBER_ADDRESS), 국적(필수, MEMBER_NATIONAL)
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
			// HashMap<String, String> infoMap
			if(Integer.parseInt(String.valueOf(infoMap.get("나이"))) < 0) {
				infoMap.put("나이", (Integer.parseInt(infoMap.get("나이"))+100)+"");
			}
			
			// 가공 종료! => infoMap
			// {국적=내국인, 자격증=간병사/요양보호사, 서비스경험=병원돌봄/가정돌봄/동행서비스, 주소=경기 성남시, CARE_JOIN_STATUS=Y, 돌봄질환경험=3/섬망/기저귀 케어, 나이=69, 성별=남성, 경력=3년미만, 최소금액=50000}
			
			// 3. 환자 목록 조회
			ArrayList<HashMap<String, Object>> promptPatientList = new ArrayList<HashMap<String, Object>>(); // 프롬프트에 전달할 최종 후보군 리스트
			String joinStatus = infoMap.remove("CARE_JOIN_STATUS"); // 공동 간병 참여여부
			String careGiverAddress = infoMap.get("주소").contains("서울") ? "서울" : (infoMap.get("주소").contains("제주") ? "제주" : (infoMap.get("주소").contains("세종") ? "세종" : infoMap.get("주소")));
			// 환자 기본정보 조회 : 회원번호, 이름, 성별, 나이, 간병장소, 국적, 키, 몸무게, 요청서비스, 요청사항, 요청장소, 매칭 시작일, 매칭 종료일, 금액
//			PATIENT : 멤버번호(PT_NO), 이름(PT_NAME), 성별(PT_GENDER), 나이(PT_GENDER), 국적, 키(PT_HEIGHT), 몸무게(PT_WEIGHT)
//			MATCHING: 매칭번호(MAT_NO), 시작날짜(BEGIN_DT), 종료날짜(END_DT), 지불가능한 금액(MONEY)  
//			MAT_PT_INFO: 신청서비스(SERVICE), 매칭장소(MAT_ADDRESS_INFO), 매칭종류(MAT_MODE)
			
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
			System.out.println(mNoList);
			ArrayList<HashMap<String, Object>> pExpList = mService.getPatientInfo(mNoList);
//			[{S_CATEGORY=병원돌봄, L_CATEGORY=service, MEMBER_NO=55}, {S_CATEGORY=병원돌봄, L_CATEGORY=service, MEMBER_NO=15}, {S_CATEGORY=병원돌봄, L_CATEGORY=service, MEMBER_NO=54}, {S_CATEGORY=가정돌봄, L_CATEGORY=service, MEMBER_NO=15}, {S_CATEGORY=치매, L_CATEGORY=disease, MEMBER_NO=15}, {S_CATEGORY=치매, L_CATEGORY=disease, MEMBER_NO=54}, {S_CATEGORY=욕창, L_CATEGORY=disease, MEMBER_NO=54}, {S_CATEGORY=하반신 마비, L_CATEGORY=disease, MEMBER_NO=15}, {S_CATEGORY=와상 환자, L_CATEGORY=disease, MEMBER_NO=15}, {S_CATEGORY=기저귀 케어, L_CATEGORY=disease, MEMBER_NO=15}, {S_CATEGORY=기저귀 케어, L_CATEGORY=disease, MEMBER_NO=55}, {S_CATEGORY=기저귀 케어, L_CATEGORY=disease, MEMBER_NO=54}, {S_CATEGORY=의식 없음, L_CATEGORY=disease, MEMBER_NO=54}, {S_CATEGORY=중증, L_CATEGORY=diseaseLevel, MEMBER_NO=55}, {S_CATEGORY=중증, L_CATEGORY=diseaseLevel, MEMBER_NO=54}, {S_CATEGORY=경증, L_CATEGORY=diseaseLevel, MEMBER_NO=15}]
			System.out.println("체크하기두번째");
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
		
			System.out.println("프롬프트 작성까지");
		// 5. 프롬프트 작성
		String prompt = "간병인 정보는" + infoMap.toString() + "이고" + "환자 목록은" + promptPatientList.toString() + "이다."
						+ "간병인의 정보를 바탕으로 가장 적절한 매칭번호 " + selectNum + "개만 숫자로만 짧게 대답해줘.";
		
		// 6. 프롬프트를 전달하고 결과값 받아오기
		String result = botController.chat(prompt); // "2, 4, 8, 10, 14"
		System.out.println("GPT가 추천한 매칭번호 : " + result);
		String[] choice = result.split(", ");
		System.out.println("GPT 추천번호의 split" + Arrays.toString(choice));
		ArrayList<Integer> choiceNoList = new ArrayList<Integer>();
		for(int i = 0; i < choice.length; i++) {
			if(choice[i].contains(".")) {
				System.out.println("에러의 원인일 수 있는 부분 : " + choice[i]);
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
	
	// 메인페이지 이동 후에 환자 메인페이지 렌더링 도중에 캘린더 이벤트를 조회하게 된다.
	@GetMapping(value="caregiverCalendarEvent.me", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String caregiverCalendarEvent(Model model, HttpServletResponse response) {
		Member loginUser = (Member)model.getAttribute("loginUser");
		
		// 일정 조회 
		ArrayList<CalendarEvent> eList = new ArrayList<CalendarEvent>();
		if(loginUser != null) {
			eList = mService.caregiverCalendarEvent(loginUser.getMemberNo());
			
		}
		
		JSONArray array = new JSONArray();
		
		for(CalendarEvent c : eList) {
			int matNo = c.getMatNo();
			int money = c.getMoney();
			String beginTime = c.getBeginTime();
			String endTime = c.getEndTime();
			String matAddressInfo = c.getMatAddressInfo();
			Date beginDate = c.getBeginDt();
			Date endDate = c.getEndDt();
			
			if(c.getPtCount() == 1) {
				if(c.getMatMode() == 1) {
					JSONObject obj = new JSONObject();
					obj.put("title", "개인 기간제 간병");
					obj.put("start", c.getBeginDt());
					obj.put("end", c.getEndDt());
					obj.put("matNo", matNo);
					obj.put("money", money);
					obj.put("matAddressInfo", matAddressInfo);
					obj.put("beginDate", beginDate);
					obj.put("endDate", endDate);
					array.put(obj);
				} else {
					String[] strArr = c.getMatDate().split(",");
					System.out.println(Arrays.toString(strArr));
					for(int i = 0; i < strArr.length; i++) {
						JSONObject obj = new JSONObject();
						obj.put("title", "개인 시간제 간병");
						obj.put("start", strArr[i]);
						obj.put("end", strArr[i]);
						obj.put("matNo", matNo);
						obj.put("money", money);
						obj.put("matAddressInfo", matAddressInfo);
						obj.put("beginDate", beginDate);
						obj.put("endDate", endDate);
						array.put(obj);
					}
				}
			} else {
				if(c.getMatMode() == 1) {
					JSONObject obj = new JSONObject();
					obj.put("title", "공동 기간제 간병");
					obj.put("start", c.getBeginDt());
					obj.put("end", c.getEndDt());
					obj.put("matNo", matNo);
					obj.put("money", money);
					obj.put("matAddressInfo", matAddressInfo);
					obj.put("beginDate", beginDate);
					obj.put("endDate", endDate);
					array.put(obj);
				} else {
					String[] strArr = c.getMatDate().split(",");
					for(int i = 0; i < strArr.length; i++) {
						JSONObject obj = new JSONObject();
						obj.put("title", "공동 시간제 간병");
						obj.put("start", strArr[i]);
						obj.put("end", strArr[i]);
						obj.put("matNo", matNo);
						obj.put("money", money);
						obj.put("matAddressInfo", matAddressInfo);
						obj.put("beginDate", beginDate);
						obj.put("endDate", endDate);
						array.put(obj);
					}
				}
			}
			
		}
		return array.toString();
	}
	
	
	
	// 간병인 메인페이지로 가기 
	@GetMapping("caregiverMain.me")
	public String caregiverMain(HttpSession session, Model model,
								@RequestParam(value="matPtCount", defaultValue = "0") int matPtCount, 
								@RequestParam(value="matPtName", required = false) String matPtName,
								 @RequestParam(value="result", required = false) String result) {
		
		Member loginUser = (Member)session.getAttribute("loginUser");
		model.addAttribute("loginUserName", loginUser.getMemberName());
		
		// 1. 자동 추천 목록 받아오기
		int memberNo = 0;
		if(loginUser != null) {
			memberNo = loginUser.getMemberNo();
			System.out.println(memberNo);
			System.out.println("전");
			ArrayList<Patient> completeList = openAiPatientChoice(memberNo, 5); // 추천목록이 없으면 null로 넘어옴
			System.out.println(memberNo);
			System.out.println("후");
			System.out.println(completeList);
			model.addAttribute("completeList", completeList);
		}
				
		
		//남희 - 환자정보 불러오기
		//MatMatptInfoPt 18개 뽑기 
		ArrayList<MatMatptInfoPt> matMatptInfoPtListBefore = mService.getMatMatptInfoPt();		
		ArrayList<MatMatptInfoPt> matMatptInfoPtList1 = new ArrayList<MatMatptInfoPt>();
		ArrayList<MatMatptInfoPt> matMatptInfoPtList2 = new ArrayList<MatMatptInfoPt>();
		ArrayList<MatMatptInfoPt> matMatptInfoPtList3 = new ArrayList<MatMatptInfoPt>();
		
		for(int i = 0; i < matMatptInfoPtListBefore.size(); i++) {
			//나이 계산
			int ptRealAge = AgeCalculator.calculateAge(matMatptInfoPtListBefore.get(i).getPtAge());
			matMatptInfoPtListBefore.get(i).setPtRealAge(ptRealAge);
			
			//노출 주소
			String[] addr = matMatptInfoPtListBefore.get(i).getMatAddressInfo().split("//");			
			String[] addressMin = addr[1].split(" ");
			String addressMinStr = addressMin[0] + " " + addressMin[1]; //00도 00시//
			matMatptInfoPtListBefore.get(i).setMatAddressMin(addressMinStr);
			
			if(matMatptInfoPtListBefore.get(i).getPtCount() > 1) {
				if(matMatptInfoPtListBefore.get(i).getGroupLeader().equals("N")) {
					matMatptInfoPtListBefore.remove(i);
				}
			}
			
			if(i < 6) {
				matMatptInfoPtList1.add(matMatptInfoPtListBefore.get(i));
			}else if(i < 12) {
				matMatptInfoPtList2.add(matMatptInfoPtListBefore.get(i));
			}else if(i < 18) {
				matMatptInfoPtList3.add(matMatptInfoPtListBefore.get(i));
			}			
					
		}
		
		if(matPtCount > 0  && matPtName != null) {
			model.addAttribute("matPtCount", matPtCount);
			model.addAttribute("matPtName", matPtName);
			model.addAttribute("result", result);
			
		}
		
		System.out.println(matMatptInfoPtList1);
		model.addAttribute("matMatptInfoPtList1", matMatptInfoPtList1);
		model.addAttribute("matMatptInfoPtList2", matMatptInfoPtList2);
		model.addAttribute("matMatptInfoPtList3", matMatptInfoPtList3);
		
		//loginUser(간병인)에게 매칭을 신청한 대상 정보 불러오기
		ArrayList<RequestMatPt> requestMatPt = mService.getRequestMatPt(loginUser.getMemberNo());
		System.out.println(requestMatPt);
		
		for(int i = 0 ; i < requestMatPt.size(); i ++) {
			int realAge = AgeCalculator.calculateAge(requestMatPt.get(i).getPtAge());
			requestMatPt.get(i).setPtRealAge(realAge);
			
			if((Integer)requestMatPt.get(i).getPtCount()> 1) {
				if(requestMatPt.get(i).getGroupLeader().equals("N")) {
					requestMatPt.remove(i);
				}
			}
			if(i > 10) {
				requestMatPt.remove(i);
			}
		}
		model.addAttribute("requestMatPt", requestMatPt);
		
		//현재 매칭중인 pt정보
		//ArrayList<MatMatptInfoPt> matConfirmPt = mService.getMatConfirmPt();
		
	
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
	
		
	//종규 : 결제에 쓸 매칭 데이터 삽입하기.여러개있을수있으니 리스트로 진행하기 --up--
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
			
		//종규 : 결제에 쓸 매칭 데이터 삽입하기.여러개있을수있으니 리스트로 진행하기 --down--
		//Info memberInfo = categoryFunction(loginUser.getMemberNo(), true); // 간병인 멤퍼인포정보
		ArrayList<CareGiver> cg = mService.selectCareGiverList(); // 간병인 정보
		model.addAttribute("cg",cg);
		ArrayList<MatMatptInfo> mc = mService.selectMatList(loginUser.getMemberNo());
		model.addAttribute("mc",mc);
		System.out.println(mc);
		for(CareGiver c : cg) {
			LocalDate birthDateParsed = c.getMemberAge().toLocalDate();
			LocalDate today = LocalDate.now();
			
			c.setAge(Period.between(birthDateParsed, today).getYears());
			//System.out.println(c);
		}
		
		return "patientMain";
	}

	// 회원가입 페이지 이동
	@GetMapping("enroll1View.me")
	public String enroll1View(HttpSession session) {

		// 멤버 테이블만 있고 환자/ 간병인 테이블에 insert됮 않은 경우 멤버 테이블 삭제 -> 회원가입 도충 탈출 등
		String memberCategory = (String) session.getAttribute("tempMemberCategory");

		String table = "";
		if (memberCategory.equals("C")) {
			table = "caregiver";
		} else {
			table = "patient";
		}

		int resultNoInfo = mService.noInfomemberdle();
		System.out.println(resultNoInfo);

		return "enroll1";
	}

	// 아이디 중복체크
	@ResponseBody
	@PostMapping("idCheck.me")
	public String idCheck(@RequestParam("id") String id) {
		int result = mService.idCheck(id);
		if (result == 0) {
			return "usable";
		} else {
			return "unusable";
		}

	}

	// 닉네임 중복 체크
	@ResponseBody
	@PostMapping("nickNameCheck.me")
	public String nickNameCheck(@RequestParam("nickName") String nickName) {
		int result = mService.nickNameCheck(nickName);
		if (result == 0) {
			return "usable";
		} else {
			return "unusable";
		}

	}

	// 회원가입
	@PostMapping("enroll.me")
	public String enroll(@ModelAttribute Member m, @RequestParam("postcode") String postcode,
			@RequestParam("roadAddress") String roadAddress, @RequestParam("detailAddress") String detailAddress,
			@RequestParam("email") String email, @RequestParam("emailDomain") String emailDomain, HttpSession session,
			Model model) {

		// 간병인/환자 택
		String memberCategory = (String) session.getAttribute("tempMemberCategory");
		m.setMemberCategory(memberCategory);

		// 대문 등록 카테고리 session삭제
		session.removeAttribute("tempMemberCategory");

		String memberPwd = bCrypt.encode(m.getMemberPwd().toLowerCase());
		m.setMemberPwd(memberPwd);

		String memberAddress = postcode + "//" + roadAddress + "//" + detailAddress;
		m.setMemberAddress(memberAddress);

		String memberEmail = email + "@" + emailDomain;
		m.setMemberEmail(memberEmail);

		System.out.println("회원가입 검증=" + m);
		//소셜회원가입하나추가
		String code = (String) session.getAttribute("code");
		m.setMemberPay(code);
		session.removeAttribute("code");
		int result = mService.enroll(m);

		// 회원가입용 session데이터
		model.addAttribute("enrollmember", m);
		System.out.println("회원가입 데이터 전송 검증 =" + m);
		System.out.println("성공여부" + result);

		if (result > 0) {
			if (m.getMemberCategory().equals("C")) {
				return "enroll2";
			} else {
				return "enroll3";
			}
		} else {
			throw new MemberException("회원가입에 실패했습니다.");
			
		}

	}

	@GetMapping("myInfoMatching.me")
	public String myInfoMatching() { // 마이페이지 현재매칭정보 확인용
		return "myInfoMatching";
	}
	
	// 매칭이력
	@GetMapping("myInfoMatchingHistory.me")
	public String myInfoMatchingHistory(HttpSession session,Model model) {		//마이페이지 매칭 이력 확인용
		LocalDate currentDate = LocalDate.now();
		String currentMonth = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
		
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		// 회원번호
		int memberNo = loginUser.getMemberNo();
		
		if(loginUser != null) {
				char check = loginUser.getMemberCategory().charAt(0);
				switch(check) {
				
				case 'C':
					ArrayList<CareReview> monthScoreList = mService.monthScoreList(memberNo);
					System.out.println("월간"+monthScoreList);
		
					CareReview monthScore = null;
					for (CareReview score : monthScoreList) {
						if(score.getMonth().equals(currentMonth)) {
							monthScore = score;
							break;
						}
					}
					System.out.println(monthScore);
					if (monthScore != null) {
						model.addAttribute("month",monthScore.getMonth());
			            model.addAttribute("sumScore", monthScore.getSumScore());
			            model.addAttribute("avgScore", monthScore.getAvgScore());
			        } else {
			            model.addAttribute("message", "이달의 데이터가 없습니다.");
			        }
					 return "myInfoMatchingHistory";
				case 'P':
					// 환자번호
					int ptNo = mService.getPtNo(memberNo);
					ArrayList<MatMatptInfo> mciList = mService.selectMatList(ptNo);	//환자측 매칭방번호 리스트.ptNo가들어가서무조건
					System.out.println("매칭이력"+mciList);
					System.out.println("=======================");
					ArrayList<CareReview> reviewList = mService.reviewList(ptNo);
					int reviewYn = 0;
					for(MatMatptInfo i : mciList) {
						System.out.println(i);
						i.setBeforeDate(currentDate.isBefore(i.getBeginDt().toLocalDate()));
						i.setAfterDate(currentDate.isAfter(i.getEndDt().toLocalDate()));
						//reviewYn= mService.selectReviewYn(i.getMatNo(), ptNo);
					}
					ArrayList<MatMatptInfoPt> monthPatient = mService.useMonth(ptNo);
					
					if(monthPatient != null) {
						model.addAttribute("monthPatient",monthPatient);
					}
					model.addAttribute("reviewList",reviewList);
					model.addAttribute("mciList",mciList);
					model.addAttribute("today", LocalDate.now());
					return "myInfoMatchingHistoryP";
				case 'A': return null;
			}
		}

		throw new MemberException("로그인없음. 인터셉터설정");
	}

	// 내가 쓴 후기
	@GetMapping("myInfoMatchingReview.me")
	public String myInfoMatchingReview(HttpSession session, Model model) { // 마이페이지 매칭 이력 확인용

		Member loginUser = (Member) session.getAttribute("loginUser");
		int memberNo = loginUser.getMemberNo();
		
		
		if (loginUser != null) {
			char check = loginUser.getMemberCategory().charAt(0);
			switch (check) {
			case 'C':
				ArrayList<CareReview> caregiverList = mService.caregiverReviewList(memberNo);
				ArrayList<CareReview> sumAvgScore = mService.sumAvgScore(memberNo);
				System.out.println("합계평균"+sumAvgScore);
				
				double avgScore = sumAvgScore.get(0).getAvgScore();
				int sumScore = sumAvgScore.get(0).getSumScore();
				int countScore = sumAvgScore.get(0).getCountReview();
				
				model.addAttribute("cList", caregiverList);
				model.addAttribute("avgScore", avgScore);
				model.addAttribute("sumScore", sumScore);
				model.addAttribute("countScore", countScore);
				return "myInfoMatchingReview";
			case 'P':
				// 회원번호로 환자번호 get
				int ptNo = mService.getPtNo(memberNo);
				
				// 환자가 작성한 후기글
				ArrayList<CareReview> list = mService.reviewList(ptNo);

				HashMap<Integer, Object> reviewList = new HashMap<Integer, Object>();
				
				
				for(CareReview reviewsInfo:list) {
					// 리뷰번호로 간병인별로 작성한 리뷰 조회
		                ArrayList<CareReview> selectReviewList = mService.selectReviewList(reviewsInfo.getReviewNo());
		                reviewList.put(reviewsInfo.getReviewNo(), selectReviewList);
		                
		            }
				System.out.println("lll"+reviewList);
				
				model.addAttribute("list", list);
				
				
				return "myInfoMatchingReviewP";
			case 'A':
				return null;
			}
		}
		throw new MemberException("로그인없음. 인터셉터설정");
	}
	
	// 내 작성글 보기
	   @GetMapping("myInfoBoardList.me")
	   public String myInfoBoardList(@RequestParam(value="page", defaultValue = "1") int currentPage, Model model, HttpSession session) {      //마이페이지 보드작성 확인용
	      
	       
	      Member loginUser = (Member)session.getAttribute("loginUser");
	      int mNo = loginUser.getMemberNo();

//	      // 게시글페이지네이션
	      int boardListCount = mService.getBoardListCount(mNo); 
	       PageInfo boardPi = Pagination.getPageInfo(currentPage, boardListCount, 5);
	       
//	      // 내 작성글 정보
	      ArrayList<Board> boardList = mService.mySelectBoardList(boardPi, mNo);
	      
//	      // 내 작성글 좋아요
	      HashMap<Integer, Integer> boardLikeCounts = new HashMap<>();
	      for (Board board : boardList) {
	         int boardLikeCount = mService.boardLikeCount(board.getBoardNo());
	         boardLikeCounts.put(board.getBoardNo(), boardLikeCount);
	      }
	      
//	      // 댓글 페이지네이션
	      int replyListCount = mService.getReplyListCount(mNo);
	      PageInfo replyPi = Pagination.getPageInfo(currentPage, replyListCount, 5);
	      
//	      // 내 댓글 정보
	      ArrayList<Reply> replyList = mService.mySelectReplyList(replyPi, mNo);
	      
//	      // 내 댓글 좋아요
	      HashMap<Integer, Integer> replyLikeCounts = new HashMap<>();
	      for(Reply reply : replyList) {
	         int replyLikeCount = mService.replyLikeCount(reply.getReplyNo());
	         replyLikeCounts.put(reply.getReplyNo(), replyLikeCount);
	      }
//	      // 좋아요 페이지네이션
	      int likeListCount = mService.getLikeListCount(mNo);
	      PageInfo likePi = Pagination.getPageInfo(currentPage, likeListCount, 5);
//
//	      // 좋아요한 글 목록
	      ArrayList<Board> likeList = mService.mySelectLikeList(likePi, mNo);
	      
	      // 좋아요한 글 좋아요
	      HashMap<Integer, Integer> likeLikeCounts = new HashMap<>();
	      for (Board board : likeList) {
	         int likeLikeCount = mService.likeLikeCount(board.getBoardNo());
	         likeLikeCounts.put(board.getBoardNo(), likeLikeCount);
	      }
	      
	      
	      model.addAttribute("boardPi", boardPi);
	      model.addAttribute("boardList", boardList);
	      model.addAttribute("boardLikeCounts", boardLikeCounts);
	      model.addAttribute("replyPi", replyPi);
	      model.addAttribute("replyList", replyList);
	      model.addAttribute("replyLikeCounts", replyLikeCounts);
	      model.addAttribute("likePi", likePi);
	      model.addAttribute("likeList", likeList);
	      model.addAttribute("likeLikeCounts", likeLikeCounts);
	      return "myInfoBoardList";
	   }
	
	
	
	//간병인 회원가입(간병인 정보 입력)
	@PostMapping("enrollCaregiver.me")
	public String enrollCaregiver(@ModelAttribute CareGiver cg, HttpSession session) {
		System.out.println("데이터 확인" + cg);

		// 간병인 memberNo 세팅
		cg.setMemberNo(((Member) session.getAttribute("enrollmember")).getMemberNo());

		System.out.println("간병인 정보=" + cg);

		int result1 = mService.enrollCareGiver(cg);
		System.out.println("result1" + result1);

		int result2 = mService.enrollInfoCategory(cg);
		System.out.println("result2" + result2);

		if (result1 > 0 || result2 > 0) {
			session.removeAttribute("enrollmember");
			return "enroll4";
		} else {
			throw new MemberException("회원가입에 실패했습니다.");
		}
	}

	// 환자 회원가입
	@PostMapping("enrollPatient.me")
	public String enrollPatient(@ModelAttribute Patient pt, @RequestParam("postcode") String postcode,
			@RequestParam("roadAddress") String roadAddress, @RequestParam("detailAddress") String detailAddress,
			HttpSession session) {

		// 간병인 memberNo 세팅
		pt.setMemberNo(((Member) session.getAttribute("enrollmember")).getMemberNo());

		// 돌봄 주소 세팅
		String ptAddress = postcode + "//" + roadAddress + "//" + detailAddress;
		pt.setPtAddress(ptAddress);

		System.out.println("간병인 정보=" + pt);

		int result1 = mService.enrollPatient(pt);
		System.out.println("result1" + result1);

		int result2 = mService.enrollInfoCategory(pt);
		System.out.println("result2" + result2);

		if (result1 > 0 || result2 > 0) {
			session.removeAttribute("enrollmember");
			return "enroll4";
		} else {

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
	public String findIdResult(@RequestParam("memberId") String memberId,
			@RequestParam("memberPhone") String memberPhone, Model model) {

		Member member = new Member();
		member.setMemberId(memberId);
		member.setMemberPhone(memberPhone);
		System.out.println(member);

		Member findMember = mService.findIdResult(member);
		System.out.println(findMember);
		if (findMember != null) {
			model.addAttribute("findMember", findMember);
			return "findIdResult";
		} else {
			throw new MemberException("해당 로그인정보로 가입된 아이디를 찾을 수 없습니다.");
		}

	}

	@PostMapping("/api/verify-member") // 입력한 아이디로 등록된 핸드폰번호 있는지 확인
	@ResponseBody
	public Map<String, Object> verifyMember(@RequestBody Member member) {
		Map<String, Object> response = new HashMap<>();

		Member findMember = mService.findIdResult(member);
		response.put("success", findMember != null);

		return response;
	}

	// 임시 : 메인페이지 이동시 캘린더 이벤트 조회	
	/*public void calendarEvent(Model model, HttpServletResponse response) {
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
	}*/

	// 메인페이지 이동 후에 환자 메인페이지 렌더링 도중에 캘린더 이벤트를 조회하게 된다.


	// 자동추천을 비동기 통신으로 요청
	@GetMapping("refreshChoice.me")
	@ResponseBody
	public void refreshChoice(HttpServletResponse response) {
		// 1. 로그인한 회원 정보를 조회한다.
		HashMap<String, String> cMap = new HashMap<String, String>();
		// 서비스경험=병원돌봄/경력=2년/환자유형=치매,재활/자격증=요양보호사/환자중증도=경증/거주지=서울시종로구/지불받기를바라는최소간병비용=60,000원이상
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
		for (int i = 0; i < 5; i++) {
			list.add(Integer.parseInt(result.split(", ")[i]));
		} // [2, 5, 8, 10, 14]

		// 5. 추출된 회원번호를 통해 회원 정보를 조회해온다. => List<Member>의 형식일 것이다.
		ArrayList<Member> resultList = new ArrayList<Member>(); // 회원정보를 받아왔다고 가정
		Gson gson = new Gson();
		response.setContentType("application/json; charset=UTF-8;");
		try {
			gson.toJson(resultList, response.getWriter());
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}


	@PostMapping("/api/send-auth-code") // 인증번호 전송
	@ResponseBody
	public Map<String, Object> sendAuthCode(@RequestBody Map<String, String> request, HttpSession session) {
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
	public String findPwdResult(HttpSession session, @RequestParam("memberId") String memberId, Model model) {
		Boolean isVerified = (Boolean) session.getAttribute("isVerified");
		if (isVerified != null && isVerified) {
			// 인증된 경우 비밀번호 재설정 페이지로 이동
			model.addAttribute("memberId", memberId); // 비밀번호 재설정을 위해 아이디 정보 가지고 이동
			return "findPwdResult";
		} else {
			// 인증되지 않은 경우 다시 비밀번호 찾기 페이지로 리다이렉트
			return "redirect:findPwd.me";
		}
	}

	@PostMapping("updatePassword.me")
	public String updatePassword(@RequestParam("memberId") String memberId,
			@RequestParam("memberPwd") String memberPwd) {
		HashMap<String, String> changeInfo = new HashMap<String, String>();
		changeInfo.put("memberId", memberId);
		changeInfo.put("newPwd", bCrypt.encode(memberPwd));
		System.out.println(changeInfo);
		int result = mService.updatePassword(changeInfo);

		if (result > 0) {
			return "redirect:home.do";
		} else {
			throw new MemberException("비밀번호 수정에 실패하였습니다");
		}

	}

	@PostMapping("updateWantInfo.me")
	public String updateWantInfo(@RequestParam("wantInfo") String wantInfo, HttpSession session) {
		System.out.println(wantInfo);

		String[] wis = wantInfo.split(",");

		Member loginUser = (Member) session.getAttribute("loginUser");
		int result1 = mService.deleteWantInfo(loginUser.getMemberNo());

		for (String wi : wis) {
			HashMap<String, Integer> info = new HashMap<String, Integer>();
			info.put("memberNo", loginUser.getMemberNo());
			info.put("categoryNo", Integer.parseInt(wi));

			int result2 = mService.insertWantInfo(info);
			System.out.println(result2);

		}

//		ArrayList<String> wi = wantInfo
//		mService.selectwantInfo(wantInfo);
		return "redirect:myInfo.me";
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
	@PostMapping("workAllInfo.me") // 간병인의 입장에서 매칭정보를 가져오는 것
	@ResponseBody
	public void workAllInfo(HttpServletResponse response, @RequestParam(value="page", defaultValue="1") int currentPage,
								@RequestParam("memberNo") Integer memberNo) {
		// 페이지 첫 로드시 또는 검색조건이 하나도 없이 검색버튼을 눌렀을 때 이곳으로 요청이 들어옴
		// MEMBER_NO(간병인 번호), MEMBER_NAME(간병인 이름), MEMBER_GENDER(간병인 성별), MEMBER_AGE(간병인 나이), MAT_MODE(기간제1, 시간제2), 
		// MAT_ADDRESS_INFO(간병 장소), HOSPITAL_NAME(병원명)
		// MAT_NO(매칭번호), PT_COUNT(공동인지 개인인지 구분), BEGIN_DT(시작일), END_DT(종료일) 
		int listCount = mService.getMatchingListCount(null);
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 8);
		ArrayList<Matching> matList = new ArrayList<Matching>();
		ArrayList<Integer> matNoList = new ArrayList<Integer>();
		ArrayList<MatPtInfo> mpiList = new ArrayList<MatPtInfo>();
		ArrayList<Member> memList = new ArrayList<Member>();
		if(memberNo != null) {
			matList = mService.selectMatchingList(pi, null);
			if(!matList.isEmpty()) {
				for(Matching mat : matList) {
					matNoList.add(Integer.parseInt(String.valueOf(mat.getMatNo()))); // [1, 50, 55, 62, 30, 31, 63]
				}
				mpiList = mService.selectMatchingPTInfoList(matNoList);
				memList = mService.selectMatchingMemberList(matNoList); // [Member(memberNo=48, memberId=null, memberPwd=null, memberName=comp1, memberGender=M, memberNickName=null, memberAge=null, memberPhone=null, memberEmail=null, memberCreateDate=null, memberAddress=null, memberCategory=null, memberStatus=null, memberNational=null, memberPay=null, memberUpdateDate=null, memberRealAge=74, career=null, license=null, matNo=1, groupLeader=N), Member(memberNo=55, memberId=null, memberPwd=null, memberName=나리환자1, memberGender=M, memberNickName=null, memberAge=null, memberPhone=null, memberEmail=null, memberCreateDate=null, memberAddress=null, memberCategory=null, memberStatus=null, memberNational=null, memberPay=null, memberUpdateDate=null, memberRealAge=44, career=null, license=null, matNo=31, groupLeader=Y), Member(memberNo=15, memberId=null, memberPwd=null, memberName=나환자, memberGender=F, memberNickName=null, memberAge=null, memberPhone=null, memberEmail=null, memberCreateDate=null, memberAddress=null, memberCategory=null, memberStatus=null, memberNational=null, memberPay=null, memberUpdateDate=null, memberRealAge=52, career=null, license=null, matNo=50, groupLeader=N), Member(memberNo=15, memberId=null, memberPwd=null, memberName=나환자, memberGender=F, memberNickName=null, memberAge=null, memberPhone=null, memberEmail=null, memberCreateDate=null, memberAddress=null, memberCategory=null, memberStatus=null, memberNational=null, memberPay=null, memberUpdateDate=null, memberRealAge=52, career=null, license=null, matNo=55, groupLeader=Y), Member(memberNo=10, memberId=null, memberPwd=null, memberName=test3, memberGender=F, memberNickName=null, memberAge=null, memberPhone=null, memberEmail=null, memberCreateDate=null, memberAddress=null, memberCategory=null, memberStatus=null, memberNational=null, memberPay=null, memberUpdateDate=null, memberRealAge=0, career=null, license=null, matNo=1, groupLeader=Y), Member(memberNo=10, memberId=null, memberPwd=null, memberName=test3, memberGender=F, memberNickName=null, memberAge=null, memberPhone=null, memberEmail=null, memberCreateDate=null, memberAddress=null, memberCategory=null, memberStatus=null, memberNational=null, memberPay=null, memberUpdateDate=null, memberRealAge=0, career=null, license=null, matNo=30, groupLeader=N), Member(memberNo=10, memberId=null, memberPwd=null, memberName=test3, memberGender=F, memberNickName=null, memberAge=null, memberPhone=null, memberEmail=null, memberCreateDate=null, memberAddress=null, memberCategory=null, memberStatus=null, memberNational=null, memberPay=null, memberUpdateDate=null, memberRealAge=0, career=null, license=null, matNo=50, groupLeader=Y), Member(memberNo=28, memberId=null, memberPwd=null, memberName=기기기, memberGender=M, memberNickName=null, memberAge=null, memberPhone=null, memberEmail=null, memberCreateDate=null, memberAddress=null, memberCategory=null, memberStatus=null, memberNational=null, memberPay=null, memberUpdateDate=null, memberRealAge=40, career=null, license=null, matNo=30, groupLeader=Y), Member(memberNo=28, memberId=null, memberPwd=null, memberName=기기기, memberGender=M, memberNickName=null, memberAge=null, memberPhone=null, memberEmail=null, memberCreateDate=null, memberAddress=null, memberCategory=null, memberStatus=null, memberNational=null, memberPay=null, memberUpdateDate=null, memberRealAge=40, career=null, license=null, matNo=62, groupLeader=Y), Member(memberNo=28, memberId=null, memberPwd=null, memberName=기기기, memberGender=M, memberNickName=null, memberAge=null, memberPhone=null, memberEmail=null, memberCreateDate=null, memberAddress=null, memberCategory=null, memberStatus=null, memberNational=null, memberPay=null, memberUpdateDate=null, memberRealAge=40, career=null, license=null, matNo=63, groupLeader=Y)]
			}
		}
		
		GsonBuilder gb = new GsonBuilder().setDateFormat("YYYY-MM-dd");
		Gson gson = gb.create();
		
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put("matList", matList);
		result.put("mpiList", mpiList);
		result.put("memList", memList);
		
		response.setContentType("application/json; charset=UTF-8");
		try {
			if(!matList.isEmpty()) {
				System.out.println("목록있음");
				gson.toJson(result, response.getWriter());
			} else {
				System.out.println("목록없음");
				gson.toJson("noExist", response.getWriter());
			}
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	// 간병인 일감찾기 페이지에서의 검색 요청을 처리
	@PostMapping("searchPatientList.me")
	@ResponseBody
	public void searchPatientList(@RequestParam("condition") String obj, 
									@RequestParam(value="page", defaultValue="1") int currentPage,
									HttpServletResponse response) {
		// 검색 조건이 하나라도 있는 경우만 이곳으로 들어온다
		System.out.println("검색조건 확인 : " + obj); 
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, String> map =
			               mapper.readValue(obj, new TypeReference<Map<String, String>>(){});
			
			response.setContentType("application/json; charset=UTF-8");
			GsonBuilder gb = new GsonBuilder().setDateFormat("YYYY-MM-dd");
			Gson gson = gb.create();
			HashMap<String, Object> result = new HashMap<String, Object>();
			
			
			// 검색조건과 페이지에 맞게 조회해와야함
			// 서비스 검색조건 가공
			ArrayList<String> serviceList = new ArrayList<String>();
			if(map.get("service").length() > 0) { // 서비스 검색조건이 있는 경우
				String[] serviceArr = map.get("service").split("/");
				for(String s : serviceArr) {
					serviceList.add(s);
				}
			}
			// 공동간병 검색조건을 가공
			ArrayList<String> shareList = new ArrayList<String>();
			if(map.get("share").length() > 0) { // 공동간병에 대한 검색조건이 있는 경우
				if(map.get("share").contains("개인")) shareList.add("1");
				if(map.get("share").contains("공동")) shareList.add("2");
			}
			// 지역 검색조건을 가공
			String area = "";
			if(map.get("area").length()>0) {
				switch(map.get("area")) {
				case "전국" : area = "%"; break;
				case "서울", "부산", "대구", "인천", "광주", "대전", "울산", "제주","세종" : area = map.get("area"); break;
				case "경기도" : area = "경기"; break;
				case "강원도" : area = "강원"; break;
				case "충청북도" : area = "충%북";  break;
				case "충청남도" : area = "충%남"; break;
				case "전라북도" : area = "전%북";  break;
				case "전라남도" : area = "전%남";  break;
				case "경상북도" : area = "경%북"; break;
				case "경상남도" : area = "경%남"; break;
				}
			}
			// 성별 검색조건을 가공
			ArrayList<String> genderList = new ArrayList<String>();
			if(map.get("gender").length()>0) {
				if(map.get("gender").contains("남")) {
					genderList.add("M");
				}
				if(map.get("gender").contains("여")) {
					genderList.add("F");
				}
			}
			// 연령 검색조건을 가공 : 경우의 수가 8가지(선택을 하지 않은 경우 포함)이므로 식별값을 부여
			String age = "";
			if(map.get("age").length() > 0) {
				switch(map.get("age")) {
				case "청년": age = "1"; break;
				case "중년": age = "2"; break;
				case "장년": age = "3"; break;
				case "청년/중년": age = "4"; break;
				case "청년/장년": age = "5"; break;
				case "중년/장년": age = "6"; break;
				case "청년/중년/장년": age = "7"; break;
				}
			}
			// 금액 검색조건을 가공
			String cost = "";
			if(map.get("cost").length() > 0) {
				switch(map.get("cost")) {
				case "~30,000원" : cost = "30000"; break;
				case "~50,000원" : cost = "50000"; break;
				case "~80,000원" : cost = "80000"; break;
				case "~100,000원" : cost = "100000"; break;
				}
			}
			
			// 서비스, 공동간병, 지역, 성별, 연령, 비용에 대하여 검색한 매칭번호를 조회한다.
			// 검색 조건이 없는 경우 mapper에게 List를 전달하지 않을 것
			HashMap<String, Object> searchDefaultMap = new HashMap<String, Object>();
			if(!serviceList.isEmpty()) searchDefaultMap.put("service", serviceList);
			if(!shareList.isEmpty()) searchDefaultMap.put("share", shareList);
			if(area.length() > 0) searchDefaultMap.put("area", area);
			if(!genderList.isEmpty()) searchDefaultMap.put("gender", genderList);
			if(age.length() > 0) searchDefaultMap.put("age", age);
			if(cost.length() > 0) searchDefaultMap.put("cost", cost);
			
			ArrayList<HashMap<String, Integer>> searchDefaultMatNoList = mService.searchDefaultMatNoList(searchDefaultMap);
			// 만약, 검색조건 중 위에서의 검색조건이 없었다면 MAT_CONFIRM = 'N'인 매칭번호들이 조회된다!
			
			// searchDefaultMatNoList 에서 시간제와 기간제를 구분한다.
			ArrayList<Integer> termMatNoList = new ArrayList<Integer>();
			ArrayList<Integer> timeMatNoList = new ArrayList<Integer>();
			for(HashMap<String, Integer> m : searchDefaultMatNoList) {
				if(String.valueOf(m.get("MAT_MODE")).equals("1")) {
					termMatNoList.add(Integer.parseInt(String.valueOf(m.get("MAT_NO"))));
				}
				if(String.valueOf(m.get("MAT_MODE")).equals("2")) {
					timeMatNoList.add(Integer.parseInt(String.valueOf(m.get("MAT_NO"))));
				}
			}
			
			// 기간 검색조건을 가공한다.
			HashMap<String, Object> termMap = new HashMap<String, Object>();
			ArrayList<Integer> searchTermMatNoList = new ArrayList<Integer>();
			ArrayList<Integer> searchTimeMatNoList = new ArrayList<Integer>();
			ArrayList<Integer> tempMatNoList = new ArrayList<Integer>();// 기간제 검색과 시간제 검색한 결과값들을 모을 리스트
			String term = "";
			if(map.get("term").length() > 0) {
				switch(map.get("term")) {
				case "1일 미만" : term = "1"; break;
				case "7일 미만" : term = "7"; break;
				case "15일 미만" : term = "5"; break;
				case "15일 이상" : term = "16"; break;
				}
			}
			
			if(term.length() > 0) {
				if(!termMatNoList.isEmpty()) { // 기간제 검색
					termMap.put("termMatNoList", termMatNoList);
					termMap.put("term", term);
					searchTermMatNoList = mService.searchTermMatNoList(termMap);
					for(Integer i : searchTermMatNoList) {
						tempMatNoList.add(Integer.parseInt(String.valueOf(i)));
					}
				} 
				if(!termMatNoList.isEmpty()) { // 시간제 검색
					termMap.put("timeMatNoList", timeMatNoList);
					termMap.put("term", term);
					searchTimeMatNoList = mService.searchTimeMatNoList(termMap);
					for(Integer i : searchTimeMatNoList) {
						tempMatNoList.add(Integer.parseInt(String.valueOf(i)));
					}
				} 
			} else { // 기간 검색자체를 하지 않은 경우는 MAT_MODE에 따라 분리한 리스트를 다시 하나로 합쳐준다
				for(Integer i : termMatNoList) {
					tempMatNoList.add(i);
				}
				for(Integer i : timeMatNoList) {
					tempMatNoList.add(i);
				}
			}
			
			// 후보 매칭번호에 대한 카테고리 넘버들을 가져온다.
			ArrayList<HashMap<String, Integer>> searchCategoryMatNoList = new ArrayList<HashMap<String, Integer>>();
			if(!tempMatNoList.isEmpty()) {
				searchCategoryMatNoList = mService.searchCategoryMatNoList(tempMatNoList);
			} else {
				gson.toJson("noExist", response.getWriter());
			}
			//[{MAT_NO=1, CATEGORY_NO=1}, {MAT_NO=1, CATEGORY_NO=2}, 
			// {MAT_NO=1, CATEGORY_NO=63}, {MAT_NO=1, CATEGORY_NO=29}, 
			// {MAT_NO=1, CATEGORY_NO=28}, {MAT_NO=1, CATEGORY_NO=24}, {MAT_NO=1, CATEGORY_NO=23}, {MAT_NO=1, CATEGORY_NO=63}, {MAT_NO=1, CATEGORY_NO=62}, {MAT_NO=1, CATEGORY_NO=3}, {MAT_NO=1, CATEGORY_NO=21}, {MAT_NO=1, CATEGORY_NO=22}, {MAT_NO=1, CATEGORY_NO=23}, {MAT_NO=1, CATEGORY_NO=24}, {MAT_NO=1, CATEGORY_NO=25}, {MAT_NO=1, CATEGORY_NO=26}, {MAT_NO=1, CATEGORY_NO=27}, {MAT_NO=1, CATEGORY_NO=28}, {MAT_NO=1, CATEGORY_NO=29}, {MAT_NO=1, CATEGORY_NO=30}, {MAT_NO=1, CATEGORY_NO=61}, {MAT_NO=30, CATEGORY_NO=24}, {MAT_NO=30, CATEGORY_NO=28}, {MAT_NO=30, CATEGORY_NO=23}, {MAT_NO=30, CATEGORY_NO=29}, {MAT_NO=30, CATEGORY_NO=22}, {MAT_NO=30, CATEGORY_NO=61}, {MAT_NO=30, CATEGORY_NO=23}, {MAT_NO=30, CATEGORY_NO=63}, {MAT_NO=46, CATEGORY_NO=41}, {MAT_NO=46, CATEGORY_NO=21}, {MAT_NO=46, CATEGORY_NO=22}, {MAT_NO=46, CATEGORY_NO=23}, {MAT_NO=46, CATEGORY_NO=24}, {MAT_NO=46, CATEGORY_NO=27}, {MAT_NO=46, CATEGORY_NO=28}, {MAT_NO=46, CATEGORY_NO=31}, {MAT_NO=46, CATEGORY_NO=32}, {MAT_NO=46, CATEGORY_NO=63}, {MAT_NO=46, CATEGORY_NO=62}, {MAT_NO=46, CATEGORY_NO=61}, {MAT_NO=46, CATEGORY_NO=40}, {MAT_NO=46, CATEGORY_NO=1}, {MAT_NO=47, CATEGORY_NO=41}, {MAT_NO=47, CATEGORY_NO=21}, {MAT_NO=47, CATEGORY_NO=22}, {MAT_NO=47, CATEGORY_NO=23}, {MAT_NO=47, CATEGORY_NO=24}, {MAT_NO=47, CATEGORY_NO=27}, {MAT_NO=47, CATEGORY_NO=28}, {MAT_NO=47, CATEGORY_NO=31}, {MAT_NO=47, CATEGORY_NO=32}, {MAT_NO=47, CATEGORY_NO=63}, {MAT_NO=47, CATEGORY_NO=62}, {MAT_NO=47, CATEGORY_NO=61}, {MAT_NO=47, CATEGORY_NO=40}, {MAT_NO=47, CATEGORY_NO=1}, {MAT_NO=49, CATEGORY_NO=41}, {MAT_NO=49, CATEGORY_NO=21}, {MAT_NO=49, CATEGORY_NO=22}, {MAT_NO=49, CATEGORY_NO=23}, {MAT_NO=49, CATEGORY_NO=24}, {MAT_NO=49, CATEGORY_NO=27}, {MAT_NO=49, CATEGORY_NO=28}, {MAT_NO=49, CATEGORY_NO=31}, {MAT_NO=49, CATEGORY_NO=32}, {MAT_NO=49, CATEGORY_NO=63}, {MAT_NO=49, CATEGORY_NO=62}, {MAT_NO=49, CATEGORY_NO=61}, {MAT_NO=49, CATEGORY_NO=40}, {MAT_NO=49, CATEGORY_NO=1}, {MAT_NO=52, CATEGORY_NO=41}, {MAT_NO=52, CATEGORY_NO=21}, {MAT_NO=52, CATEGORY_NO=22}, {MAT_NO=52, CATEGORY_NO=23}, {MAT_NO=52, CATEGORY_NO=24}, {MAT_NO=52, CATEGORY_NO=27}, {MAT_NO=52, CATEGORY_NO=28}, {MAT_NO=52, CATEGORY_NO=31}, {MAT_NO=52, CATEGORY_NO=32}, {MAT_NO=52, CATEGORY_NO=63}, {MAT_NO=52, CATEGORY_NO=62}, {MAT_NO=52, CATEGORY_NO=61}, {MAT_NO=52, CATEGORY_NO=40}, {MAT_NO=52, CATEGORY_NO=1}, {MAT_NO=54, CATEGORY_NO=27}, {MAT_NO=54, CATEGORY_NO=26}, {MAT_NO=54, CATEGORY_NO=24}, {MAT_NO=54, CATEGORY_NO=21}, {MAT_NO=54, CATEGORY_NO=63}, {MAT_NO=54, CATEGORY_NO=2}, {MAT_NO=54, CATEGORY_NO=1}, {MAT_NO=55, CATEGORY_NO=27}, {MAT_NO=55, CATEGORY_NO=26}, {MAT_NO=55, CATEGORY_NO=24}, {MAT_NO=55, CATEGORY_NO=21}, {MAT_NO=55, CATEGORY_NO=63}, {MAT_NO=55, CATEGORY_NO=2}, {MAT_NO=55, CATEGORY_NO=1}, {MAT_NO=61, CATEGORY_NO=41}, {MAT_NO=61, CATEGORY_NO=21}, {MAT_NO=61, CATEGORY_NO=22}, {MAT_NO=61, CATEGORY_NO=23}, {MAT_NO=61, CATEGORY_NO=24}, {MAT_NO=61, CATEGORY_NO=27}, {MAT_NO=61, CATEGORY_NO=28}, {MAT_NO=61, CATEGORY_NO=31}, {MAT_NO=61, CATEGORY_NO=32}, {MAT_NO=61, CATEGORY_NO=63}, {MAT_NO=61, CATEGORY_NO=62}, {MAT_NO=61, CATEGORY_NO=61}, {MAT_NO=61, CATEGORY_NO=40}, {MAT_NO=61, CATEGORY_NO=1}, {MAT_NO=64, CATEGORY_NO=41}, {MAT_NO=64, CATEGORY_NO=21}, {MAT_NO=64, CATEGORY_NO=22}, {MAT_NO=64, CATEGORY_NO=23}, {MAT_NO=64, CATEGORY_NO=24}, {MAT_NO=64, CATEGORY_NO=27}, {MAT_NO=64, CATEGORY_NO=28}, {MAT_NO=64, CATEGORY_NO=31}, {MAT_NO=64, CATEGORY_NO=32}, {MAT_NO=64, CATEGORY_NO=63}, {MAT_NO=64, CATEGORY_NO=62}, {MAT_NO=64, CATEGORY_NO=61}, {MAT_NO=64, CATEGORY_NO=40}, {MAT_NO=64, CATEGORY_NO=1}, {MAT_NO=70, CATEGORY_NO=41}, {MAT_NO=70, CATEGORY_NO=21}, {MAT_NO=70, CATEGORY_NO=22}, {MAT_NO=70, CATEGORY_NO=23}, {MAT_NO=70, CATEGORY_NO=24}, {MAT_NO=70, CATEGORY_NO=27}, {MAT_NO=70, CATEGORY_NO=28}, {MAT_NO=70, CATEGORY_NO=31}, {MAT_NO=70, CATEGORY_NO=32}, {MAT_NO=70, CATEGORY_NO=63}, {MAT_NO=70, CATEGORY_NO=62}, {MAT_NO=70, CATEGORY_NO=61}, {MAT_NO=70, CATEGORY_NO=40}, {MAT_NO=70, CATEGORY_NO=1}, {MAT_NO=71, CATEGORY_NO=41}, {MAT_NO=71, CATEGORY_NO=21}, {MAT_NO=71, CATEGORY_NO=22}, {MAT_NO=71, CATEGORY_NO=23}, {MAT_NO=71, CATEGORY_NO=24}, {MAT_NO=71, CATEGORY_NO=27}, {MAT_NO=71, CATEGORY_NO=28}, {MAT_NO=71, CATEGORY_NO=31}, {MAT_NO=71, CATEGORY_NO=32}, {MAT_NO=71, CATEGORY_NO=63}, {MAT_NO=71, CATEGORY_NO=62}, {MAT_NO=71, CATEGORY_NO=61}, {MAT_NO=71, CATEGORY_NO=40}, {MAT_NO=71, CATEGORY_NO=1}, {MAT_NO=72, CATEGORY_NO=23}, {MAT_NO=72, CATEGORY_NO=24}, {MAT_NO=72, CATEGORY_NO=28}, {MAT_NO=72, CATEGORY_NO=63}, {MAT_NO=72, CATEGORY_NO=62}, {MAT_NO=72, CATEGORY_NO=40}, {MAT_NO=72, CATEGORY_NO=1}, {MAT_NO=72, CATEGORY_NO=2}, {MAT_NO=72, CATEGORY_NO=3}, {MAT_NO=73, CATEGORY_NO=23}, {MAT_NO=73, CATEGORY_NO=24}, {MAT_NO=73, CATEGORY_NO=28}, {MAT_NO=73, CATEGORY_NO=63}, {MAT_NO=73, CATEGORY_NO=62}, {MAT_NO=73, CATEGORY_NO=40}, {MAT_NO=73, CATEGORY_NO=1}, {MAT_NO=73, CATEGORY_NO=2}, {MAT_NO=73, CATEGORY_NO=3}]
			
			// 질병과 중증도, 거동 검색 조건을 가져온다
			String disease = map.get("disease"); // 질병에 대한 검색조건
			ArrayList<Integer> categoryNoList = new ArrayList<Integer>(); // 질병에 대한 조건을 필요한 카테고리 넘버로 변환하여 저장
			if(disease.length() > 0) {
				if(disease.contains("치매")) categoryNoList.add(21);
				if(disease.contains("섬망")) categoryNoList.add(22);
				if(disease.contains("욕창")) categoryNoList.add(23);
				if(disease.contains("하반신 마비")) categoryNoList.add(24);
				if(disease.contains("전신 마비")) categoryNoList.add(25);
				if(disease.contains("와상환자")) categoryNoList.add(26);
				if(disease.contains("기저귀 케어")) categoryNoList.add(27);
				if(disease.contains("의식 없음")) categoryNoList.add(28);
				if(disease.contains("석션")) categoryNoList.add(29);
				if(disease.contains("피딩")) categoryNoList.add(30);
				if(disease.contains("소변줄")) categoryNoList.add(31);
				if(disease.contains("장루")) categoryNoList.add(32);
				if(disease.contains("투석")) categoryNoList.add(33);
				if(disease.contains("전염성 질환")) categoryNoList.add(34);
				if(disease.contains("파킨슨")) categoryNoList.add(35);
				if(disease.contains("정신질환")) categoryNoList.add(36);
			}
			String level = map.get("level");
			if(level.length() > 0) {
				if(level.contains("경증")) categoryNoList.add(61);
				if(level.contains("중등중")) categoryNoList.add(62);
				if(level.contains("중증")) categoryNoList.add(63);
			}
			
			String walk = map.get("walk");
			if(walk.length() > 0) {
				if(walk.contains("가능")) categoryNoList.add(40);
				if(walk.contains("불가능")) categoryNoList.add(41);
			}

			if(!categoryNoList.isEmpty()) { // 카테고리 번호가 포함된 맵들로만 걸러내자
				for(int i = 0; i < searchCategoryMatNoList.size(); i++) {
					if(!categoryNoList.contains(Integer.parseInt(String.valueOf(searchCategoryMatNoList.get(i).get("CATEGORY_NO"))))) {
						searchCategoryMatNoList.remove(i);
					}
				}
			}
			
			ArrayList<Integer> resultMatNoList = new ArrayList<Integer>();
			if(categoryNoList.isEmpty()) {
				// 카테고리 필터링을 하지 않았을 때 tempMatNoList에 원래의 매칭번호들이 담겨있음
				resultMatNoList = tempMatNoList;
			} else { // 카테고리 필터링을 했을 때 searchCategoryMatNoList에서 매칭번호들을 추출해야함
				for(HashMap<String, Integer> m : searchCategoryMatNoList) {
					Integer matNo = Integer.parseInt(String.valueOf(m.get("MAT_NO")));
					if(!resultMatNoList.contains(matNo)) {
						resultMatNoList.add(matNo);
					}
				}
			}
			// 조건에 따른 매칭번호 추출 완료! resultMatNoList
			
			int listCount = resultMatNoList.size();
			PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 8);
			ArrayList<Matching> matList = new ArrayList<Matching>();
			ArrayList<MatPtInfo> mpiList = new ArrayList<MatPtInfo>();
			ArrayList<Member> memList = new ArrayList<Member>();
			
			if(!resultMatNoList.isEmpty()) {
				matList = mService.searchMatchingList(pi, resultMatNoList);
				mpiList = mService.selectMatchingPTInfoList(resultMatNoList);
				memList = mService.selectMatchingMemberList(resultMatNoList);
			}
			
			if(!matList.isEmpty() && !mpiList.isEmpty() && !memList.isEmpty()) {
				result.put("matList", matList);
				result.put("mpiList", mpiList);
				result.put("memList", memList);
				gson.toJson(result, response.getWriter());
			} else {
				gson.toJson("noExist", response.getWriter());
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (JsonIOException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@GetMapping("moreCaregiverInfo.me")
	public String moreCaregiverInfo(@RequestParam(value = "page", defaultValue="1") int currentPage, HttpSession session, Model model) {
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
		public void searchCaregiverList(@RequestParam("condition") String obj, @RequestParam(value="page", defaultValue="1") int currentPage,
										HttpServletResponse response) {
			// 검색 조건이 하나라도 있는 경우만 이곳으로 들어온다
			System.out.println("검색조건 확인 : " + obj);
			
			ObjectMapper mapper = new ObjectMapper();
			try {
				Map<String, String> map =
				               mapper.readValue(obj, new TypeReference<Map<String, String>>(){});
				
				// 검색조건과 페이지에 맞게 조회해와야함
				response.setContentType("application/json; charset=UTF-8");
				GsonBuilder gb = new GsonBuilder().setDateFormat("YYYY-MM-dd");
				Gson gson = gb.create();
				
				// 공동간병 검색조건을 가공
				ArrayList<String> shareList = new ArrayList<String>();
				if(map.get("share").length() > 0) { // 공동간병에 대한 검색조건이 있는 경우
					if(map.get("share").contains("개인")) shareList.add("N");
					if(map.get("share").contains("공동")) shareList.add("Y");
				}
				
				// 지역 검색조건을 가공
				String area = "";
				if(map.get("area").length()>0) {
					switch(map.get("area")) {
					case "전국" : area = "%"; break;
					case "서울", "부산", "대구", "인천", "광주", "대전", "울산", "제주","세종" : area = map.get("area"); break;
					case "경기도" : area = "경기"; break;
					case "강원도" : area = "강원"; break;
					case "충청북도" : area = "충%북";  break;
					case "충청남도" : area = "충%남"; break;
					case "전라북도" : area = "전%북";  break;
					case "전라남도" : area = "전%남";  break;
					case "경상북도" : area = "경%북"; break;
					case "경상남도" : area = "경%남"; break;
					}
				}
				
				// 성별 검색조건을 가공
				ArrayList<String> genderList = new ArrayList<String>();
				if(map.get("gender").length()>0) {
					if(map.get("gender").contains("남")) {
						genderList.add("M");
					}
					if(map.get("gender").contains("여")) {
						genderList.add("F");
					}
				}
				// 연령 검색조건을 가공 : 경우의 수가 8가지(선택을 하지 않은 경우 포함)이므로 식별값을 부여
				String age = "";
				if(map.get("age").length() > 0) {
					switch(map.get("age")) {
					case "청년": age = "1"; break;
					case "중년": age = "2"; break;
					case "장년": age = "3"; break;
					case "청년/중년": age = "4"; break;
					case "청년/장년": age = "5"; break;
					case "중년/장년": age = "6"; break;
					case "청년/중년/장년": age = "7"; break;
					}
				}
				// 금액 검색조건을 가공
				String maxMoney = "";
				if(map.get("cost").length() > 0) {
					switch(map.get("cost")) {
					case "~30,000원" : maxMoney = "30000"; break;
					case "~50,000원" : maxMoney = "50000"; break;
					case "~80,000원" : maxMoney = "80000"; break;
					case "~100,000원" : maxMoney = "100000"; break;
					}
				}
				
				// 서비스, 공동간병, 지역, 성별, 연령, 비용에 대하여 검색한 매칭번호를 조회한다.
				// 검색 조건이 없는 경우 mapper에게 List를 전달하지 않을 것
				HashMap<String, Object> searchDefaultMap = new HashMap<String, Object>();
				if(!shareList.isEmpty()) searchDefaultMap.put("share", shareList);
				if(area.length() > 0) searchDefaultMap.put("area", area);
				if(!genderList.isEmpty()) searchDefaultMap.put("gender", genderList);
				if(age.length() > 0) searchDefaultMap.put("age", age);
				if(maxMoney.length() > 0) searchDefaultMap.put("maxMoney", maxMoney);
				
				ArrayList<CareGiver> searchDefaultCaregiverNoList = mService.searchDefaultCaregiverNoList(searchDefaultMap);
				// 만약, 검색조건 중 위에서의 검색조건이 없었다면 MEMBER_STATUS = 'N' AND MEMBER_CATEGORY = 'C'인 간병인들이 조회된다!
				//[CareGiver(memberNo=83, careImg=null, careIntro=null, minMoney=0, maxMoney=0, careJoinStatus=null, careService=null, careUpdateDate=null, infoCategory=null, caregiverRealAge=44, caregiverNational=내국인, haveLicense=null, caregiverAddress=null, haveDisease=null, memberGender=M, wantService=null, haveService=null, career=null, memberName=컴2, avgReviewScore=0), CareGiver(memberNo=79, careImg=null, careIntro=null, minMoney=0, maxMoney=0, careJoinStatus=null, careService=null, careUpdateDate=null, infoCategory=null, caregiverRealAge=34, caregiverNational=내국인, haveLicense=null, caregiverAddress=null, haveDisease=null, memberGender=M, wantService=null, haveService=null, career=null, memberName=나리간병3, avgReviewScore=0), CareGiver(memberNo=85, careImg=null, careIntro=null, minMoney=0, maxMoney=0, careJoinStatus=null, careService=null, careUpdateDate=null, infoCategory=null, caregiverRealAge=69, caregiverNational=내국인, haveLicense=null, caregiverAddress=null, haveDisease=null, memberGender=M, wantService=null, haveService=null, career=null, memberName=나리간병5, avgReviewScore=0), CareGiver(memberNo=22, careImg=null, careIntro=null, minMoney=0, maxMoney=0, careJoinStatus=null, careService=null, careUpdateDate=null, infoCategory=null, caregiverRealAge=0, caregiverNational=내국인, haveLicense=null, caregiverAddress=null, haveDisease=null, memberGender=M, wantService=null, haveService=null, career=null, memberName=test, avgReviewScore=0), CareGiver(memberNo=14, careImg=null, careIntro=null, minMoney=0, maxMoney=0, careJoinStatus=null, careService=null, careUpdateDate=null, infoCategory=null, caregiverRealAge=30, caregiverNational=외국인, haveLicense=null, caregiverAddress=null, haveDisease=null, memberGender=M, wantService=null, haveService=null, career=null, memberName=김종기2, avgReviewScore=0), CareGiver(memberNo=82, careImg=null, careIntro=null, minMoney=0, maxMoney=0, careJoinStatus=null, careService=null, careUpdateDate=null, infoCategory=null, caregiverRealAge=-20, caregiverNational=외국인, haveLicense=null, caregiverAddress=null, haveDisease=null, memberGender=M, wantService=null, haveService=null, career=null, memberName=나리간병4, avgReviewScore=0)]
				
				// 위의 조건에 해당하는 간병인 번호를 추출한다.
				ArrayList<Integer> cNoList = new ArrayList<Integer>();
				if(!searchDefaultCaregiverNoList.isEmpty()) { // 간병인 목록이 존재하는 경우
					for(CareGiver c : searchDefaultCaregiverNoList) {
						cNoList.add(c.getMemberNo());
					}
				}// [83, 79, 85, 22, 14, 82]
				
				// 후보 간병인에 대한 카테고리 넘버들을 가져온다.
				ArrayList<HashMap<String, Integer>> searchCaregiverCategoryNoList = new ArrayList<HashMap<String, Integer>>();
				if(!cNoList.isEmpty()) {
					searchCaregiverCategoryNoList = mService.searchCaregiverCategoryMNoList(cNoList);
				} else {
					gson.toJson("noExist", response.getWriter());
				}
				// [{CATEGORY_NO=15, MEMBER_NO=83}, {CATEGORY_NO=1, MEMBER_NO=83}, {CATEGORY_NO=2, MEMBER_NO=83}, {CATEGORY_NO=21, MEMBER_NO=83}, {CATEGORY_NO=22, MEMBER_NO=83}, {CATEGORY_NO=23, MEMBER_NO=83}, {CATEGORY_NO=24, MEMBER_NO=83}, {CATEGORY_NO=25, MEMBER_NO=83}, {CATEGORY_NO=26, MEMBER_NO=83}, {CATEGORY_NO=27, MEMBER_NO=83}, {CATEGORY_NO=28, MEMBER_NO=83}, {CATEGORY_NO=29, MEMBER_NO=83}, {CATEGORY_NO=30, MEMBER_NO=83}, {CATEGORY_NO=51, MEMBER_NO=83}, {CATEGORY_NO=72, MEMBER_NO=83}, {CATEGORY_NO=73, MEMBER_NO=83}, {CATEGORY_NO=11, MEMBER_NO=22}, {CATEGORY_NO=11, MEMBER_NO=14}, {CATEGORY_NO=2, MEMBER_NO=14}, {CATEGORY_NO=52, MEMBER_NO=14}, {CATEGORY_NO=51, MEMBER_NO=14}, {CATEGORY_NO=52, MEMBER_NO=14}, {CATEGORY_NO=51, MEMBER_NO=14}]

				
				// 질병과 서비스, 경력, 자격증 검색 조건을 가져온다
				ArrayList<Integer> categoryNoList = new ArrayList<Integer>(); // 조건을 카테고리 넘버로 변환하여 저장
				String disease = map.get("disease"); // 질병에 대한 검색조건
				if(disease.length() > 0) {
					if(disease.contains("치매")) categoryNoList.add(21);
					if(disease.contains("섬망")) categoryNoList.add(22);
					if(disease.contains("욕창")) categoryNoList.add(23);
					if(disease.contains("하반신 마비")) categoryNoList.add(24);
					if(disease.contains("전신 마비")) categoryNoList.add(25);
					if(disease.contains("와상환자")) categoryNoList.add(26);
					if(disease.contains("기저귀 케어")) categoryNoList.add(27);
					if(disease.contains("의식 없음")) categoryNoList.add(28);
					if(disease.contains("석션")) categoryNoList.add(29);
					if(disease.contains("피딩")) categoryNoList.add(30);
					if(disease.contains("소변줄")) categoryNoList.add(31);
					if(disease.contains("장루")) categoryNoList.add(32);
					if(disease.contains("투석")) categoryNoList.add(33);
					if(disease.contains("전염성 질환")) categoryNoList.add(34);
					if(disease.contains("파킨슨")) categoryNoList.add(35);
					if(disease.contains("정신질환")) categoryNoList.add(36);
				}
				
				String service = map.get("service"); // 서비스에 대한 검색조건
				if(service.length() > 0) {
					if(service.contains("병원")) categoryNoList.add(6);
					if(service.contains("가정")) categoryNoList.add(7);
				}
				
				String career = map.get("career"); // 경력에 대한 검색조건 : 없음/1년미만/1~3년/3~5년/5년이상
				if(career.length() > 0) {
					if(career.contains("없음")) categoryNoList.add(11);
					if(career.contains("1년미만")) categoryNoList.add(12);
					if(career.contains("1~3년")) categoryNoList.add(13);
					if(career.contains("3~5년")) categoryNoList.add(14);
					if(career.contains("5년이상")) categoryNoList.add(15);
				}
				
				String license = map.get("license"); // 자격증에 대한 검색조건 : 간병사/요양보호사/간호조무사
				if(license.length() > 0) {
					if(license.contains("간병사")) categoryNoList.add(51);
					if(license.contains("요양보호사")) categoryNoList.add(52);
					if(license.contains("간호조무사")) categoryNoList.add(53);
				}
				
				
				if(!categoryNoList.isEmpty()) { // 카테고리 번호가 포함된 맵들로만 걸러내자
					for(int i = 0; i < searchCaregiverCategoryNoList.size(); i++) {
						if(!categoryNoList.contains(Integer.parseInt(String.valueOf(searchCaregiverCategoryNoList.get(i).get("CATEGORY_NO"))))) {
							searchCaregiverCategoryNoList.remove(i);
						}
					}
				}
				
				
				ArrayList<Integer> resultCaregiverNoList = new ArrayList<Integer>();
				if(categoryNoList.isEmpty()) {
					// 카테고리 필터링을 하지 않았을 때 tempMatNoList에 원래의 매칭번호들이 담겨있음
					resultCaregiverNoList = cNoList;
				} else { // 카테고리 필터링을 했을 때 searchCategoryMatNoList에서 매칭번호들을 추출해야함
					for(HashMap<String, Integer> m : searchCaregiverCategoryNoList) {
						Integer memberNo = Integer.parseInt(String.valueOf(m.get("MEMBER_NO")));
						if(!resultCaregiverNoList.contains(memberNo)) {
							resultCaregiverNoList.add(memberNo);
						}
					}
				} // 조건에 따른 간병인의 회원번호 추출 완료! resultCaregiverNoList
				
				int listCount = resultCaregiverNoList.size();
				PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 8);
				ArrayList<CareGiver> cList = mService.searchCaregiverList(pi, resultCaregiverNoList);
				
				
				ArrayList<HashMap<String, Integer>> scoreList = new ArrayList<HashMap<String, Integer>>();
				if(!cList.isEmpty()) { // 간병인 목록이 존재하는 경우
					for(CareGiver c : cList) {
						cNoList.add(c.getMemberNo());
					}
					scoreList = mService.getCaregiverScoreList(resultCaregiverNoList);
					
					for(HashMap<String, Integer> m : scoreList) {
						for(CareGiver c : cList) {
							if(Integer.parseInt(String.valueOf(m.get("MEMBER_NO"))) == c.getMemberNo()) {
								c.setAvgReviewScore(Integer.parseInt(String.valueOf(m.get("AVGREVIEWSCORE"))));
							}
						}
					}
					gson.toJson(cList, response.getWriter());
				} else { // 간병인 목록이 존재하지 않는 경우
					gson.toJson("noExist", response.getWriter());
				}
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (JsonIOException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	

	@PostMapping("patientUpdate.me")
	public String updatePatient(@ModelAttribute Patient p, HttpSession session,
			@RequestParam("memInfo") String memInfo) {
		Member loginUser = (Member) session.getAttribute("loginUser");

		p.setMemberNo(loginUser.getMemberNo());

		System.out.println(p);
		System.out.println(memInfo);
		int result = mService.updatePatient(p); // 환자정보바꾸기

		int result2 = mService.deleteMemberInfo(loginUser.getMemberNo()); // 환자인포정보 한번 다 지우기
		if (!memInfo.equals("fail")) {

			String[] mis = memInfo.split(",");

			for (String mi : mis) {
				HashMap<String, Integer> info = new HashMap<String, Integer>();
				info.put("memberNo", loginUser.getMemberNo());
				info.put("categoryNo", Integer.parseInt(mi));
				int result3 = mService.insertMemberInfo(info);
			}

		}
		return "redirect:home.do";

	};

	
	@PostMapping("careGiverUpdate.me")
	public String updateCareGiver(@ModelAttribute Member m,@ModelAttribute CareGiver cg,HttpSession session, @RequestParam("memInfo") String memInfo) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		m.setMemberNo(loginUser.getMemberNo());
		cg.setMemberNo(loginUser.getMemberNo());
		
		System.out.println(m);
		System.out.println(cg);
		System.out.println(memInfo);
		int result = mService.updateCareGiver(cg);	//간병인정보바꾸기
		int result4 = mService.updateMemberVer2(m); //간병인은 같은 페이지에서 이름,나이,성별 세가지만 따로 바로 바꿀수있다!

		if(result==0 || result4 ==0) {
			throw new MemberException("에러!");
		}
		
		
		if(!memInfo.equals("fail")) {
			
			int result2 = mService.deleteMemberInfo(loginUser.getMemberNo()); //간병인인포정보 한번 다 지우기
			String[] mis = memInfo.split(",");
			
			for(String mi : mis) {
				HashMap<String,Integer> info = new HashMap<String,Integer>();
				info.put("memberNo",loginUser.getMemberNo());
				info.put("categoryNo",Integer.parseInt(mi));
				int result3 = mService.insertMemberInfo(info);
			}
			
		}
		return "redirect:home.do";
		
	};
	
		@PostMapping("moreCaregiverList.me")
		@ResponseBody
		public void moreCaregiverList(HttpServletResponse response, 
										@RequestParam(value="page", defaultValue="1") int currentPage) {
			System.out.println("컨트롤러의 페이지 : " + currentPage);
			
			// 페이지 첫 로드시 또는 검색조건이 하나도 없이 검색버튼을 눌렀을 때 이곳으로 요청이 들어옴
			Gson gson = new Gson();
			response.setContentType("application/json; charset=UTF-8");
			try {
				
				int listCount = mService.getCaregiverListCount();
				PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 8);
				//currentPage > pi.getEndPage()
				System.out.println("최대 페이지는 :" + pi.getMaxPage());
				if(currentPage > pi.getEndPage()) {
					gson.toJson("noExist", response.getWriter());
				} else {
					ArrayList<CareGiver> cList = mService.selectAllCaregiver(pi);
					ArrayList<Integer> cNoList = new ArrayList<Integer>();
					ArrayList<HashMap<String, Integer>> scoreList = new ArrayList<HashMap<String, Integer>>();
					if(!cList.isEmpty()) { // 간병인 목록이 존재하는 경우
						for(CareGiver c : cList) {
							cNoList.add(c.getMemberNo());
						}
						scoreList = mService.getCaregiverScoreList(cNoList);
						
						for(HashMap<String, Integer> m : scoreList) {
							for(CareGiver c : cList) {
								if(Integer.parseInt(String.valueOf(m.get("MEMBER_NO"))) == c.getMemberNo()) {
									c.setAvgReviewScore(Integer.parseInt(String.valueOf(m.get("AVGREVIEWSCORE"))));
								}
							}
						}
						gson.toJson(cList, response.getWriter());
					} else { // 간병인 목록이 존재하지 않는 경우
						gson.toJson("noExist", response.getWriter());
					}
				}
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
		String myAddress = infoMap.get("주소").contains("서울") ? "서울" : (infoMap.get("주소").contains("제주") ? "제주" : (infoMap.get("주소").contains("세종") ? "세종" : infoMap.get("주소")));
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
		System.out.println("GPT가 추천한 매칭번호 : " + result); //83, 82, 57, 85, 14, 46, 23, 22, 79, 84.
		String[] choice = result.split(", "); 
		System.out.println("GPT가 추천한 매칭번호의 스플릿 : " + Arrays.toString(choice)); // [90, 42, 83, 50, 23, 82, 85, 57, 14, 79.]
		ArrayList<Integer> choiceNoList = new ArrayList<Integer>();
		for(int i = 0; i < choice.length; i++) {
			if(choice[i].contains(".")) {
				System.out.println("에러의 원인일 수 있는 부분 : " + choice[i]);
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
	
	@GetMapping(value="caregiverPatientEvent.me", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String caregiverPatientEvent(Model model, HttpServletResponse response,
											@RequestParam("memberNo") int memberNo) {
		Member loginUser = (Member)model.getAttribute("loginUser");
		
		// 일정 조회 
		ArrayList<CalendarEvent> eList = new ArrayList<CalendarEvent>();
		ArrayList<Member> mList = new ArrayList<Member>();
		if(loginUser != null) {
			// 환자에 대한 성사된 매칭번호와 해당 간병인의 회원번호 조회
			ArrayList<HashMap<String, Integer>> list = mService.getPatientEvent(memberNo); // [{MAT_NO=69, MEMBER_NO=85}]
			ArrayList<Integer> memberNoList = new ArrayList<Integer>();
			ArrayList<Integer> matNoList = new ArrayList<Integer>();
			for(HashMap<String, Integer> m : list) {
				memberNoList.add(Integer.parseInt(String.valueOf(m.get("MEMBER_NO")))); 
				matNoList.add(Integer.parseInt(String.valueOf(m.get("MAT_NO"))));
			}
			
			// 해당 간병인에 대한 정보 조회 : 멤버번호, 간병인이름, 성별, 연령, 경력(필수, 1개), 자격증(선택, 0~3개) 
			//MEMBER : MEMBER_NO, MEMBER_NAME, MEMBER_GENDER, MEMBER_AGE
			//INFO_CATEGORY : L_CATEGORY==career, L_CATEGORY==license
			mList = mService.selectMemberList(memberNoList); // [Member(memberNo=85, memberId=null, memberPwd=null, memberName=나리간병5, memberGender=M, memberNickName=null, memberAge=null, memberPhone=null, memberEmail=null, memberCreateDate=null, memberAddress=null, memberCategory=null, memberStatus=null, memberNational=null, memberPay=null, memberUpdateDate=null, memberRealAge=69)]
			ArrayList<HashMap<String, Object>> infoList = mService.selectCaregiverInfo(memberNoList);
			//[{S_CATEGORY=병원돌봄, L_CATEGORY=service, MEMBER_NO=85}, {S_CATEGORY=가정돌봄, L_CATEGORY=service, MEMBER_NO=85}, {S_CATEGORY=동행서비스, L_CATEGORY=service, MEMBER_NO=85}, {S_CATEGORY=3, L_CATEGORY=career, MEMBER_NO=85}, {S_CATEGORY=섬망, L_CATEGORY=disease, MEMBER_NO=85}, {S_CATEGORY=기저귀 케어, L_CATEGORY=disease, MEMBER_NO=85}, {S_CATEGORY=간병사, L_CATEGORY=license, MEMBER_NO=85}, {S_CATEGORY=요양보호사, L_CATEGORY=license, MEMBER_NO=85}]

			
			// 해당 매칭에 대한 정보 조회 : 매칭정보 : 매칭번호, 시작날짜, 종료날짜, 시작시간, 종료시간, 금액, 시간제날짜, 간병인의 회원번호
			//MATCHING : MAT_NO, BEGIN_DT, END_DT, BEGIN_TIME, END_TIME, MONEY, MEMBER_NO
			//MATCHING_DATE : MAT_DATE
			eList = mService.patientCalendarEvent(matNoList);
			//[CalendarEvent(matNo=69, title=null, start=null, end=null, money=150000, hospitalNo=0, hospitalAddress=null, hospitalName=null, beginTime=12:00, endTime=15:00, matMode=0, matchingType=null, ptCount=0, matAddressInfo=null, ptNo=0, matDate=2024-08-09,2024-08-16,2024-08-15,2024-08-22,2024-08-23, beginDt=2024-08-09, endDt=2024-08-23, careGiverNo=85)]
			
			for(Member m : mList) {
				String career = ""; 
				String license = "";
				for(HashMap<String, Object> h : infoList) {
					if(String.valueOf(h.get("MEMBER_NO")).equals(m.getMemberNo()+"")) {
						if(h.get("L_CATEGORY").equals("career")) {
							career += h.get("S_CATEGORY") + "/";
						}
						if(h.get("L_CATEGORY").equals("license")) {
							license += h.get("S_CATEGORY") + "/";
						}
					}
				}
				
				if(career.length() > 0) { // 간병인의 경력이 존재하는 경우를 가르킴
					career = career.substring(0, career.lastIndexOf("/"));
					m.setCareer(career);
				}
				if(license.length() > 0) { // 간병인의 자격증이 존재하는 경우를 가르킴
					license = license.substring(0, license.lastIndexOf("/"));
					m.setLicense(license);
				}
			}
		}
		
		JSONArray array = new JSONArray();
		
		for(CalendarEvent c : eList) {
			for(Member m : mList) {
				int matNo = c.getMatNo();
				int caregiverNo = c.getCareGiverNo();
				int money = c.getMoney();
				String beginTime = c.getBeginTime();
				String endTime = c.getEndTime();
				String matAddressInfo = c.getMatAddressInfo();
				Date beginDate = c.getBeginDt();
				Date endDate = c.getEndDt();
				
				if(c.getPtCount() == 1) {
					if(c.getMatMode() == 1) {
						JSONObject obj = new JSONObject();
						obj.put("title", "개인 기간제 간병");
						obj.put("start", c.getBeginDt());
						obj.put("end", c.getEndDt());
						obj.put("matNo", matNo);
						obj.put("money", money);
						obj.put("matAddressInfo", matAddressInfo);
						obj.put("beginDate", beginDate);
						obj.put("endDate", endDate);
						if(caregiverNo == m.getMemberNo()) { //간병인이름, 성별, 연령, 경력(필수, 1개), 자격증(선택, 0~3개)
							obj.put("caregiverName", m.getMemberName());
							obj.put("caregiverGender", m.getMemberGender());
							obj.put("caregiverRealAge", m.getMemberRealAge());
							obj.put("caregiverCareer", m.getCareer());
							obj.put("caregiverLicense", m.getLicense()); // 자격증이 없을 때는 ""로 들어감
						}
						array.put(obj);
					} else {
						String[] strArr = c.getMatDate().split(",");
						System.out.println(Arrays.toString(strArr));
						for(int i = 0; i < strArr.length; i++) {
							JSONObject obj = new JSONObject();
							obj.put("title", "개인 시간제 간병");
							obj.put("start", strArr[i]);
							obj.put("end", strArr[i]);
							obj.put("matNo", matNo);
							obj.put("money", money);
							obj.put("matAddressInfo", matAddressInfo);
							obj.put("beginDate", beginDate);
							obj.put("endDate", endDate);
							if(caregiverNo == m.getMemberNo()) { //간병인이름, 성별, 연령, 경력(필수, 1개), 자격증(선택, 0~3개)
								obj.put("caregiverName", m.getMemberName());
								obj.put("caregiverGender", m.getMemberGender());
								obj.put("caregiverRealAge", m.getMemberRealAge());
								obj.put("caregiverCareer", m.getCareer());
								obj.put("caregiverLicense", m.getLicense()); // 자격증이 없을 때는 ""로 들어감
							}
							array.put(obj);
						}
					}
				} else {
					if(c.getMatMode() == 1) {
						JSONObject obj = new JSONObject();
						obj.put("title", "공동 기간제 간병");
						obj.put("start", c.getBeginDt());
						obj.put("end", c.getEndDt());
						obj.put("matNo", matNo);
						obj.put("money", money);
						obj.put("matAddressInfo", matAddressInfo);
						obj.put("beginDate", beginDate);
						obj.put("endDate", endDate);
						if(caregiverNo == m.getMemberNo()) { //간병인이름, 성별, 연령, 경력(필수, 1개), 자격증(선택, 0~3개)
							obj.put("caregiverName", m.getMemberName());
							obj.put("caregiverGender", m.getMemberGender());
							obj.put("caregiverRealAge", m.getMemberRealAge());
							obj.put("caregiverCareer", m.getCareer());
							obj.put("caregiverLicense", m.getLicense()); // 자격증이 없을 때는 ""로 들어감
						}
						array.put(obj);
					} else {
						String[] strArr = c.getMatDate().split(",");
						for(int i = 0; i < strArr.length; i++) {
							JSONObject obj = new JSONObject();
							obj.put("title", "공동 시간제 간병");
							obj.put("start", strArr[i]);
							obj.put("end", strArr[i]);
							obj.put("matNo", matNo);
							obj.put("money", money);
							obj.put("matAddressInfo", matAddressInfo);
							obj.put("beginDate", beginDate);
							obj.put("endDate", endDate);
							if(caregiverNo == m.getMemberNo()) { //간병인이름, 성별, 연령, 경력(필수, 1개), 자격증(선택, 0~3개)
								obj.put("caregiverName", m.getMemberName());
								obj.put("caregiverGender", m.getMemberGender());
								obj.put("caregiverRealAge", m.getMemberRealAge());
								obj.put("caregiverCareer", m.getCareer());
								obj.put("caregiverLicense", m.getLicense()); // 자격증이 없을 때는 ""로 들어감
							}
							array.put(obj);
						}
					}
				}
			}
		}
		return array.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@GetMapping("updatePwdView.me")
	public String updatePwdView() {
		return "updatePwd";
	};

	@PostMapping("updatePwd.me")
	public String updatePwd(@RequestParam("checkPwd") String checkPwd, @RequestParam("memberPwd") String memberPwd,
			HttpSession session, Model model) {
		Member loginUser = (Member) model.getAttribute("loginUser");

		if (bCrypt.matches(checkPwd, loginUser.getMemberPwd())) {

			HashMap<String, String> changeInfo = new HashMap<String, String>();
			changeInfo.put("memberId", loginUser.getMemberId());
			changeInfo.put("newPwd", bCrypt.encode(memberPwd));
			int result = mService.updatePassword(changeInfo);

			if (result > 0) {
				return "redirect:myInfo.me";
			} else {
				throw new MemberException("비밀번호 변경을 실패했습니다");
			}

		} else {

			throw new MemberException("비밀번호가 틀립니다");
		}

	};

	@GetMapping("updateMemberView.me")
	public String updateMemberView() {

		return "updateMember";
	}

	@PostMapping("updateMember.me")
	public String updateMember(@ModelAttribute Member m, @RequestParam("postcode") String postcode,
			@RequestParam("roadAddress") String roadAddress, @RequestParam("detailAddress") String detailAddress,
			@RequestParam("email") String email, @RequestParam("emailDomain") String emailDomain, HttpSession session,
			Model model) {

		Member loginUser = (Member) session.getAttribute("loginUser");

		String memberAddress = postcode + "//" + roadAddress + "//" + detailAddress;
		m.setMemberAddress(memberAddress);

		String memberEmail = email + "@" + emailDomain;
		m.setMemberEmail(memberEmail);

		m.setMemberId(loginUser.getMemberId());
		m.setMemberNo(loginUser.getMemberNo());

		System.out.println(m);

		int result = mService.updateMember(m);

		if (result > 0) {
			model.addAttribute("loginUser", mService.login(m));
			return "redirect:myInfo.me";
		}
		throw new MemberException("정보변경을 실패했습니다");
	}

	@GetMapping("socialLogin.me")
	public String socialLogin(@RequestParam("code") String code,HttpSession session,Model model,RedirectAttributes ra) {
		//소셜로그인 없으면 회원가입으로, 있으면 로그인 바로하게 하기
		System.out.println(code);
		Member m = mService.selectSocialLogin(code);
		if(m == null) {		//검사해서 없으면 회원가입창으로
			session.setAttribute("code", code);
			return "redirect:enroll1View.me";
		}else {				//검사해서 있으면 바로 로그인하기
			model.addAttribute("loginUser", m);

			if (m.getMemberCategory().equalsIgnoreCase("C")) {
				ra.addAttribute("memberNo", m.getMemberNo());

				return "redirect:caregiverMain.me";
			} else if(m.getMemberCategory().equalsIgnoreCase("P")) {
				
				
				return "redirect:patientMain.me";
			}

			return "redirect:patientMain.me";
		}
		
	}
	
}//클래스 끝

	
	




