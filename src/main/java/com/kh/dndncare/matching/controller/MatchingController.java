package com.kh.dndncare.matching.controller;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.kh.dndncare.matching.model.exception.MatchingException;
import com.kh.dndncare.matching.model.service.MatchingService;
import com.kh.dndncare.matching.model.vo.CareReview;
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

@Controller
public class MatchingController {
	
	@Autowired
	private MatchingService mcService;
	
	@GetMapping("publicMatching.mc")
	public String publicMatchingView(HttpSession session,Model model) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		if(loginUser !=null) {
			int memberNo = loginUser.getMemberNo();
			Patient patient = mcService.getPatient(memberNo);
			model.addAttribute("patient",patient);
			return "publicMatching";
			
		}
		throw new MatchingException("하하");
	}
	
	//2번째 페이지로 정보 전달 및 이동
	@PostMapping("publicMatching2.mc")
	public String publicMatching2(@ModelAttribute Patient patient,Model model,HttpSession session) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		if(loginUser !=null && patient !=null) {
			patient.setMemberNo(loginUser.getMemberNo());
			session.setAttribute("tempPatient", patient);
			return "publicMatching2";
		} else {
			throw new MatchingException("다음 페이지로 이동하는중 오류가 발생하였습니다.");
		}				
	}
	
	@PostMapping("publicMatchingApply.mc")
	public String publicMatchingApply(HttpSession session,@ModelAttribute Matching matching,@RequestParam("selectedSymptoms") String selectedSymptoms,
										@RequestParam("selectedMobility") String selectedMobility,@RequestParam("selectedGender") String selectedGender,
										@RequestParam(value="selectedDays",required =false) String selectDays) {
		Patient patient = (Patient)session.getAttribute("tempPatient");
		if(patient != null && matching != null && selectedSymptoms !=null
				&& selectedMobility !=null && selectedGender !=null) {
			matching.setMemberNo(patient.getMemberNo());
			matching.setPtCount(1);
			if(selectDays == null) {
				matching.setMatMode(1);
			} else {
				matching.setMatMode(2);
			}
			
		}
		System.out.println(patient);
		System.out.println("매칭 : " + matching);
		System.out.println("질병 : " + selectedSymptoms);
		System.out.println("거동 : " + selectedMobility);
		System.out.println("성별 : " + selectedGender);
		return null;
	}
	
	
	@GetMapping("joinMatchingMainView.jm")
	public String joinMatchingMainView() {
		
		return "joinMatchingMain";
	}
	
	//공동간병 병원 선택
	@GetMapping("joinMatching.jm")
	public String joinMatchinjmain(@RequestParam("hospitalName") String hospitalName, @RequestParam("hospitalAddress") String hospitalAddress, Model model) {
		
		//병원 정보 전달
		Hospital hospital = new Hospital();
		hospital.setHospitalName(hospitalName);
		hospital.setHospitalAddress(hospitalAddress);
		model.addAttribute("hospital", hospital);
		
		//병원으로 list 뽑기 
		ArrayList<MatMatptInfo> list = mcService.getJmList(hospitalName);
		System.out.println(list);
		
		model.addAttribute("list", list);
		return "joinMatching";
	}

	
	@GetMapping("joinMatchingEnrollView.jm")
	public String joinMatchingEnrollView(@RequestParam("hospitalName") String hospitalName, 
										@RequestParam("hospitalAddress") String hospitalAddress, Model model) {
		Hospital hospital = new Hospital();
		hospital.setHospitalName(hospitalName);
		hospital.setHospitalAddress(hospitalAddress);
		model.addAttribute("hospital", hospital);
	
		return "joinMatchingEnroll";
	}
	
	
	
	//공동간병 등록
	@PostMapping("enrollJoinMatching.jm")
	public String enrollJoinMatching(@ModelAttribute Matching jm, @ModelAttribute MatPtInfo jmPt, @ModelAttribute Hospital hospital,
									HttpSession session, RedirectAttributes re,
									@RequestParam("day") String[] day, 
									@RequestParam("begin") String begin, @RequestParam("end") String end,
									@RequestParam("beginTime") String beginTime, @RequestParam("endTime") String endTime) {
		
		//병원이 테이블에 없을 경우 등록 && 매칭 테이블 병원 셋
		Hospital ho = mcService.getHospital(hospital);
		if(ho == null) {
			 mcService.enrollHospital(hospital);
			jm.setHospitalNo(hospital.getHospitalNo());
		}else {
			jm.setHospitalNo(ho.getHospitalNo());
		}
		
		//매칭 등록
		jm.setMoney(jmPt.getAntePay() * jm.getPtCount());		
		
		//날짜-시간 변환
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date utilDate = null;
        Date sqlDate = null;
		//기간제
		if(jm.getMatMode() == 1) {
		    try {
	        	utilDate = dateFormat.parse(begin);
	            sqlDate = new Date(utilDate.getTime());
	            jm.setBeginDt(sqlDate);
	            utilDate = dateFormat.parse(end);
	            sqlDate = new Date(utilDate.getTime());
	            jm.setEndDt(sqlDate);
	         } catch (ParseException e) {
	            e.printStackTrace();
	         }	
		//시간제
		} else if(jm.getMatMode() == 2) {
	         Arrays.sort(day);
	         try {
	        	utilDate = dateFormat.parse(day[0]);
	            sqlDate = new Date(utilDate.getTime());
	            jm.setBeginDt(sqlDate);
	            utilDate = dateFormat.parse(day[day.length-1]);
	            sqlDate = new Date(utilDate.getTime());
	            jm.setEndDt(sqlDate);
	         } catch (ParseException e) {
	            e.printStackTrace();
	         }	
		}
		
		//시간 set (불필요한 , 뺴기)
		beginTime = beginTime.replace(",", "");
		endTime = endTime.replace(",", "");	
		jm.setBeginTime(beginTime);
		jm.setEndTime(endTime);
		
		
		System.out.println("등록" + jm);
		int result2 = mcService.enrollMatching(jm);
		System.out.println("등록 후" + jm);
		
		
		if(jm.getMatMode() == 2) {
			String matchingDate = Arrays.toString(day);
			mcService.insertMatchingDate(jm.getMatNo(), matchingDate);
		}
		
		//매칭 환자 등록
		jmPt.setMatNo(jm.getMatNo());
		int memberNo = ((Member)session.getAttribute("loginUser")).getMemberNo();
		Patient pt = mcService.getPatient(memberNo);		
		jmPt.setPtNo(pt.getPtNo());
		jmPt.setService("공동간병");
		jmPt.setMatAddressInfo(hospital.getHospitalAddress() +" // "+ jmPt.getMatAddressInfo());
		jmPt.setGroupLeader("Y");
		System.out.println("등록" + jmPt);
		int result3 = mcService.enrollMatPtInfo(jmPt);
		
		
		if(result2>0 && result3>0) {
			re.addAttribute("hospitalName", hospital.getHospitalName());
			re.addAttribute("hospitalAddress", hospital.getHospitalAddress());
			return "redirect:joinMatching.jm";
		}
		throw new MemberException("공동간병 그룹 등록 실패");

	}
	
	
	//공동간병 상세 정보 
	@PostMapping(value="getJmContent.jm", produces="application/json; charset=UTF-8")
	@ResponseBody
	public void getjmMatMatptInfo(@RequestParam("matNo") int matNo, HttpServletResponse response) {
		
		//get matching & ptinfo
		MatMatptInfo jmMatMatptInfo = mcService.getMatMatptInfo(matNo);
		
		//MatNo로 get 공동간병 Patient
		ArrayList<Patient> jmPts = mcService.getPatientToMatNo(matNo);		
		
		//공동간병 Patient에 member info set
		for(Patient jmPt : jmPts) {
			
			//get member info (대분류 : 소분류)
			ArrayList<InfoCategory> jmPtInfos =  mcService.getInfo(jmPt.getMemberNo());
			System.out.println(jmPtInfos);
			
			ArrayList<String> disease =  new ArrayList<>();
			String diseaseLevel = null;
			if(jmPtInfos != null) {
				for(InfoCategory jmPtInfo : jmPtInfos) {
					if(jmPtInfo.getLCategory().equals("disease")) {
						disease.add(jmPtInfo.getSCategory());					
					}else if(jmPtInfo.getLCategory().equals("diseaseLevel")) {
						
						diseaseLevel = jmPtInfo.getSCategory();				
					}	
				}	
				//공동 간병 참여자들 Patient에 member info set
				jmPt.setDisease(disease);
				jmPt.setDiseaseLevel(diseaseLevel);
			}		

		}		
		
		System.out.println("jmMatMatptInfo" + jmMatMatptInfo);
		System.out.println("jmPts" + jmPts);
		HashMap<String, Object> jmMacPt = new HashMap<String, Object>();
		jmMacPt.put("jmMatMatptInfo", jmMatMatptInfo);
		jmMacPt.put("jmPts", jmPts);
				
		response.setContentType("application/json; charset=UTF-8");
		GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd");
		Gson gson = gb.create();
		
		try {
			gson.toJson(jmMacPt, response.getWriter());
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
		
	
	}

	//공동간병 참여
	@PostMapping("joinJoinMatching.jm")
	public String joinJoinMatching(@RequestParam("matNo") int matNo, @RequestParam("matRequest") String matRequest,
									HttpSession session) {
		
		MatPtInfo joinMatptInfo = new MatPtInfo();
		
		MatMatptInfo jmMatMatptInfo = mcService.getMatMatptInfo(matNo);	
		joinMatptInfo.setMatNo(jmMatMatptInfo.getMatNo());
		int memberNo = ((Member)session.getAttribute("loginUser")).getMemberNo();
		Patient joinPt = mcService.getPatient(memberNo);
		joinMatptInfo.setPtNo(joinPt.getPtNo());
		joinMatptInfo.setAntePay(jmMatMatptInfo.getAntePay());
		joinMatptInfo.setService("공동간병");
		joinMatptInfo.setMatAddressInfo(jmMatMatptInfo.getMatAddressInfo());
		joinMatptInfo.setMatRequest(matRequest);
		joinMatptInfo.setGroupLeader("N");
		System.out.println(joinMatptInfo);
		
		int result = mcService.enrollMatPtInfo(joinMatptInfo);

		if(result > 0) {
			return "redirect:joinMatching.jm";
		}
		throw new MemberException("공동간병 그룹 등록 실패");
	}
	
	
	
	@GetMapping("reviewDetail.mc")
	public String getMethodName(HttpSession session, Model model) {
		int memberNo = ((Member)session.getAttribute("loginUser")).getMemberNo();
		
		// 정보
		ArrayList<CareReview> reviewList = mcService.selectReviewList(memberNo);
		
		// 후기개수
		int reviewCount = mcService.reviewCount(memberNo);
		
		// 평점
		int avgReviewScore = mcService.avgReviewScore(memberNo);
		
		model.addAttribute("reviewList", reviewList);
		model.addAttribute("reviewCount", reviewCount);
		model.addAttribute("avgReviewScore",avgReviewScore);
		return "reviewDetail";
	}
	
	
	
	
	
	
	
	
	
	
	
}
