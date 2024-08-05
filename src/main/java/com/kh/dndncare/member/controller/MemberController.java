package com.kh.dndncare.member.controller;

import java.io.IOException;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
	
	// ai추천 로직 메소드
	public ArrayList<Patient> openAiChoice(int memberNo) {
		// (1) 자동추천 기능구현 : 간병인이라 가정하고 테스트
		// 1. 간병인 정보 조회 
		// 		MEMBER : 성별, 나이, 주소, 국적
		HashMap<String, String> infoMap =  mService.getCaregiverInfo(memberNo); // {국적=내국인, 주소=제주특별자치도 제주시 첨단로 242 히히, 나이=30, 성별=남성}
		// 		INFO_CATEGORY : 서비스경험, 경력, 질환경험, 자격증, 중증도
		ArrayList<HashMap<String, String>> cExpList = mService.getCaregiverExp(memberNo); // [{S_CATEGORY=병원돌봄, L_CATEGORY=service}, {S_CATEGORY=0, L_CATEGORY=career}, {S_CATEGORY=호흡기 질환, L_CATEGORY=disase}, {S_CATEGORY=거동불편, L_CATEGORY=disase}, {S_CATEGORY=와상환자, L_CATEGORY=disase}, {S_CATEGORY=간병사, L_CATEGORY=license}]
		
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
			ArrayList<Patient> completeList = openAiChoice(memberNo); // 추천목록이 없으면 null로 넘어옴
			
			model.addAttribute("completeList", completeList);
		}
		
		
		
		
		
		
		
		
		return "caregiverMain";
	}
	
	// 자동추천을 비동기 통신으로 요청
	@GetMapping("refreshChoice.me")
	@ResponseBody
	public void refreshChoice(@RequestParam("memberNo") int memberNo, HttpServletResponse response) {
		// 후보군이 있을 때만 새로고침 버튼이 활성화 되기 때문에 null로 넘어오는 경우는 배제함
		ArrayList<Patient> completeList = openAiChoice(memberNo); 

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
	public String patientMain() {
		
		
		
		
		
		
		
		
		
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
	public String moreWorkInfo() {
		return "moreWorkInfo";
	}
	
	
	//무한스크롤 테스트 중 : 성공
	@GetMapping("workInfoTest.me")
	@ResponseBody
	public void workInfoTest(HttpServletResponse response, @RequestParam("page") int currentPage) {
		
		System.out.println("컨트롤러");
		
		Gson gson = new Gson();
		ArrayList<String> list = new ArrayList<String>();
		list.add("1");
		list.add("2");
		list.add("3");
		list.add("4");
		list.add("5");
		list.add("6");
		list.add("7");
		list.add("8");
		response.setContentType("application/json; charset=UTF-8");
		try {
			gson.toJson(list, response.getWriter());
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	// 간병인 일감찾기 페이지에서의 검색 요청을 처리
//	@PostMapping("searchPatientList.me")
//	public 
	
	
	
	
	
	
}



	












