package com.kh.dndncare.matching.controller;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
import com.kh.dndncare.common.AgeCalculator;
import com.kh.dndncare.matching.model.exception.MatchingException;
import com.kh.dndncare.matching.model.service.MatchingService;
import com.kh.dndncare.matching.model.vo.CareReview;
import com.kh.dndncare.matching.model.vo.Hospital;
import com.kh.dndncare.matching.model.vo.MatMatptInfo;
import com.kh.dndncare.matching.model.vo.MatMatptInfoPt;
import com.kh.dndncare.matching.model.vo.MatPtInfo;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.matching.model.vo.Pay;
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
	                              @RequestParam(value="selectedDays",required =false) String selectDays,@RequestParam("selectedCareer") String selectedCareer
	                              ,@RequestParam("selectedLocal") String selectedLocal,@RequestParam("selectedAge") String selectedAge) {
	      Patient patient = (Patient)session.getAttribute("tempPatient");
	      int memberNo = patient.getMemberNo();
	      String formattedDates = null;
	      int dateResult = 0;
	      // 문자열을 Integer 리스트로 변환
	      Map<String, Object> params = new HashMap<>();
	        params.put("symptoms", Arrays.stream(selectedSymptoms.split(","))
	                                     .map(Integer::parseInt)
	                                     .collect(Collectors.toList()));
	        System.out.println(params);
	        // 단일 값들도 Integer로 변환
	        params.put("mobility", selectedMobility != null ? Integer.parseInt(selectedMobility) : null);
	        params.put("gender", selectedGender != null ? Integer.parseInt(selectedGender) : null);
	        params.put("career", selectedCareer != null ? Integer.parseInt(selectedCareer) : null);
	        params.put("local", selectedLocal != null ? Integer.parseInt(selectedLocal) : null);
	        params.put("age", selectedAge != null ? Integer.parseInt(selectedAge) : null);
	        params.put("memberNo",memberNo);
	        
	        //원래있던 memberNo에 해당하는 want-info 삭제후 want_info insert
	        int deleteWantInfo = mcService.deleteWantInfo(memberNo);
	        int wantInfoResult = mcService.insertWantInfo(params);

	        
	      
	      
	      if(patient != null && matching != null && selectedSymptoms !=null
	            && selectedMobility !=null && selectedGender !=null
	            && selectedCareer !=null && selectedLocal !=null && selectedAge !=null) {
	         
	         Patient previousPatient = mcService.getPatient(memberNo);
	         int ptNo = previousPatient.getPtNo();
	         patient.setPtNo(ptNo);
	         //patient 정보 update
	         int patientResult = mcService.updatePatient(patient);


	         matching.setPtCount(1);
	         matching.setHospitalNo(99);
	         if(selectDays == null) {
	            matching.setMatMode(1);
	         } else {
	            matching.setMatMode(2);
	         }
	         System.out.println("matching : " + matching);
	         //Matching 정보 삽입
	         int matchingResult = mcService.enrollMatching(matching);
	         
	         int matNo = matching.getMatNo();
	         
	         //시간제일 때 Matching_date 테이블 insert
	         if(matching.getMatMode() == 2 && selectDays != null) {
	            formattedDates = convertDates(selectDays);
	            HashMap<String,Object> map = new HashMap<String,Object>();
	            map.put("formattedDates", formattedDates);
	            map.put("matNo", matNo);
	            dateResult = mcService.insertMatchingDate(map);
	         }

	         //mat_pt_info insert
	         MatPtInfo matPtInfo = new MatPtInfo();
	         matPtInfo.setMatNo(matNo);
	         matPtInfo.setPtNo(ptNo);
	         matPtInfo.setAntePay(matching.getMoney());
	         matPtInfo.setService("개인간병");
	         matPtInfo.setMatAddressInfo(patient.getPtAddress());
	         matPtInfo.setMatRequest("일단없음");
	         matPtInfo.setGroupLeader("N");

	         int ptInfoResult = mcService.enrollMatPtInfo(matPtInfo);
	         int finalResult = wantInfoResult + patientResult + ptInfoResult + dateResult + matchingResult + deleteWantInfo;
	         
	         if(finalResult!=0) {
	            return "redirect:myInfo.me";
	         } else {
	            throw new MatchingException("공개구인 신청에 실패하였습니다");
	         }
	      }
	      throw new MatchingException("하하");
	      
	            
	   }
	
	//selectDays 타입 변환 메소드
	private String convertDates(String selectDays) {
	    if (selectDays == null || selectDays.trim().isEmpty()) {
	        return "";
	    }
	
	    DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");
	    DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	
	    return Arrays.stream(selectDays.split(", "))
	            .map(String::trim)
	            .map(date -> LocalDate.parse(date, inputFormatter))
	            .map(date -> date.format(outputFormatter))
	            .collect(Collectors.joining(","));
	}
	
	
	
	
	@GetMapping("joinMatchingMainView.jm")
	public String joinMatchingMainView() {
		
		return "joinMatchingMain";
	}
	
	//공동간병 병원 선택
	@GetMapping("joinMatching.jm")
	public String joinMatchinjmain(@RequestParam("hospitalName") String hospitalName, @RequestParam("hospitalAddress") String hospitalAddress, 
								Model model, HttpSession session, @RequestParam(value="msg", required=false) String msg) {
		
		//병원 정보 전달
		Hospital hospital = new Hospital();
		hospital.setHospitalName(hospitalName);
		hospital.setHospitalAddress(hospitalAddress);
		model.addAttribute("hospital", hospital);
		
		//병원으로 list 뽑기 
		ArrayList<MatMatptInfo> list = mcService.getJmList(hospitalName);
		
		
		//loginUser가 그룹에 참여중인지 아닌지 확인 => view 표시용
		//loginUser-MatNo get
		Member loginUser = (Member)session.getAttribute("loginUser");
		Set<Integer> loginMatNos = mcService.getloginMatNo(loginUser.getMemberNo());
		for (MatMatptInfo l : list) {
		    if (loginMatNos.contains(l.getMatNo())) {
		        l.setJoin("Y");
		    } else {
		        l.setJoin("N");
		    }
		}
		
		model.addAttribute("msg", msg);
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
			int result = mcService.enrollHospital(hospital);
			if(result > 0) {
				jm.setHospitalNo(hospital.getHospitalNo());
			}
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
		jm.setBeginTime(beginTime.replace(",", ""));
		jm.setEndTime(endTime.replace(",", ""));
		
		
		System.out.println("등록" + jm);
		int result2 = mcService.enrollMatching(jm);
		System.out.println("등록 후" + jm);
		
		//macthing date set(불필요한 괄호 빼기)
		if(jm.getMatMode() == 2) {
			String matDate = Arrays.toString(day).replace("[", "").replace("]", "");
			@SuppressWarnings("unused")
			int result4 = mcService.insertMatDate(jm.getMatNo(), matDate);
		}
		
		//매칭 환자 등록
		jmPt.setMatNo(jm.getMatNo());
		int memberNo = ((Member)session.getAttribute("loginUser")).getMemberNo();
		Patient pt = mcService.getPatient(memberNo);		
		jmPt.setPtNo(pt.getPtNo());
		jmPt.setService("공동간병");
		jmPt.setMatAddressInfo(hospital.getHospitalAddress() +"//"+ jmPt.getMatAddressInfo());
		jmPt.setGroupLeader("Y");
		System.out.println("등록" + jmPt);
		int result3 = mcService.enrollMatPtInfo(jmPt);
		
		
		if(result2>0 && result3>0) {
			re.addAttribute("hospitalName", hospital.getHospitalName());
			re.addAttribute("hospitalAddress", hospital.getHospitalAddress());
			re.addAttribute("msg", "공동간병 등록이 완료되었습니다.");
			return "redirect:joinMatching.jm";
		}
		throw new MemberException("공동간병 그룹 등록 실패");

	}
	
	
	//공동간병 상세 정보 
	@PostMapping(value="getJmContent.jm", produces="application/json; charset=UTF-8")
	@ResponseBody
	public void getjmMatMatptInfo(@RequestParam("matNo") int matNo, HttpServletResponse response, HttpSession session) {
		
		//get matching & ptinfo
		MatMatptInfo jmMatMatptInfo = mcService.getMatMatptInfo(matNo);
		
		//MatNo로 get 공동간병 Patient
		ArrayList<Patient> jmPts = mcService.getPatientToMatNo(matNo);
		
		//loginUser-get ptNo
		Member loginUser = (Member)session.getAttribute("loginUser");
		Patient loginPt = mcService.getPatient(loginUser.getMemberNo());
				
		//공동간병 Patient에 member info set
		for(Patient jmPt : jmPts) {
			
			//get member info (대분류 : 소분류)
			ArrayList<InfoCategory> jmPtInfos =  mcService.getInfo(jmPt.getPtNo());
			
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
		System.out.println("jmPts1" + jmPts);
		HashMap<String, Object> jmMacPt = new HashMap<String, Object>();
		jmMacPt.put("jmMatMatptInfo", jmMatMatptInfo);
		jmMacPt.put("jmPts", jmPts);
		jmMacPt.put("jmPts", jmPts);
		jmMacPt.put("loginPt", loginPt);
		
		//시간제일 경우 선택한 날짜 get + 전송
		if(jmMatMatptInfo.getMatMode() == 2) {
			String jmMatDate = mcService.getMatDate(jmMatMatptInfo.getMatNo());
			jmMacPt.put("jmMatDate", jmMatDate);
		}
		
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
								@RequestParam("hospitalName") String hospitalName, @RequestParam("hospitalAddress") String hospitalAddress,
								HttpSession session, RedirectAttributes  re) {
		
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
			re.addAttribute("hospitalName", hospitalName);
			re.addAttribute("hospitalAddress", hospitalAddress);
			re.addAttribute("msg", "공동간병 참여가 완료되었습니다.");
			return "redirect:joinMatching.jm";
		}
		throw new MemberException("공동간병 그룹 참여 실패");
	}
	
	//공동간병 참여 취소
	@PostMapping("outJoinMatching.jm")
	public String outJoinMatching(@RequestParam("matNo") int matNo, @RequestParam("matMode") int matMode, HttpSession session, 
								@RequestParam("hospitalName") String hospitalName, @RequestParam("hospitalAddress") String hospitalAddress,
								RedirectAttributes re) {
		
		Member loginUser = (Member) session.getAttribute("loginUser");
		int ptNo = mcService.getPtNo(loginUser.getMemberNo());
		
		//loginUser의 해당 매칭방에 대한 MatPtInfo 삭제
		int result = mcService.delMatPtInfo(matNo, ptNo);
		
		if(result > 0) {
			
			//매칭방에 참여하는 환자(MatPtInfo) 있는지 확인
			int joinPtCount =  mcService.joinPtCount(matNo);
			
			//매칭방에 참여하는 환자(MatPtInfo)가 없다면 매칭방 삭제
			if(joinPtCount < 1) {
				//매칭방 삭제 전 시간제일 경우 MatchingDate부터 삭제
				if(matMode == 2) {
					@SuppressWarnings("unused")
					int result1 = mcService.delMatchingDate(matNo);
				}
				@SuppressWarnings("unused")
				int result2 = mcService.delMatching(matNo);
			}
			
			re.addAttribute("hospitalName", hospitalName);
			re.addAttribute("hospitalAddress", hospitalAddress);
			re.addAttribute("msg", "공동간병 참여 취소가 완료되었습니다.");
			return "redirect:joinMatching.jm";			
		}else {
			throw new MemberException("공동간병 그룹 참여 취소 실패");
		}
	}
	
	
	@GetMapping("reviewDetail.mc")												
	public String getMethodName(HttpSession session, Model model,@RequestParam("memberNo")int memberNo) {
		
		//int memberNo = ((Member)session.getAttribute("loginUser")).getMemberNo();
		
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
	
	//공동간병 참여자 퇴장
	@PostMapping("walkoutJoinMatching.jm")
	public String walkoutJoinMatching(@RequestParam("matNo") int matNo, @RequestParam("ptNo") int ptNo,
									@RequestParam("hospitalName") String hospitalName, @RequestParam("hospitalAddress") String hospitalAddress,
									RedirectAttributes re) {
		int result = mcService.delMatPtInfo(matNo, ptNo);
		if(result > 0) {
			re.addAttribute("hospitalName", hospitalName);
			re.addAttribute("hospitalAddress", hospitalAddress);
			re.addAttribute("msg", "공동간병 참여자 퇴장이 완료되었습니다.");
			return "redirect:joinMatching.jm";
		}else {
			throw new MemberException("공동간병 참여자 퇴장 실패");
		}


	}
	
	//비동기로 환자측에서 결제할때 간병인 정보 가져오기
	@GetMapping("payInfo.mc")
	@ResponseBody
	public void payInfo(@RequestParam("matNo") int matNo,HttpServletResponse response,HttpSession session) {
		// 보낼때, 매칭번호가 필수다
		
		Member m = (Member)session.getAttribute("loginUser");
		MatMatptInfo matInfo = mcService.selecMatching(matNo);
		MatMatptInfo matPtInfo = mcService.selecMatPtInfo(matNo,m.getMemberNo());
		matInfo.setPtNo(matPtInfo.getPtNo());
		matInfo.setAntePay(matPtInfo.getAntePay());
		matInfo.setService(matPtInfo.getService());
		matInfo.setMatAddressInfo(matPtInfo.getMatAddressInfo());
		matInfo.setMatRequest(matPtInfo.getMatRequest());
		matInfo.setDeposit(matPtInfo.getDeposit());
		matInfo.setGroupLeader(matPtInfo.getGroupLeader());
		
		String hourly = mcService.selectMatDate(matNo);
		int hourly2 = hourly.split(",").length;
		matInfo.setHourly(hourly2);
		
		//며칠 몇시간 하는건지 계산해보자
		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        // LocalDateTime 객체로 변환
        LocalDateTime beforeDateTime = LocalDateTime.parse(matInfo.getBeginDt() + " " + matInfo.getBeginTime(), dateTimeFormatter);
        LocalDateTime afterDateTime = LocalDateTime.parse(matInfo.getEndDt() + " " + matInfo.getEndTime(), dateTimeFormatter);
        
        // 시간 차이 계산
        Duration duration = Duration.between(beforeDateTime, afterDateTime);
        
        // 일과 시간으로 변환
        long days = duration.toDays();
        matInfo.setDays(duration.toDays());
        matInfo.setTimes(duration.minusDays(days).toHours());
		
		
		
		
		GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd");	
		//내 date형식 포멧을 변경해준다.
		Gson gson = gb.create();
		response.setContentType("application/json; charset=UTF-8");
		try {
			gson.toJson(matInfo, response.getWriter());
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	
	//간병인 메인에서 환자 정보 페이지로 
	@GetMapping("goCaregiverPtInfo.mc")
	public String goCaregiverPtInfo(@RequestParam("matNo") int matNo, Model model, HttpSession session) {
				
		ArrayList<MatMatptInfoPt> mPI = mcService.matPtInfoToCaregiver("N", matNo);
		ArrayList<String> diseaseArr = new ArrayList<String>();
		ArrayList<String> diseaseLevel = new ArrayList<String>();
		String mobilityStatus = null;
		
		for(int i = 0; i < mPI.size(); i++) {
			//나이 계산
			int ptRealAge = AgeCalculator.calculateAge(mPI.get(i).getPtAge());
			mPI.get(i).setPtRealAge(ptRealAge);
			
			//노출 주소 00도 00시
			String[] addr = mPI.get(i).getMatAddressInfo().split("//");
			String[] addressMin = addr[1].split(" ");
			String addressMinStr = addressMin[0] + " " + addressMin[1];
			mPI.get(i).setMatAddressMin(addressMinStr);
			
			//주소 full (//제외)
			String address = mPI.get(i).getMatAddressInfo().replace("//", " "); 
			mPI.get(i).setMatAddressInfo(address);
			
			int ptNo = mPI.get(i).getPtNo();
									
			//memberInfo 뽑기
			ArrayList <InfoCategory> info = mcService.getInfo(ptNo);
			System.out.println("info: " + info);
			
			for(int l = 0; l < info.size(); l++) {
				
				if(info.get(l).getLCategory().equals("disease")) {
					 diseaseArr.add(info.get(l).getSCategory());
				}else if(info.get(l).getLCategory().equals("diseaseLevel")) {
					diseaseLevel.add(info.get(l).getSCategory());
				}else if(info.get(l).getLCategory().equals("mobilityStatus")) {
					mobilityStatus = info.get(l).getSCategory();
				}
			}			
			mPI.get(i).setDisease(diseaseArr.toString().replace("[", "").replace("]", ""));
			mPI.get(i).setDiseaseLevel(diseaseLevel.toString().replace("[", "").replace("]", ""));
			mPI.get(i).setMobilityStatus(mobilityStatus);
		}
		
		
		Member loginUser = (Member) session.getAttribute("loginUser");		
		int count = mcService.requestMatCheck(loginUser.getMemberNo(), matNo);
		
		String matCheck = "";
		if(count == 0) {
			matCheck = "N";
		}else {
			matCheck = "Y";
		}		
		
		System.out.println(mPI);
		System.out.println(matCheck);
		model.addAttribute("mPI", mPI);
		model.addAttribute("matCheck", matCheck);
		
		return "caregiverPtInfo";
	}
	
	
	//간병인 회원의 매칭 신청
	@GetMapping("requestMatching.mc")
	public String requestMatching(@RequestParam("matNo") int matNo, @RequestParam("ptCount") int ptCount, 
								RedirectAttributes re, HttpSession session) {
		
		Member loginUser = (Member) session.getAttribute("loginUser");
		
		int result = mcService.requestMatching(loginUser.getMemberNo(), matNo);
		
		if(result > 0) {
			//pt 이름 뽑기(공동간병일 경우 방 개설자)
			String matPtName = mcService.getMatPtName(matNo, ptCount);
			
			if(matPtName != null) {
				re.addAttribute("matPtName", matPtName);
				re.addAttribute("matPtCount", ptCount);
				return "redirect:caregiverMain.me";
			}else {
				throw new MemberException("간병인 매칭 신청 실패1");
			}			
			
		}else {
			throw new MemberException("간병인 매칭 신청 실패2");
		}

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@PostMapping("successPay.mc")
	public String insertPay(@ModelAttribute Pay p,
							HttpSession session) {
		
		Member loginUser = (Member)session.getAttribute("loginUser");
		//pay DB에 접근,기록
		int result1 = mcService.insertPay(loginUser,p);
		//
		if( result1 > 0) {
		
			
			
			return "redirect:patientMain.me";
			
		}else {
		
			throw new MatchingException("결제실패");
		}
	}
	
	
	
	
	
	
	
}
