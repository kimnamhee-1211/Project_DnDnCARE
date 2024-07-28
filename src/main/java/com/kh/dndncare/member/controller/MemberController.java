package com.kh.dndncare.member.controller;

import java.sql.Date;
import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
import org.springframework.web.servlet.ModelAndView;

import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.service.MemberService;
import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

import jakarta.servlet.http.HttpSession;

@SessionAttributes({"loginUser", "tempMemberCategory"})
@Controller
public class MemberController {
	
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


//	@GetMapping("myInfo.me")
//	public String myInfo() {		//마이페이지 확인용
//		return "myInfo";
//	}


	
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
						@RequestParam("email") String email, @RequestParam("emailDomain") String emailDomain, 
						HttpSession session, Model model) {
		
		//간병인/환자 택
		String memberCategory = (String)session.getAttribute("tempMemberCategory");
		
		m.setMemberCategory(memberCategory);
		String memberPwd = bCrypt.encode(m.getMemberPwd().toLowerCase());
		m.setMemberPwd(memberPwd);
		String memberAddress = postcode +"//"+ roadAddress +"//"+ detailAddress;
		m.setMemberAddress(memberAddress);
		String memberEmail = email + "@" + emailDomain;
		m.setMemberEmail(memberEmail);
		
		System.out.println("회원가입 검증=" + m);
		
		model.addAttribute("m", m);
		int result = mService.enroll(m);
		if(result > 0) {
			return "enroll2";
		}else {
			throw new MemberException("회원가입에 실패했습니다.");
		}		
		
	}
	
	
	//타입별 회원가입 페이지 이동
	@PostMapping("enroll2View.me")
	public String enrol21View(@ModelAttribute Member m, Model model, HttpSession session) {
		model.addAttribute("m", m);
		String memberCategory = (String)session.getAttribute("tempMemberCategory");
		if(memberCategory.equals("C")) {
			return "enroll3";
		}else {
			return "enroll5";
		}
		
	}
	
	
	//간병인 회원가입(간병인 정보 입력)
	@PostMapping("enrollCaregiver.me")
	public String enrollCaregiver(@ModelAttribute CareGiver cg, @ModelAttribute Member m, @RequestParam("careService") String[] careServiceArr) {
		//간병인 memberNo 세팅
		String memberId = (String)m.getMemberId();
		int memberNo = mService.getMemberNo(memberId);
		cg.setMemberNo(memberNo);
		
		//간병인 기본 정보
		String careService = "";
		for(String i : careServiceArr) {
			careService += (i + "//");
		}
		cg.setCareService(careService);
		
		System.out.println(cg);
		
		//간병인 테이블 조인(경력 사항 관련) 세팅
		if(cg.getServiceName() != null || cg.getDisaseName() != null || cg.getLicenseName() != null) {
			cg.setCareJoinStatus("Y");
		}else {
			cg.setCareJoinStatus("N");
		}		
		
		//간병인 테이블 insert
		System.out.println(cg);
		int result = mService.enrollCareGiver(cg);
		
		//간병인 테이블 조인(경력 사항 관련) insert
		if(cg.getServiceName() != null) {
			int result1 = mService.enrollExpService(cg);
		}
		
		if(cg.getDisaseName() != null) {
			int result2 = mService.enrollDisase(cg, null, "EXP");
		}
		if(cg.getLicenseName() != null) {
			int result3 = mService.enrollLicense(cg);
		}
		
		if(result>0) {
			return "emroll4";
		}else {
			throw new MemberException("회원가입에 실패했습니다.");
		}
	}

	
	@PostMapping("enrollCaregiverWantPt.me")
	public String enrollCaregiverWantPt(@ModelAttribute Patient pt, @ModelAttribute Member m,
										@RequestParam("ptAge") int ptAge) {
		
		//간병인 memberNo 세팅
		String memberId = (String)m.getMemberId();
		int memberNo = mService.getMemberNo(memberId);
		pt.setMemberNo(memberNo);
		
		//간병인 원하는 환자 나이 계산		
		Calendar c = Calendar.getInstance();
		int currentYear = c.get(Calendar.YEAR);
		int ptAgeYear = currentYear - ptAge;
		
		Calendar calendar = Calendar.getInstance();
        calendar.set(ptAgeYear, Calendar.JANUARY, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date sqlDate = new Date(calendar.getTimeInMillis());		
		pt.setPtAge(sqlDate);
		
		//간병인 원하는 환자 입력
		System.out.println(pt);
		
		int result = mService.enrollCaregiverWantPt(pt);
				
		if(pt.getDiseaseName() != null) {
			int result2 = mService.enrollDisase(null, pt, "WANT");
		}
		if(pt.getDiseaseLevel() != null) {
			int result3 = mService.enrollDisaseLevel(null, pt);
		}
		
		if(result>0) {
			return "emroll7";
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}



	












