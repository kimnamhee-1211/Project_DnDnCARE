package com.kh.dndncare.matching.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.kh.dndncare.matching.model.service.MatchingService;
import com.kh.dndncare.matching.model.vo.Hospital;
import com.kh.dndncare.matching.model.vo.MatMatptInfo;
import com.kh.dndncare.matching.model.vo.MatPtInfo;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.vo.InfoCategory;
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@SessionAttributes("hospital")
@Controller
public class MatchingController {
	
	@Autowired
	private MatchingService mcService;
	
	@GetMapping("publicMatching.mc")
	public String publicMatchingView() {
		return "publicMatching";
	}
	
	
	@GetMapping("joinMachingMainView.jm")
	public String joinMachingMainView() {
		
		return "joinMachingMain";
	}
	
	//공동간병 병원 선택
	@GetMapping("joinMaching.jm")
	public String joinMachingMain(@RequestParam("hospitalName") String hospitalName, @RequestParam("hospitalAddress") String hospitalAddress,  Model model) {
		
		//병원 정보 전달
		Hospital hospital = new Hospital();
		hospital.setHospitalName(hospitalName);
		hospital.setHospitalAddress(hospitalAddress);
		model.addAttribute("hospital", hospital);
		
		
		//병원으로 list 뽑기 
		ArrayList<MatMatptInfo> list = mcService.getGmList(hospitalName);
		System.out.println(list);
		
		model.addAttribute("list", list);
		return "joinMaching";
	}

	
	@GetMapping("joinMachingEnrollView.jm")
	public String joinMachingEnrollView(@RequestParam("hospitalName") String hospitalName, 
										@RequestParam("hospitalAddress") String hospitalAddress, Model model) {
		Hospital hospital = new Hospital();
		hospital.setHospitalName(hospitalName);
		hospital.setHospitalAddress(hospitalAddress);
		model.addAttribute("hospital", hospital);
	
		return "joinMachingEnroll.jm";
	}
	
	
	
	//공동간병 등록
	@PostMapping("enrolljoinMaching.jm")
	public String enrolljoinMaching(@ModelAttribute Matching gm, @ModelAttribute MatPtInfo gmPt, @ModelAttribute Hospital ho,
									HttpSession session, Model model) {
		
		//병원등록
		int result1 = mcService.enrollHospital(ho);
		
		//매칭 등록
		gm.setMoney(gmPt.getAntePay() * gm.getPtCount());
		gm.setMatType(2);
		gm.setHospitalNo(ho.getHospitalNo());		
		System.out.println("등록" + gm);
		int result2 = mcService.enrollMatching(gm);
		
		//매칭 환자 등록
		gmPt.setMatNo(gm.getMatNo());
		int memberNo = ((Member)session.getAttribute("loginUser")).getMemberNo();
		Patient pt = mcService.getPatient(memberNo);		
		gmPt.setPtNo(pt.getPtNo());
		gmPt.setService("공동간병");
		gmPt.setMatAddressInfo(gmPt.getMatAddressInfo());	
		System.out.println("등록" + gmPt);
		int result3 = mcService.enrollMatPtInfo(gmPt);
		
		
		if(result1>0 && result2>0 && result3>0) {
			return "redirect:joinMachingMy.jm";
		}
		throw new MemberException("공동간병 매칭 실패");
	}
	
	
	//공동간병 상세 정보 
	@PostMapping(value="getGmContent.jm", produces="application/json; charset=UTF-8")
	@ResponseBody
	public void getGmMatMatptInfo(@RequestParam("matNo") int matNo, HttpServletResponse response) {
		
		//get matching & ptinfo
		MatMatptInfo gmMatMatptInfo = mcService.getMatMatptInfo(matNo);
		
		//MatNo로 get 공동간병 Patient
		ArrayList<Patient> gmPts = mcService.getPatientToMatNo(matNo);		
		
		//공동간병 Patient에 member info set
		for(Patient gmPt : gmPts) {
			
			//get member info (대분류 : 소분류)
			ArrayList<InfoCategory> gmPtInfos =  mcService.getInfo(gmPt.getMemberNo());
			System.out.println(gmPtInfos);
			
			ArrayList<String> disease =  new ArrayList<>();
			String diseaseLevel = null;
			if(gmPtInfos != null) {
				for(InfoCategory gmPtInfo : gmPtInfos) {
					if(gmPtInfo.getLCategory().equals("disease")) {
						disease.add(gmPtInfo.getSCategory());					
					}else if(gmPtInfo.getLCategory().equals("diseaseLevel")) {
						
						diseaseLevel = gmPtInfo.getSCategory();				
					}	
				}	
				//공동 간병 참여자들 Patient에 member info set
				gmPt.setDisease(disease);
				gmPt.setDiseaseLevel(diseaseLevel);
			}		

		}		
		
		System.out.println("gmMatMatptInfo" + gmMatMatptInfo);
		System.out.println("gmPts" + gmPts);
		HashMap<String, Object> gmMacPt = new HashMap();
		gmMacPt.put("gmMatMatptInfo", gmMatMatptInfo);
		gmMacPt.put("gmPts", gmPts);
				

		response.setContentType("application/json; charset=UTF-8");
		GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd");
		Gson gson = new Gson();
		
		try {
			gson.toJson(gmMacPt, response.getWriter());
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
		
	
	}

	
	
	
	
	
	
	
	
	
	
}
