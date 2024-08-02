package com.kh.dndncare.joinMaching.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kh.dndncare.joinMaching.model.service.JoinMachingService;
import com.kh.dndncare.joinMaching.model.vo.Hospital;
import com.kh.dndncare.matching.model.vo.MatPtInfo;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

import jakarta.servlet.http.HttpSession;

@SessionAttributes("hospital")
@Controller
public class JoinMachingController {

	@Autowired
	private JoinMachingService jmService;
	
	
	@GetMapping("joinMachingMainView.jm")
	public String joinMachingMainView() {
		
		return "joinMachingMain";
	}
	
	@GetMapping("joinMaching.jm")
	public String joinMachingMain(@RequestParam("hospitalName") String hospitalName, @RequestParam("hospitalAddress") String hospitalAddress,  Model model) {
		Hospital hospital = new Hospital();
		hospital.setHospitalName(hospitalName);
		hospital.setHospitalAddress(hospitalAddress);
		
		model.addAttribute("hospital", hospital);

		return "joinMaching";
	}

	
	@GetMapping("joinMachingEnrollView.jm")
	public String joinMachingEnrollView() {
		return "joinMachingMy"; /*테스트 중  원래 : joinMachingEnroll*/
	}
	
	
	
	
	@PostMapping("enrolljoinMaching.jm")
	public String enrolljoinMaching(@ModelAttribute Matching gm, @ModelAttribute MatPtInfo gmPt, 
									HttpSession session, Model model) {
		
		//병원등록
		Hospital ho = (Hospital) session.getAttribute("hospital");
		int result1 = jmService.enrollHospital(ho);
		
		//매칭 등록
		gm.setMoney(gmPt.getAntePay() * gm.getPtCount());
		gm.setMatType(2);
		gm.setHospitalNo(ho.getHospitalNo());		
		System.out.println("등록" + gm);
		int result2 = jmService.enrollMatching(gm);
		
		//매칭 환자 등록
		gmPt.setMatNo(gm.getMatNo());
		int memberNo = ((Member)session.getAttribute("loginUser")).getMemberNo();
		Patient pt = jmService.getPatient(memberNo);		
		gmPt.setPtNo(pt.getPtNo());
		gmPt.setService("공동간병");
		gmPt.setMatAddressInfo(ho.getHospitalAddress() +" "+ gmPt.getMatAddressInfo());	
		System.out.println("등록" + gmPt);
		int result3 = jmService.enrollMatPtInfo(gmPt);
		
		
		if(result1>0 && result2>0 && result3>0) {
			model.addAttribute("gm", gm);
			model.addAttribute("pt", pt);
			model.addAttribute("gmPt", gmPt);
			
			return "joinMachingMy";
		}

		throw new MemberException("공동간병 메칭 실패");
	}
	
	
	
	
	
	
}
