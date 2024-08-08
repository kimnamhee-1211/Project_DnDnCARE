package com.kh.dndncare.member.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
import com.kh.dndncare.matching.model.vo.MatMatptInfo;
import com.kh.dndncare.matching.model.vo.MatMatptInfoPt;
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


			switch (check) {
			case 'C':
				return "myInfo";

			case 'P':
				Info memberInfo = categoryFunction(loginUser.getMemberNo(), true); // 내정보
				Info wantInfo = categoryFunction(loginUser.getMemberNo(), false); // 원하는간병인정보
				model.addAttribute("p", p);
				model.addAttribute("memberInfo", memberInfo);
				model.addAttribute("wantInfo", wantInfo);
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
				 switch(m.get("L_CATEGORY")) {
					 case "service" : memberInfo.getInfoService().add(0,m.get("S_CATEGORY")); break;
					 case "serviceCareer" : memberInfo.getInfoServiceCareer().add(0,m.get("S_CATEGORY")); break;
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
	public ArrayList<Patient> openAiPatientChoice(int memberNo) {
		// 1. 간병인 정보 조회 
//		조회할 항목
//			필수입력 : 원하는 서비스, 공동간병 참여여부, 경력, 적정비용, 성별, 나이, 주소
//			선택입력 : 서비스 경험, 돌봄경험, 자격증
//		항목의 출처
//			WANT_INFO : 원하는 서비스(필수),
//			CAREGIVER : 공동간병 희망여부(필수,CARE_JOIN_STATUS), 적정비용(필수, MIN_MONEY, MAX_MONEY), 
//			MEMBER_INFO : 경력기간(필수), 서비스경험(선택), 돌봄경험(선택), 자격증(선택)
//			MEMBER: 성별(필수, MEMBER_GENDER), 나이(필수, MEMBER_AGE), 주소(필수, MEMBER_ADDRESS), 국적(필수, MEMBER_NATIONAL)
		
		
		
		
		
		
		
		
		
		
		// 		MEMBER : 성별, 나이, 주소, 국적
		
		
		/*
		
		HashMap<String, String> infoMap =  mService.getCaregiverInfo(memberNo); // {국적=내국인, 주소=제주특별자치도 제주시 첨단로 242 히히, 나이=30, 성별=남성}
		// 		INFO_CATEGORY : 서비스경험, 경력, 질환경험, 자격증, 중증도
		ArrayList<HashMap<String, String>> cExpList = mService.getCaregiverExp(memberNo); // [{S_CATEGORY=병원돌봄, L_CATEGORY=service}, {S_CATEGORY=0, L_CATEGORY=career}, {S_CATEGORY=호흡기 질환, L_CATEGORY=disase}, {S_CATEGORY=거동불편, L_CATEGORY=disase}, {S_CATEGORY=와상환자, L_CATEGORY=disase}, {S_CATEGORY=간병사, L_CATEGORY=license}]
		
<<<<<<< HEAD
=======
		// 1. 간병인 정보 조회
		// MEMBER : 성별, 나이, 주소, 국적
		HashMap<String, String> infoMap = mService.getCaregiverInfo(memberNo); // {국적=내국인, 주소=제주시 첨단로 242 히히, 나이=29,
																				// 성별=남성}
		// INFO_CATEGORY : 서비스경험, 경력, 질환경험, 자격증, 중증도
		ArrayList<HashMap<String, String>> expList = mService.getCaregiverExp(memberNo); // [{S_CATEGORY=병원돌봄,
																							// L_CATEGORY=service},
																							// {S_CATEGORY=0,
																							// L_CATEGORY=career},
																							// {S_CATEGORY=호흡기 질환,
																							// L_CATEGORY=disase},
																							// {S_CATEGORY=거동불편,
																							// L_CATEGORY=disase},
																							// {S_CATEGORY=와상환자,
																							// L_CATEGORY=disase},
																							// {S_CATEGORY=간병사,
																							// L_CATEGORY=license}]

>>>>>>> refs/remotes/origin/Kiryong
=======
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
>>>>>>> refs/remotes/origin/myunghun
		// 2. 간병인 정보 가공
		String service = ""; // service
		String career = ""; // career
		String disease = ""; // disease
		String license = ""; // license
		for(HashMap<String, String> m : cExpList) {
			switch(m.get("L_CATEGORY")) {
			case "service" : service += m.get("S_CATEGORY") + "/"; break;
			case "career" : career = m.get("S_CATEGORY"); break;
			case "disease" : disease += m.get("S_CATEGORY") + "/"; break;
			case "license" : license += m.get("S_CATEGORY") + "/"; break;
			}
		}
		service = service.substring(0, service.lastIndexOf("/"));
		disease = disease.substring(0, disease.lastIndexOf("/"));
		license = license.substring(0, license.lastIndexOf("/"));

		infoMap.put("서비스경험", service);
		infoMap.put("경력", career);
		infoMap.put("돌봄질환경험", disease);
		infoMap.put("자격증", license); // 가공 종료! => infoMap
		
		// 3. 환자 목록 조회
		ArrayList<HashMap<String, Object>> promptPatientList = new ArrayList<HashMap<String, Object>>(); // 프롬프트에 던질 후보군 리스트
		// 가) 환자 목록을 조회하기 위한 조건절에 쓰일 간병인 정보
		String[] addressArr = infoMap.get("주소").split(" ");
		String caregiverCity = addressArr[0] + addressArr[1]; // 간병인 주소 중 "xx도 xx시"의 정보만 선택
		
		// 나) 환자 기본정보 조회 : 회원번호, 이름, 성별, 나이, 간병장소, 국적, 키, 몸무게, 요청서비스, 요청사항, 요청장소, 매칭 시작일, 매칭 종료일, 금액
		ArrayList<Patient> pList = mService.selectPatientList(caregiverCity); // 길이 : 0~10
		ArrayList<HashMap<String, String>> pExpList = null;
		
		if(pList.size() != 0) {
			ArrayList<Integer> pNoList = new ArrayList<Integer>();
			for(Patient p : pList) {
				pNoList.add(p.getMemberNo());
			}
			// 다) 환자 질병정보 조회 (INFO_CATEGORY) : 보유질환, 중증도
			pExpList = mService.getPatientExp(pNoList);
			
			// 4. 환자 정보의 목록을 가공
			// 회원번호, 성별, 나이, 간병장소, 국적, 키, 몸무게, 요청서비스, 요청사항, 요청장소, 매칭 시작일, 매칭 종료일, 금액
			// 보유질환, 중증도
			for(int i = 0; i < pList.size(); i++) {
				Patient p = pList.get(i);
				
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("회원번호", p.getMemberNo());
				map.put("성별", p.getPtGender().equalsIgnoreCase("M") ? "남성" : "여성");
				map.put("나이", p.getPtRealAge());
				map.put("간병장소", p.getPtAddress());
				map.put("국적", p.getMemberNational());
				map.put("키", p.getPtHeight() + "cm");
				map.put("몸무게", p.getPtWeight() + "kg");
				map.put("요청서비스", p.getPtService());
				map.put("요청사항", p.getPtRequest());
				map.put("간병 시작일", p.getBeginDt());
				map.put("간병 종료일", p.getEndDt());
				map.put("지급가능한 비용", p.getMoney());
				
				String pDisease = ""; // 환자 보유 질환
				String pDiseaseLevel = ""; // 환자 보유 중증도
				if(pExpList != null) {
					for(HashMap<String, String> m : pExpList) {
						switch(m.get("L_CATEGORY")) {
						case "disease" : pDisease += m.get("S_CATEGORY") + "/"; break;
						case "diseaseLevel" : pDiseaseLevel = m.get("S_CATEGORY"); break;
						}
					}
					pDisease = pDisease.substring(0, pDisease.lastIndexOf("/"));
				}
				
				map.put("보유질환", pDisease);
				map.put("중증도", pDiseaseLevel);
				
				promptPatientList.add(map);
			} // 후보군 가공 종료! => promptPatientList
			
			// 5. 프롬프트 작성
			String prompt = "간병인 정보는" + infoMap.toString() + "이고" + "환자 목록은" + promptPatientList.toString() + "이다."
							+ "간병인의 정보를 바탕으로 가장 적절한 회원번호 5개만 숫자로만 짧게 대답해줘.";
			
			// 6. 프롬프트를 전달하고 결과값 받아오기
			String result = botController.chat(prompt); // "2, 4, 8, 10, 14"
			ArrayList<Integer> choiceNoList = new ArrayList<Integer>();
			for(int i = 0; i < choiceNoList.size(); i++) {
				choiceNoList.add(Integer.parseInt(result.split(", ")[i]));
			} // [2, 5, 8, 10, 14] 
			
			// 7. View로 전달한 결과값만 추리기
			// 이름, 성별, 나이, 지역, 질환, 금액
			ArrayList<Patient> completeList = mService.choicePatientList(choiceNoList);
			ArrayList<HashMap<String, String>> diseaseList = mService.getPatientExp(choiceNoList);
			
			if(!diseaseList.isEmpty()) {
				for(Patient p : completeList) {
					String temp = "";
					for(HashMap<String, String> m : diseaseList) {
						if(p.getMemberNo() == Integer.parseInt(m.get("L_CATEGORY"))) {
							temp = m.get("S_L_CATEGORY") + "/";
						}
					}
					temp = temp.substring(0, temp.lastIndexOf("/"));
					p.setPtDisease(temp);
				}
			}
			
			return completeList;
		} else {
			return null;
		}
		*/
		return null;
	}
	
	// 임시 : 메인페이지 이동시 캘린더 이벤트 조회
	// 메인페이지 이동 후에 환자 메인페이지 렌더링 도중에 캘린더 이벤트를 조회하게 된다.
	@GetMapping("caregiverCalendarEvent.me")
	@ResponseBody
	public void calendarEvent(Model model, HttpServletResponse response) {
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
	
	
	
	// 간병인 메인페이지로 가기 
	@GetMapping("caregiverMain.me")
	public String caregiverMain(HttpSession session, Model model) {
		// 1. 자동 추천 목록 받아오기
		Member loginUser = (Member)session.getAttribute("loginUser");
		int memberNo = 0;
		if(loginUser != null) {
			memberNo = loginUser.getMemberNo(); 
			ArrayList<Patient> completeList = openAiPatientChoice(memberNo); // 추천목록이 없으면 null로 넘어옴
			
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
			String[] addr = matMatptInfoPtListBefore.get(i).getMatAddressInfo().split(" ");
			String addressMin = addr[0] + " " +  addr[1];  //00도 00시//
			matMatptInfoPtListBefore.get(i).setMatAddressMin(addressMin);
			
			if(i < 6 ){			
			matMatptInfoPtList1.add(matMatptInfoPtListBefore.get(i));
			}else if(i<12) {
				matMatptInfoPtList2.add(matMatptInfoPtListBefore.get(i));
			}else if(i<18) {
				matMatptInfoPtList3.add(matMatptInfoPtListBefore.get(i));
			}			
			
		}
		
		model.addAttribute("matMatptInfoPtList1", matMatptInfoPtList1);
		model.addAttribute("matMatptInfoPtList2", matMatptInfoPtList2);
		model.addAttribute("matMatptInfoPtList3", matMatptInfoPtList3);
		

		//infoMap.put("자격증", license); // 가공 종료!

		// 2. 환자 목록 조회

		// 2. 환자 목록 설정
//		String pStr = "{\r\n"
//				+ "			환자번호=1/필요한서비스=병원돌봄/원하는간병인경력=0년이상/보유질환=치매/중증도=경증/간병받을장소=성북서울요양병원/지불가능한금액=50,000원이하,\r\n"
//				+ "			환자번호=2/필요한서비스=가정돌봄/원하는간병인경력=1년이상/보유질환=호흡기질환/중증도=중증/간병받을장소=종로구관철동19-19/지불가능한금액=100,000원이하,\r\n"
//				+ "			환자번호=3/필요한서비스=동행서비스/원하는간병인경력=3년이상/보유질환=거동불편/중증도=경증/간병받을장소=아산병원/지불가능한금액=60,000원이하,\r\n"
//				+ "			환자번호=4/필요한서비스=병원돌봄/원하는간병인경력=5년이상/보유질환=외상환자/중증도=중증/입원병원=연세세브란스병원/지불가능한금액=70,000원이하,\r\n"
//				+ "			환자번호=5/필요한서비스=가정돌봄/원하는간병인경력=8년이상/보유질환=파킨슨/중증도=경증/입원병원=부여중앙병원/지불가능한금액=80,000원이하,\r\n"
//				+ "			환자번호=6/필요한서비스=동행서비스/원하는간병인경력=0년이상/보유질환=수면질환/중증도=중증/입원병원=삼성서울병원  /지불가능한금액=40,000원이하,\r\n"
//				+ "			환자번호=7/필요한서비스=병원돌봄/원하는간병인경력=1년이상/보유질환=정신질환/중증도=경증/입원병원=성북서울요양병원/지불가능한금액=80,000원이하,\r\n"
//				+ "			환자번호=8/필요한서비스=가정돌봄/원하는간병인경력=3년이상/보유질환=재활/중증도=중증/입원병원=서울대학교병원/지불가능한금액=70,000원이하,\r\n"
//				+ "			환자번호=9/필요한서비스=동행서비스/원하는간병인경력=5년이상/보유질환=치매/중증도=경증/입원병원=서울성모병원/지불가능한금액=50,000원이하,\r\n"
//				+ "			환자번호=10/필요한서비스=병원돌봄/원하는간병인경력=8년이상/보유질환=호흡기질환/중증도=중증/입원병원=연세세브란스병원/지불가능한금액=40,000원이하,\r\n"
//				+ "			환자번호=11/필요한서비스=가정돌봄/원하는간병인경력=0년이상/보유질환=거동불편/중증도=경증/입원병원=강북삼성병원/지불가능한금액=70,000원이하,\r\n"
//				+ "			환자번호=12/필요한서비스=동행서비스/원하는간병인경력=1년이상/보유질환=외상환자/중증도=중증/입원병원=반포서래여성의원/지불가능한금액=65,000원이하,\r\n"
//				+ "			환자번호=13/필요한서비스=가정돌봄/원하는간병인경력=3년이상/보유질환=재활/중증도=경증/입원병원=서울병원/지불가능한금액=70,000원이하,\r\n"
//				+ "			환자번호=14/필요한서비스=병원돌봄/원하는간병인경력=5년이상/보유질환=골절/중증도=중증/입원병원=서울중앙의원/지불가능한금액=80,000원이하,\r\n"
//				+ "		}";

		// 3.
//		String prompt = "간병인 정보는" + cMap.toString() + "환자 목록은 " + pStr + "간병인의 정보를 바탕으로 가장 적절한 환자번호 5개만 숫자로만 짧게 대답해줘.";
//		
//		String result = botController.chat(prompt); // "2, 4, 8, 10, 14"
//		
//		
//		ArrayList<Integer> list = new ArrayList<Integer>();
//		for(int i = 0; i < 5; i++) {
//			list.add(Integer.parseInt(result.split(", ")[i]));
//		} // [2, 5, 8, 10, 14] 


		return "caregiverMain";
	}
	
	// 자동추천을 비동기 통신으로 요청
//	@GetMapping("refreshChoice.me")
//	@ResponseBody
//	public void refreshChoice(@RequestParam("memberNo") int memberNo, HttpServletResponse response) {
//		// 후보군이 있을 때만 새로고침 버튼이 활성화 되기 때문에 null로 넘어오는 경우는 배제함
//		ArrayList<Patient> completeList = openAiPatientChoice(memberNo); 
//	}
	
	
	
	
	
	
	

	// 임시버튼 : 환자 메인페이지로 가기
	@GetMapping("patientMain.me")
	public String patientMain() {
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

		int result = mService.enroll(m);

		// 회원가입용 session데이터
		model.addAttribute("enrollmember", m);
		System.out.println("회원가입 데이터 전송 검증 =" + m);

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

	@GetMapping("myInfoMatchingHistory.me")
	public String myInfoMatchingHistory(HttpSession session,Model model) {		//마이페이지 매칭 이력 확인용
		
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		
		if(loginUser != null) {
			char check = loginUser.getMemberCategory().charAt(0);
			switch(check) {
				case 'C': return "myInfoMatchingHistory";
				
				case 'P':
					ArrayList<MatMatptInfo> mciList = mService.selectMatList(loginUser.getMemberNo());	//환자측 매칭방번호 리스트.ptNo가들어가서무조건
					for(MatMatptInfo i : mciList) {
						System.out.println(i);
						i.setAfterDate(LocalDate.now().isAfter(i.getBeginDt().toLocalDate()));
						
					}
					model.addAttribute("mciList",mciList);
					model.addAttribute("today", LocalDate.now());
					return "myInfoMatchingHistoryP";
				case 'A': return null;
			}
		}

		throw new MemberException("로그인없음. 인터셉터설정");
	}

	@GetMapping("myInfoMatchingReview.me")
	public String myInfoMatchingReview(HttpSession session) { // 마이페이지 매칭 이력 확인용

		Member loginUser = (Member) session.getAttribute("loginUser");

		if (loginUser != null) {
			char check = loginUser.getMemberCategory().charAt(0);
			switch (check) {
			case 'C':
				return "myInfoMatchingReview";
			case 'P':
				return "myInfoMatchingReviewP";
			case 'A':
				return null;
			}
		}
		throw new MemberException("로그인없음. 인터셉터설정");
	}
	
	// 내 작성글 보기
	@GetMapping("myInfoBoardList.me")
	public String myInfoBoardList(@RequestParam(value="page", defaultValue = "1") int currentPage, Model model, HttpSession session) {		//마이페이지 보드작성 확인용
		
	    
		Member loginUser = (Member)session.getAttribute("loginUser");
		int mNo = loginUser.getMemberNo();

	// 내 작성글 보기
//	   @GetMapping("myInfoBoardList.me")
//	   public String myInfoBoardList(@RequestParam(value="page", defaultValue = "1") int currentPage, Model model, HttpSession session) {      //마이페이지 보드작성 확인용
//	      
//	       
//	      Member loginUser = (Member)session.getAttribute("loginUser");
//	      int mNo = loginUser.getMemberNo();
//
//	      // 게시글페이지네이션
//	      int boardListCount = mService.getBoardListCount(mNo); 
//	       PageInfo boardPi = Pagination.getPageInfo(currentPage, boardListCount, 5);
//	       
//	      // 내 작성글 정보
//	      ArrayList<Board> boardList = mService.mySelectBoardList(boardPi, mNo);
//	      
//	      // 내 작성글 좋아요
//	      HashMap<Integer, Integer> boardLikeCounts = new HashMap<>();
//	      for (Board board : boardList) {
//	         int boardLikeCount = mService.boardLikeCount(board.getBoardNo());
//	         boardLikeCounts.put(board.getBoardNo(), boardLikeCount);
//	      }
//	      
//	      // 댓글 페이지네이션
//	      int replyListCount = mService.getReplyListCount(mNo);
//	      PageInfo replyPi = Pagination.getPageInfo(currentPage, replyListCount, 5);
//	      
//	      // 내 댓글 정보
//	      ArrayList<Reply> replyList = mService.mySelectReplyList(replyPi, mNo);
//	      
//	      // 내 댓글 좋아요
//	      HashMap<Integer, Integer> replyLikeCounts = new HashMap<>();
//	      for(Reply reply : replyList) {
//	         int replyLikeCount = mService.replyLikeCount(reply.getReplyNo());
//	         replyLikeCounts.put(reply.getReplyNo(), replyLikeCount);
//	      }
//	      // 좋아요 페이지네이션
//	      int likeListCount = mService.getLikeListCount(mNo);
//	      PageInfo likePi = Pagination.getPageInfo(currentPage, likeListCount, 5);
//
//	      // 좋아요한 글 목록
//	      ArrayList<Board> likeList = mService.mySelectLikeList(likePi, mNo);
//	      
//	      // 좋아요한 글 좋아요
//	      HashMap<Integer, Integer> likeLikeCounts = new HashMap<>();
//	      for (Board board : likeList) {
//	         int likeLikeCount = mService.likeLikeCount(board.getBoardNo());
//	         likeLikeCounts.put(board.getBoardNo(), likeLikeCount);
//	      }
//	      
//	      
//	      model.addAttribute("boardPi", boardPi);
//	      model.addAttribute("boardList", boardList);
//	      model.addAttribute("boardLikeCounts", boardLikeCounts);
//	      model.addAttribute("replyPi", replyPi);
//	      model.addAttribute("replyList", replyList);
//	      model.addAttribute("replyLikeCounts", replyLikeCounts);
//	      model.addAttribute("likePi", likePi);
//	      model.addAttribute("likeList", likeList);
//	      model.addAttribute("likeLikeCounts", likeLikeCounts);
//	      return "myInfoBoardList";
//	   }
	/*
	 * // 좋아요한 글 목록 ArrayList<Board> likeList = mService.mySelectLikeList(likePi,
	 * mNo);
	 * 
	 * // 좋아요한 글 좋아요 HashMap<Integer, Integer> likeLikeCounts = new HashMap<>(); for
	 * (Board board : likeList) { int likeLikeCount =
	 * mService.likeLikeCount(board.getBoardNo());
	 * likeLikeCounts.put(board.getBoardNo(), likeLikeCount); }
	 * 
	 * 
	 * model.addAttribute("boardPi", boardPi); model.addAttribute("boardList",
	 * boardList); model.addAttribute("boardLikeCounts", boardLikeCounts);
	 * model.addAttribute("replyPi", replyPi); model.addAttribute("replyList",
	 * replyList); model.addAttribute("replyLikeCounts", replyLikeCounts);
	 * model.addAttribute("likePi", likePi); model.addAttribute("likeList",
	 * likeList); model.addAttribute("likeLikeCounts", likeLikeCounts);
	 */
		
		
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
=======

	// 임시 : 메인페이지 이동시 캘린더 이벤트 조회
	public void calendarEvent(Model model, HttpServletResponse response) {
		Member loginUser = (Member) model.getAttribute("loginUser");

		// 일정 조회
>>>>>>> refs/remotes/origin/Kiryong
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
	public String moreWorkInfo() {
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
	public String moreCaregiverInfo(@RequestParam(value = "page", defaultValue="1") int page ) {
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

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}//클래스 끝

	
	




