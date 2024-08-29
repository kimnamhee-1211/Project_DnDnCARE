package com.kh.dndncare.matching.controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.kh.dndncare.common.AgeCalculator;
import com.kh.dndncare.common.GetzipNo;
import com.kh.dndncare.matching.model.exception.MatchingException;
import com.kh.dndncare.matching.model.service.MatchingService;
import com.kh.dndncare.matching.model.vo.CareReview;
import com.kh.dndncare.matching.model.vo.Hospital;
import com.kh.dndncare.matching.model.vo.MatMatptInfo;
import com.kh.dndncare.matching.model.vo.MatMatptInfoPt;
import com.kh.dndncare.matching.model.vo.MatPtInfo;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.matching.model.vo.Pay;
import com.kh.dndncare.matching.model.vo.joinMatInfoMin;
import com.kh.dndncare.member.controller.MemberController;
import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.member.model.vo.CareGiverMin;
import com.kh.dndncare.member.model.vo.Info;
import com.kh.dndncare.member.model.vo.InfoCategory;
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class MatchingController {
	
	@Autowired
	private MatchingService mcService;
	
	private static Logger logger = LoggerFactory.getLogger(MatchingController.class);
	
	@GetMapping("publicMatching.mc")
	public String publicMatchingView(HttpSession session,Model model,
			@RequestParam(value="memberNoC", defaultValue = "0" ) int memberNoC) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		if(loginUser !=null) {			
			int memberNo = loginUser.getMemberNo();
			Patient patient = mcService.getPatient(memberNo);
			model.addAttribute("patient",patient);
			
			if(memberNoC > 0) {
				model.addAttribute("memberNoC",memberNoC);
			}							
			return "publicMatching";
			
		}
		throw new MatchingException("페이지 이동에 실패하였습니다.");
	}
	
	//2번째 페이지로 정보 전달 및 이동
	@PostMapping("publicMatching2.mc")
	public String publicMatching2(@ModelAttribute Patient patient, Model model, HttpSession session,
			@RequestParam("service") String service,
			@RequestParam(value="memberNoC", defaultValue = "0") int memberNoC,@RequestParam("hospitalName") String hospitalName) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		int memberNo = loginUser.getMemberNo();
		List<Integer> categoryList= mcService.getCategoryNo(memberNo);
		
		if(loginUser !=null && patient !=null) {
			if(categoryList !=null) {
				model.addAttribute("categoryList",categoryList);
			}
			if(memberNoC > 0) {
				model.addAttribute("memberNoC",memberNoC);
			}				
			patient.setMemberNo(memberNo);
			String request = mcService.getRequest(memberNo);
			patient.setPtRequest(request);
			session.setAttribute("tempPatient", patient);
			session.setAttribute("service", service);
			session.setAttribute("hospitalName",hospitalName);
			return "publicMatching2";
		} else {
			throw new MatchingException("다음 페이지로 이동하는중 오류가 발생하였습니다.");
		}				
	}
	
	@PostMapping("publicMatchingApply.mc")
	   public String publicMatchingApply(HttpSession session,@ModelAttribute Matching matching,@RequestParam("selectedSymptoms") String selectedSymptoms,
	                              @RequestParam("selectedMobility") String selectedMobility,@RequestParam("selectedGender") String selectedGender,
	                              @RequestParam(value="selectedDays",required =false) String selectDays,@RequestParam("selectedCareer") String selectedCareer,
	                              @RequestParam("selectedLocal") String selectedLocal, @RequestParam("selectedAge") String selectedAge,
	                              @RequestParam(value="memberNoC", defaultValue = "0") int memberNoC, RedirectAttributes re,
	                              @RequestParam("selectedDiseaseLevels") String selectedDiseaseLevels) {

	      Patient patient = (Patient)session.getAttribute("tempPatient");
	      String hospitalName = (String)session.getAttribute("hospitalName");
	      int memberNo = patient.getMemberNo();
	      String formattedDates = null;
	      int dateResult = 0;
	      
	   // 병원돌봄 선택했을 때 우편번호 넣기
	      String hospitalAddress = patient.getPtAddress();
	      String[] addressParts = hospitalAddress.split("//");
	      String zipCodefirm = addressParts[0];
	      String test1 = "";

	      if (zipCodefirm.equals("00000")) {
	          String[] testArr = addressParts[1].split(" ");
	          for (int i = 0; i < testArr.length; i++) {
	              if (testArr[i].contains("로") || testArr[i].contains("길")) {
	                  test1 = testArr[i];
	                  if (i + 1 < testArr.length && testArr[i + 1].matches("\\d+")) {
	                      test1 += " " + testArr[i + 1];
	                  }
	                  break;
	              }
	          }
	          
	          String zipCode = GetzipNo.ApiExplorer(test1);
	          NodeList zipNoList = null;
	          try {
	              DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	              DocumentBuilder builder = factory.newDocumentBuilder();

	              // XML 문자열을 Document로 변환
	              ByteArrayInputStream input = new ByteArrayInputStream(zipCode.getBytes(StandardCharsets.UTF_8));
	              Document document = builder.parse(input);

	              // <newAddressListAreaCd> 요소의 zipNo 추출
	              zipNoList = document.getElementsByTagName("zipNo");

	          } catch (Exception e) {
	              e.printStackTrace();
	          }
	          
	          // zipNo 값을 추출
	          String zipNo = null;
	          if (zipNoList != null && zipNoList.getLength() > 0) {
	              Element zipNoElement = (Element) zipNoList.item(0);
	              zipNo = zipNoElement.getTextContent();
	          }
	          
	          // zipNo가 null이 아니면 fullAddress의 우편번호 부분을 대체
	          if (zipNo != null && !zipNo.isEmpty()) {
	              addressParts[0] = zipNo;
	              hospitalAddress = String.join("//", addressParts);
	              patient.setPtAddress(hospitalAddress);
	          }
	          
	          int hospitalNo = mcService.getHospitalNo(hospitalName);
	          
	          if (hospitalNo != 0) {
	              matching.setHospitalNo(hospitalNo);
	          } else {
	              int insertResult = mcService.insertHospital(hospitalName, hospitalAddress);
	              if (insertResult > 0) {
	                  hospitalNo = mcService.getHospitalNo(hospitalName);
	                  matching.setHospitalNo(hospitalNo);
	              } else {
	            	  throw new MemberException("다시해라");
	              }
	          }
	      } 

	      System.out.println("최종 주소: " + patient.getPtAddress());
		  
	        // member_Info에 들어갈 값 맵에 넣기
	      	Map<String,Object> memberInfoParams= new HashMap<>();
	      
	      	memberInfoParams.put("symptoms", Arrays.stream(selectedSymptoms.split(","))
	                                     .map(Integer::parseInt)
	                                     .collect(Collectors.toList()));
	      	memberInfoParams.put("selectedDiseaseLevels", Arrays.stream(selectedDiseaseLevels.split(","))
					                    .map(Integer::parseInt)
					                    .collect(Collectors.toList()));
	      	memberInfoParams.put("mobility", selectedMobility != null ? Integer.parseInt(selectedMobility) : null);
	      	memberInfoParams.put("memberNo",memberNo);
	      	memberInfoParams.put("service", session.getAttribute("service").equals("hospital") ? 1 : 2);
	  
	      	
	      	
	      	// memberInfo 삭제 및 인설트
	      	int deleteMemberInfoResult = mcService.deleteMemberInfo(memberInfoParams);
	        int memberInfoResult = mcService.insertMemberInfo(memberInfoParams);
	        

	       
	      	
	        // want_info에 들어갈 값 맵에 넣기
	        Map<String, Object> wantInfoparams = new HashMap<>();
	        wantInfoparams.put("gender", selectedGender != null ? Integer.parseInt(selectedGender) : null);
	        wantInfoparams.put("career", selectedCareer != null ? Integer.parseInt(selectedCareer) : null);
	        wantInfoparams.put("local", selectedLocal != null ? Integer.parseInt(selectedLocal) : null);
	        wantInfoparams.put("age", selectedAge != null ? Integer.parseInt(selectedAge) : null);
	        wantInfoparams.put("memberNo",memberNo);
	        
	        
	        
	        //원래있던 memberNo에 해당하는 want-info 삭제후 want_info insert
	        int deleteWantInfo = mcService.deleteWantInfo(memberNo);
	        int wantInfoResult = mcService.insertWantInfo(wantInfoparams);

	        	      
	      if(patient != null && matching != null && selectedSymptoms !=null
	            && selectedMobility !=null && selectedGender !=null
	            && selectedCareer !=null && selectedLocal !=null && selectedAge !=null) {
	         
	         Patient previousPatient = mcService.getPatient(memberNo);
	         int ptNo = previousPatient.getPtNo();
	         patient.setPtNo(ptNo);
	         //patient 정보 update
	         int patientResult = mcService.updatePatient(patient);


	         matching.setPtCount(1);
	         if(selectDays == null) {
	            matching.setMatMode(1);
	         } else {
	            matching.setMatMode(2);
	         }
	         
	         //Matching 정보 삽입
	         System.out.println("종규 매칭 정보 확인하기 : "+matching);
	         if (session.getAttribute("service").equals("hospital")) {
	        	 	matching.setHospitalNo(99);
		         } else {
		        	 matching.setHospitalNo(99);
		    	    
		         }
	         int matchingResult = mcService.enrollMatching(matching);
	         
	         int matNo = matching.getMatNo();
	         //System.out.println("종규 매칭 정보 확인하기2 : "+matNo);
	         //System.out.println("종규 매칭 정보 확인하기 : "+matching);
	         //System.out.println("종규 매칭 정보 확인하기 : "+matching.getMatNo());
	         	               	         
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
	         if (session.getAttribute("service").equals("hospital")) {
        	    matPtInfo.setService("병원돌봄");
	         } else {
	    	    matPtInfo.setService("가정돌봄");
	    	    
	         }
	         
	         matPtInfo.setMatAddressInfo(patient.getPtAddress());
	         matPtInfo.setMatRequest(patient.getPtRequest());
	         matPtInfo.setGroupLeader("N");

	         int ptInfoResult = mcService.enrollMatPtInfo(matPtInfo);
	         
	         
	         
	         //채팅방 생성 (간병인 후기 보기에서 매칭방 신청하고 바로 채팅방 생성할때)
	         
	         int finalResult = wantInfoResult + patientResult + ptInfoResult + dateResult + matchingResult + deleteWantInfo + deleteMemberInfoResult + memberInfoResult;
	         
	         //모달용
	         String result = "insert";
	         
	         //공개 매칭 신청 시
	         if(finalResult != 0) {
	        	 
		         //환자 -> 간병인 정보보기 ->  매칭 신청했을 경우 macthing 테이블에 간병인 memberNo 넣기
		        if(memberNoC > 0) {
		        	int updateMatCResult = mcService.updateMatC(matNo, memberNoC);
		        	//모달용
		        	if(updateMatCResult > 0) {		        	
			        	String matCName = mcService.getNameC(memberNoC);
			    		re.addAttribute("matCName", matCName);
			    		result = "request";
		        	}
		        }
		        
	        	session.removeAttribute("tempPatient"); // 세션에 담아놨던 patient 객체 삭제 
	        	session.removeAttribute("service");
	        	
	        	//모달용
	        	re.addAttribute("result", result);
	        	
	        	//환자 메인페이지로
	            return "redirect:patientMain.me";
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
		
		for(MatMatptInfo i : list) {
			i.setMatAddressInfo(i.getMatAddressInfo().replace("//", " "));		
		}
		
		
		
		
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
		
		//하나만 확인해보자
		//logger.info(" 잘 들어가지니? ");
		
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
		int ho = mcService.getHospitalNo(hospital.getHospitalName());
		if(ho < 1) {
			
			//우편번호 삽입
			String test2 = "";
			String[] testArr =  hospital.getHospitalAddress().split(" ");	
			for(int i= 0; i < testArr.length; i ++) {
				if(testArr[i].contains("로") || testArr[i].contains("길")) {
					test2 = testArr[i];
					if(i+1 < testArr.length && testArr[i+1].matches("\\d+")) {
						 test2 += " " + testArr[i + 1];
					}					
	 			}
			}
					
			String zipCode = GetzipNo.ApiExplorer(test2);
			NodeList zipNoList = null;
			try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	            DocumentBuilder builder = factory.newDocumentBuilder();

	            // XML 문자열을 Document로 변환
	            ByteArrayInputStream input = new ByteArrayInputStream(zipCode.getBytes(StandardCharsets.UTF_8));
	            Document document = builder.parse(input);
	
	            // <newAddressListAreaCd> 요소의 zipNo 추출
	            zipNoList = document.getElementsByTagName("zipNo");
            
			} catch (Exception e) {
				e.printStackTrace();
			}
            
            // zipNo 값을 출력
			String zipNo = null;
            for (int i = 0; i < zipNoList.getLength(); i++) {
                Element zipNoElement = (Element) zipNoList.item(i);
                zipNo = zipNoElement.getTextContent();
            }
            
            String addZipNo = "";
            if(zipNo != null) {            	
            	addZipNo = zipNo + "//" + hospital.getHospitalAddress();
            }else {
            	addZipNo =  "00000//" + hospital.getHospitalAddress();
            }
            hospital.setHospitalAddress(addZipNo);  
            
			int result = mcService.enrollHospital(hospital);
			
			if(result > 0) {
				jm.setHospitalNo(hospital.getHospitalNo());
			}
			
		}else {
			jm.setHospitalNo(ho);
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
			
			session.setAttribute("logMatNo",jm.getMatNo() );
			session.setAttribute("logMatService","공동간병");
			
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
		
		jmMatMatptInfo.setMatAddressInfo(jmMatMatptInfo.getMatAddressInfo().replace("//", " "));
				
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
				
		HashMap<String, Object> jmMacPt = new HashMap<String, Object>();
		jmMacPt.put("jmMatMatptInfo", jmMatMatptInfo);
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
								@RequestParam(value="hospitalName", required=false) String hospitalName, @RequestParam(value="hospitalAddress", required=false) String hospitalAddress,
								RedirectAttributes re, @RequestParam(value="beforePage", required=false) String beforePage) {
		
		System.out.println("공동간병 현황" + matNo);
		
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
			if(beforePage != null && beforePage.equals("my")) {
				return "redirect:goMyMatchingP.mc";
			}else {
				re.addAttribute("hospitalName", hospitalName);
				re.addAttribute("hospitalAddress", hospitalAddress);
				re.addAttribute("msg", "공동간병 참여 취소가 완료되었습니다.");
				return "redirect:joinMatching.jm";
			}
			
		}else {
			throw new MemberException("공동간병 그룹 참여 취소 실패");
		}
	}
	

	
	@GetMapping("reviewDetail.mc")												
	public String getMethodName(HttpSession session, Model model, @RequestParam("memberNo") Integer memberNo,
								@RequestParam(value="matNo", required = false) Integer matNo, 
								@RequestParam(value="from", required = false) String beforePage) {		
		
		// 후기내역
		ArrayList<CareReview> reviewList = mcService.selectReviewList(memberNo);
		
		// 후기개수
		int reviewCount = mcService.reviewCount(memberNo);
		
		// 평점
		Double avgReviewScore = mcService.avgReviewScore(memberNo);
		avgReviewScore= (avgReviewScore != null) ? avgReviewScore : 0.0;
		
		// 간병인 소개
		CareGiver caregiverIntro = mcService.selectIntro(memberNo);
		AgeCalculator ageCalculator = new AgeCalculator();
		int age = ageCalculator.calculateAge(caregiverIntro.getMemberAge());
		caregiverIntro.setAge(age);
		System.out.println("남희님이 셋팅해준 나이 " + caregiverIntro.getAge());
		
		//통계용
		ArrayList<MatMatptInfo> serviceList = mcService.serviceList(memberNo);
		if(serviceList != null) {
			System.out.println("서비스1"+serviceList);
			model.addAttribute("serviceList", serviceList);
		}
		System.out.println("서비스2"+serviceList);
				
		
		
		
		// 여기서 로그 테스트
		// 기존 로그의 데이터
		Set<String> loggedMatNos = new HashSet<>();

		// 기존 로그 파일의 모든 메시지를 읽어와 Set에 저장
		try (BufferedReader br = new BufferedReader(new FileReader("C:\\logs\\matching\\matchingDisease.log"))) {
		    String line;
		    while ((line = br.readLine()) != null) {
	            String loggedList= line.substring(line.indexOf("_")+1).trim(); 
		    	String loggedMatNo = loggedList.split("//")[0];
		        loggedMatNos.add(loggedMatNo);  
		        System.out.println("기존의 matNo " + loggedMatNo);
		    }
		} catch (IOException e) {
		    e.printStackTrace();
		}
		
		// 매칭번호, 시작시간, 종료시간, 나이, 성별, 질환
		ArrayList<Matching> matPatientInfoLists = mcService.matPatientList(memberNo);
		for (Matching matPatientInfoList : matPatientInfoLists) {
			System.out.println("매칭번호"+matPatientInfoList.getMatNo());
			System.out.println("시작시간"+matPatientInfoList.getBeginDt());
			System.out.println("종료시간"+matPatientInfoList.getEndDt());
			System.out.println("나이"+matPatientInfoList.getPtAge());
			System.out.println("성별"+matPatientInfoList.getMemberGender());
			System.out.println("Scategory"+matPatientInfoList.getSCategory());
			System.out.println("회원번호" + matPatientInfoList.getMemberNo());
			
			// 나이 계산
			matPatientInfoList.setAge(AgeCalculator.calculateAge(matPatientInfoList.getPtAge()));
			
			
			// 비교가 계속 안되니까 String 타입으로 바꿔서
			String strMatNo = (matPatientInfoList.getMatNo()+"").trim();
			
			// 날짜도 비교
			Date today = new Date(age);
				// 이미 로그된 matNo인지 확인
			    if (!loggedMatNos.contains(strMatNo)) {
			       String logInfo = matPatientInfoList.getMatNo() + "//" + matPatientInfoList.getAge() + "//" + matPatientInfoList.getMemberGender() + "//" + matPatientInfoList.getSCategory() + "//" + matPatientInfoList.getMemberNo();
			       System.out.println("로그에 저장할 정보"+logInfo);
			        
			     logger.info(logInfo);
			   
			    }
        }
		
		
		// 간병인 정보(국적, 경력, 자격증)
		ArrayList<InfoCategory> caregiverInfo = mcService.getCaregiverInfo(memberNo);
		HashMap<String, Object> caregiverInfoList = new HashMap<String, Object>();
		boolean hasLicense = false;
		for(InfoCategory info:caregiverInfo) {
			switch(info.getLCategory()) {
			case "career" : caregiverInfoList.put("career", info.getSCategory()); break;
			case "license" : 
				if (!caregiverInfoList.containsKey("license")) {
                caregiverInfoList.put("license", new ArrayList<String>());
				}
            ((ArrayList<String>)caregiverInfoList.get("license")).add(info.getSCategory());
            hasLicense = true;
            break;
			}
				
		}
		if (!hasLicense) {
		    caregiverInfoList.put("license", null);
		}
		System.out.println("이전페이지"+beforePage);
		System.out.println("매칭"+caregiverInfoList);
		
		
		//공동매칭일 경우 loginUser가 방장인지 아닌지 알아보기
		String leader = "N";
		if(matNo != null){
			leader = "Y";
			int matNo2 = (int)matNo;
			int ptCount = mcService.getPtCount(matNo2);			
			if(ptCount > 1){
				//loginUser가 그룹 리더인지 아닌지 확인
				Member loginUser = (Member)session.getAttribute("loginUser");	
				int loginPtNo = mcService.getPtNo(loginUser.getMemberNo());
				String gl = mcService.getGroupLeader(matNo2, loginPtNo);
				if(gl.equals("N")) {
					leader = "N";
				}
			}
		}
				
		model.addAttribute("leader", leader);
		model.addAttribute("memberNo", memberNo);
		model.addAttribute("matNo", matNo);
		model.addAttribute("beforePage", beforePage);
		model.addAttribute("reviewList", reviewList);
		model.addAttribute("reviewCount", reviewCount);
		model.addAttribute("avgReviewScore",avgReviewScore);
		model.addAttribute("caregiverIntro",caregiverIntro);
		model.addAttribute("caregiverInfoList", caregiverInfoList);
		model.addAttribute("age",age);
		return "reviewDetail";
	}
	
	
	
	//공동간병 참여자 퇴장
	@PostMapping("walkoutJoinMatching.jm")
	public String walkoutJoinMatching(@RequestParam("matNo") int matNo, @RequestParam("ptNo") int ptNo,
									@RequestParam(value="hospitalName", required=false) String hospitalName, @RequestParam(value="hospitalAddress", required=false) String hospitalAddress,
									@RequestParam(value="before", required=false) String before, RedirectAttributes re) {
		int result = mcService.delMatPtInfo(matNo, ptNo);
		
		if(result > 0) {
			
			if(before != null && before.equals("my")) {
				return "redirect:goMyMatchingP.mc";
			}else {
				re.addAttribute("hospitalName", hospitalName);
				re.addAttribute("hospitalAddress", hospitalAddress);
				re.addAttribute("msg", "공동간병 참여자 퇴장이 완료되었습니다.");
				return "redirect:joinMatching.jm";
			}
			
		}else {
			throw new MemberException("공동간병 참여자 퇴장 실패");
		}


	}
	
	//후기 작성
	@PostMapping("writeReview.mc")
	public String insertReview(@RequestParam("memberNo") int memberNo, @RequestParam(value="reviewScore", defaultValue = "10") int reviewScore, @RequestParam(value="matNo",required = false) int matNo , @RequestParam("reviewContent") String reviewContent, HttpSession session, RedirectAttributes ra) {
		int loginUserNo = ((Member)session.getAttribute("loginUser")).getMemberNo();
		int ptNo = mcService.getPtNo(loginUserNo);
		HashMap<String, Object> map = new HashMap<String, Object>();
		// 환자번호
		map.put("ptNo", ptNo);
		// 후기점수
		map.put("reviewScore", reviewScore);
		// 후기내용
		map.put("reviewContent", reviewContent);
		
		// 매칭번호
		map.put("matNo", matNo);
		
		// 간병인 고유번호
		map.put("memberNo", memberNo);
		
		System.out.println("후기작성데이터"+map);
		int result = mcService.insertReview(map);
		if(result>0) {
			return "redirect:myInfoMatchingReview.me";
		}else {
			throw new MatchingException("후기 작성 실패");
		}
	}
				
	//비동기로 환자측에서 결제할때 간병인 정보 가져오기
	@GetMapping("payInfo.mc")
	@ResponseBody
	public void payInfo(@RequestParam("matNo") int matNo,HttpServletResponse response,HttpSession session) {
		// 보낼때, 매칭번호가 필수다
		
		Member m = (Member)session.getAttribute("loginUser");
		MatMatptInfo matInfo = mcService.selectMatching(matNo);
		MatMatptInfo matPtInfo = mcService.selecMatPtInfo(matNo,m.getMemberNo());
		matInfo.setPtNo(matPtInfo.getPtNo());
		matInfo.setAntePay(matPtInfo.getAntePay());
		matInfo.setService(matPtInfo.getService());
		matInfo.setMatAddressInfo(matPtInfo.getMatAddressInfo());
		matInfo.setMatRequest(matPtInfo.getMatRequest());
		matInfo.setDeposit(matPtInfo.getDeposit());
		matInfo.setGroupLeader(matPtInfo.getGroupLeader());
		
		String hourly = null;
		hourly = mcService.selectMatDate(matNo);
		if(hourly != null) {
			int hourly2 = hourly.split(",").length;
			matInfo.setHourly(hourly2);
		}

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
		
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		ArrayList<MatMatptInfoPt> mPI = mcService.matPtInfoToCaregiver(matNo);
		ArrayList<String> diseaseArr = new ArrayList<String>();
		ArrayList<String> diseaseLevel = new ArrayList<String>();
		String mobilityStatus = null;
		
		//matching테이블에 간병인 memberNo들어왔는지 확인 => 매칭 신청/승낙 구분용
		Integer MatMemberNo = mcService.getMatMemberNo(matNo);
		if(MatMemberNo != null && MatMemberNo == loginUser.getMemberNo()) {
			model.addAttribute("MatMemberNo", MatMemberNo);
		}

		
		for(MatMatptInfoPt m: mPI) {
			//나이 계산
			int ptRealAge = AgeCalculator.calculateAge(m.getPtAge());
			m.setPtRealAge(ptRealAge);
			
			//노출 주소 00도 00시
			String[] addr = m.getMatAddressInfo().split("//");
			String[] addressMin = addr[1].split(" ");
			String addressMinStr = addressMin[0] + " " + addressMin[1];
			m.setMatAddressMin(addressMinStr);

			//종규 맵에 넣을 주소 하나추가
			m.setMatAddressMap(m.getMatAddressInfo().split("//")[1]);
			System.out.println("확인하기종규" + m.getMatAddressMap());
			//주소 full (//제외)
			String address = m.getMatAddressInfo().replace("//", " "); 
			m.setMatAddressInfo(address);
			
			//memberInfo 뽑기
			ArrayList <InfoCategory> info = mcService.getInfo(m.getPtNo());
			System.out.println("info: " + info);
			
			for(InfoCategory i : info) {
				if(i.getLCategory().equals("disease")) {
					 diseaseArr.add(i.getSCategory());
				}else if(i.getLCategory().equals("diseaseLevel")) {
					diseaseLevel.add(i.getSCategory());
				}else if(i.getLCategory().equals("mobilityStatus")) {
					mobilityStatus = i.getSCategory();
				}
			}			
			m.setDisease(diseaseArr.toString().replace("[", "").replace("]", ""));
			m.setDiseaseLevel(diseaseLevel.toString().replace("[", "").replace("]", ""));
			m.setMobilityStatus(mobilityStatus);
		}
	
		model.addAttribute("mPI", mPI);
		//채팅테스트용 matNo
		model.addAttribute("matNo",matNo);
		//종규 주소[1]만 삽입하기
		
		
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
				re.addAttribute("result", "request");
				return "redirect:caregiverMain.me";
			}else {
				throw new MemberException("간병인 매칭 신청 실패1");
			}			
			
		}else {
			throw new MemberException("간병인 매칭 신청 실패2");
		}

	}
	
	//나의 매칭현황(간병인)
	@GetMapping("goMyMatchingC.mc")
	public String goMyMatchingC(HttpSession session, Model model, 
			@RequestParam(value="before", required=false) String before) {
		
		Member loginUser = (Member)session.getAttribute("loginUser");
		ArrayList<MatMatptInfoPt> myMatchingAll = mcService. getMyMatching(loginUser.getMemberNo());
		System.out.println("myMatchingAll : " + myMatchingAll);
		
		
		ArrayList<MatMatptInfoPt> myMatching = new ArrayList<MatMatptInfoPt>();
		ArrayList<MatMatptInfoPt> myMatchingW = new ArrayList<MatMatptInfoPt>();
		ArrayList<MatMatptInfoPt> myRequestMatPt = new ArrayList<MatMatptInfoPt>();
		
		LocalDate today = LocalDate.now();		
		
		for(MatMatptInfoPt i : myMatchingAll) {
			
			//노출 나이 set
			int realAge = AgeCalculator.calculateAge(i.getPtAge());
			i.setPtRealAge(realAge);
						
			//매칭 진행 중
			if(i.getMatConfirm().equals("Y")) {
		        Date endDt = i.getEndDt(); 
		        LocalDate endLocalDate = endDt.toLocalDate();		        
		        if (endLocalDate.isAfter(today)) {
		            myMatching.add(i);
		        }
			}
			
			//매칭 결제 대기중
			if(i.getMatConfirm().equals("W")) {
		        Date beginDt = i.getBeginDt(); 
		        LocalDate beginLocalDate = beginDt.toLocalDate();		        
		        if (beginLocalDate.isAfter(today)) {
		        	myMatchingW.add(i);
		        }
			}
			
			//매칭신청 받은 내역
			if(i.getMatConfirm().equals("N")) {
		        Date beginDt = i.getBeginDt(); 
		        LocalDate beginLocalDate = beginDt.toLocalDate();		        
		        if (beginLocalDate.isAfter(today)) {
		        	myRequestMatPt.add(i);
		        }
			}	
		}		
		
		//매칭 신청 내역
		ArrayList<MatMatptInfoPt> myRequestMat= mcService.getMyRequestMat(loginUser.getMemberNo());
		for(MatMatptInfoPt i : myRequestMat) {
			
			//노출 나이 set
			int realAge = AgeCalculator.calculateAge(i.getPtAge());
			i.setPtRealAge(realAge);
		}
			
		model.addAttribute("myMatching", myMatching);
		model.addAttribute("myMatchingW", myMatchingW);
		model.addAttribute("myRequestMat", myRequestMat);
		model.addAttribute("myRequestMatPt", myRequestMatPt);
		model.addAttribute("loginUserName", loginUser.getMemberName());
		if(before != null && before.equals("myPage")) {
			return "myMatC";
		}else {
			return "myMatchingC";
		}
	}
	
		//이거 어느꺼찌? 종규 수정 남흰ㅁ한테물어보기
	@PostMapping(value="getMatPtToMatNo.mc", produces="application/json; charset=UTF-8")
	@ResponseBody
	public void getMatPtToMatNo(@RequestParam("matNo") int matNo, HttpServletResponse response) {
		
		ArrayList<MatMatptInfoPt> matInfo = mcService.matPtInfoToCaregiver(matNo);
		System.out.println(matInfo);
		
		ArrayList<String> diseaseArr = new ArrayList<String>();
		ArrayList<String> diseaseLevel = new ArrayList<String>();
		String mobilityStatus = null;
		
		for(MatMatptInfoPt i : matInfo) {
			
			//노출 주소 set
			String[] addArr = i.getMatAddressInfo().split("//");
			String[] addArrMin = addArr[1].split(" ");
			String addMin = addArrMin[0] + " " +  addArrMin[1];
			i.setMatAddressMin(addMin);
			
			//노출 나이 set
			int realAge = AgeCalculator.calculateAge(i.getPtAge());
			i.setPtRealAge(realAge);
			
			//상세 주소 set
			String add = i.getMatAddressInfo().replace("//", " ");
			i.setMatAddressInfo(add);
			//날짜 노출 예쁘게
			if(i.getMatDate() != null) {
				String date = i.getMatDate().replace(",", ", ");
				i.setMatDate(date);
			}
			
			//환자 정보 set
			ArrayList <InfoCategory> info = mcService.getInfo(i.getPtNo());
			System.out.println("info: " + info);
			
			for(InfoCategory m : info) {
				if(m.getLCategory().equals("disease")) {
					 diseaseArr.add(m.getSCategory());
				}else if(m.getLCategory().equals("diseaseLevel")) {
					diseaseLevel.add(m.getSCategory());
				}else if(m.getLCategory().equals("mobilityStatus")) {
					mobilityStatus = m.getSCategory();
				}
			}			
			i.setDisease(diseaseArr.toString().replace("[", "").replace("]", ""));
			i.setDiseaseLevel(diseaseLevel.toString().replace("[", "").replace("]", ""));
			i.setMobilityStatus(mobilityStatus);
		}
		
		System.out.println(matInfo);
		
		response.setContentType("application/json; charset=UTF-8");
		GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd");
		Gson gson = gb.create();
		
		try {
			gson.toJson(matInfo, response.getWriter());
		} catch (JsonIOException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	//간병인의 매칭 승낙
	@GetMapping("matchingApproveC.mc")
	public String matchingapproveC(@RequestParam("matNo") int matNo, @RequestParam("ptCount") int ptCount, 
			RedirectAttributes re, HttpSession session) {
		
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		int result = mcService.matchingApproveC(matNo, loginUser.getMemberNo());
		
		if(result > 0) {		
			String matPtName = mcService.getMatPtName(matNo, ptCount);			
			re.addAttribute("matPtName", matPtName);
			re.addAttribute("matPtCount", ptCount);
			re.addAttribute("result", "approve");
			return "redirect:caregiverMain.me";
		}else {
			throw new MemberException("간병인 매칭 승낙 실패");
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
	
	// 후기삭제
	@PostMapping("deleteReview.mc")
	public String deleteReview(@RequestParam("reviewNo") int reviewNo) {
		int result = mcService.deleteReivew(reviewNo);
		if(result > 0) {
			return "redirect:myInfoMatchingReview.me";
		}else {
				throw new MatchingException("후기 삭제 실패");
			}
	}
	
	// 후기 수정
	@PostMapping("updateReview.mc")
	public String updateReview(@ModelAttribute CareReview cr) {
		System.out.println("updateReview"+cr);
		
		int result = mcService.updateReview(cr);
		if(result > 0) {
			return "redirect:myInfoMatchingReview.me";
		}else {
			throw new MatchingException("후기 수정 실패");
		}
	}
	
	
	//나의 매칭현황(환자)
	@GetMapping("goMyMatchingP.mc")
	public String goMyMatchingP(HttpSession session, Model model, 
			@RequestParam(value="before", required=false) String before) {
		
		Member loginUser = (Member)session.getAttribute("loginUser"); 
			
		int loginPt = mcService.getPtNo(loginUser.getMemberNo());
		
		//매칭 내역 (진행 + 결제대기 + 환자자 신청)
		ArrayList<CareGiverMin> myMatchingAll = mcService. getMyMatchingP(loginPt);
				
		ArrayList<CareGiverMin> myMatching = new ArrayList<CareGiverMin>();
		ArrayList<CareGiverMin> myMatchingW = new ArrayList<CareGiverMin>();
		ArrayList<CareGiverMin> myRequestMatC = new ArrayList<CareGiverMin>();
		
		LocalDate today = LocalDate.now();		
		
		for(CareGiverMin i : myMatchingAll) {
			
			//노출 나이 set
			int realAge = AgeCalculator.calculateAge(i.getMemberAge());
			i.setAge(realAge);

			
			if(i.getPtCount() == 1) {
				i.setGroupLeader("Y");
			}			
			
						
			//매칭 진행 중
			if(i.getMatConfirm().equals("Y")) {
		        Date endDt = i.getEndDt(); 
		        LocalDate endLocalDate = endDt.toLocalDate();		        
		        if (endLocalDate.isAfter(today)) {
		            myMatching.add(i);
		        }
			}
			
			//매칭 결제 대기중
			if(i.getMatConfirm().equals("W")) {
		        Date beginDt = i.getBeginDt(); 
		        LocalDate beginLocalDate = beginDt.toLocalDate();		        
		        if (beginLocalDate.isAfter(today)) {
		        	myMatchingW.add(i);
		        }
			}
			
			//매칭신청한 내역
			if(i.getMatConfirm().equals("N")) {
		        Date beginDt = i.getBeginDt(); 
		        LocalDate beginLocalDate = beginDt.toLocalDate();		        
		        if (beginLocalDate.isAfter(today)) {
		        	myRequestMatC.add(i);
		        }
			}	
		}				
		
		//매칭 신청 받은 내역 (간병인이 나(환자)를 신청)
		ArrayList<CareGiverMin> myMatchingMat = mcService.getMyMatchingPN(loginPt);
		for(CareGiverMin i : myMatchingMat) {
			
			//노출 나이 set
			int realAge = AgeCalculator.calculateAge(i.getMemberAge());
			i.setAge(realAge);
			
			if(i.getPtCount() == 1) {
				i.setGroupLeader("Y");
			}	
			
		}
		
		//공동간병 참여중인 내역
		ArrayList<joinMatInfoMin> myJoinMat = mcService.getMyJoinMat(loginPt);	
		
		model.addAttribute("myMatching", myMatching);
		model.addAttribute("myMatchingW", myMatchingW);
		model.addAttribute("myRequestMatC", myRequestMatC);
		model.addAttribute("myMatchingMat", myMatchingMat);
		model.addAttribute("myJoinMat", myJoinMat);
		
		model.addAttribute("loginUserName", loginUser.getMemberName());
		model.addAttribute("loginPt", loginPt);
		
		if(before != null && before.equals("myPage")) {
			return "myMatP";
		}else {
			return "myMatchingP";	
		}
	
	}
	
	//환자 매칭 승낙
	@GetMapping("matchingApproveP.mc")
	public String matchingApproveP(@RequestParam("matNo") int matNo, @RequestParam("memberNo") int memberNo,
									RedirectAttributes re) {
		
		int result = mcService.matchingApproveP(matNo, memberNo);
		
		
		//매칭 간병인 이름 얻어오기
		String matCName = mcService.getNameC(memberNo);
		
		if(result > 0) {
			re.addAttribute("matCName", matCName);
			re.addAttribute("result", "approve");
			return "redirect:patientMain.me";
		}else {
			throw new MatchingException(" 환자 매칭 승낙 실패");
		}

	}
	
	
	//환자가 이미 신청한 내역인지 확인
	@GetMapping(value="requestCheck.mc", produces="application/json; charset=UTF-8")
	@ResponseBody
	public String requestCheck(@RequestParam("matNo") int matNo) {
		
		int CheckMatMemNo = mcService.CheckMatMemNo(matNo);
		if(CheckMatMemNo > 0) {
			return "aready";
		}else {
			return "none";
		}
	
	}
	
	//환자 현황 만드는 중
	@PostMapping(value="getMatCToMatNoMemNo.mc", produces="application/json; charset=UTF-8")
	@ResponseBody
	public void getMatCToMatNoMemNo(@RequestParam("matNo") int matNo, @RequestParam("memberNo") int memberNo, HttpServletResponse response) {
		
		ArrayList<MatMatptInfoPt> matInfo = mcService.matPtInfoToCaregiver(matNo);
		
		for(MatMatptInfoPt i : matInfo) {
			
			//노출 주소 set
			String[] addArr = i.getMatAddressInfo().split("//");
			String[] addArrMin = addArr[1].split(" ");
			String addMin = addArrMin[0] + " " +  addArrMin[1];
			i.setMatAddressMin(addMin);
			
			//상세 주소 set
			String add = i.getMatAddressInfo().replace("//", " ");
			i.setMatAddressInfo(add);
			//날짜 노출 예쁘게
			if(i.getMatDate() != null) {
				String date = i.getMatDate().replace(",", ", ");
				i.setMatDate(date);
			}
		}
		
		
		// 후기내역
		ArrayList<CareReview> reviewList = mcService.selectReviewList(memberNo);
		
		// 후기개수
		int reviewCount = mcService.reviewCount(memberNo);
		
		// 평점
		Double avgReviewScore = mcService.avgReviewScore(memberNo);
		avgReviewScore= (avgReviewScore != null) ? avgReviewScore : 0.0;
		
		// 간병인 소개
		CareGiver caregiverIntro = mcService.selectIntro(memberNo);	
		System.out.println(caregiverIntro);		
		
		//남희 : 나이세팅
		int age = AgeCalculator.calculateAge(caregiverIntro.getMemberAge());
		caregiverIntro.setAge(age);
		System.out.println("남희님이 셋팅해준 나이 " + caregiverIntro.getAge());
		
		
		
		
		// 간병인 정보(국적, 경력, 자격증)
		ArrayList<InfoCategory> caregiverInfo = mcService.getCaregiverInfo(memberNo);
		HashMap<String, Object> caregiverInfoList = new HashMap<String, Object>();
		for(InfoCategory info:caregiverInfo) {
			switch(info.getLCategory()) {
			case "career" : caregiverInfoList.put("career", info.getSCategory()); break;
			case "license" : 
				if (!caregiverInfoList.containsKey("license")) {
                caregiverInfoList.put("license", new ArrayList<String>());
				}
				((ArrayList<String>)caregiverInfoList.get("license")).add(info.getSCategory().replace("[", "").replace("]", ""));
			}
		}
		
		
		//공동간병방 찾기
		//pt,metpt, mat 찾고
		//간병인이 있는 경우 와 없는 경우??
				
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		

		
		System.out.println(matInfo);
		System.out.println(caregiverIntro);
		Map<String, Object> responseMap = new HashMap<>();
		responseMap.put("matInfo", matInfo);
		responseMap.put("reviewList", reviewList);
		responseMap.put("reviewCount", reviewCount);
		responseMap.put("avgReviewScore", avgReviewScore);
		responseMap.put("caregiverIntro", caregiverIntro);
		responseMap.put("caregiverInfoList", caregiverInfoList);

	
		response.setContentType("application/json; charset=UTF-8");
		GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd");
		Gson gson = gb.create();
		
		try {
			gson.toJson(responseMap, response.getWriter());
		} catch (JsonIOException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	//환자 매칭 신청 취소
	@GetMapping("matchingCancelP.mc")
	public String matchingCancelP(@RequestParam("matNo") int matNo, @RequestParam("memberNo") int memberNo,
									RedirectAttributes re) {
		
		int result = mcService.matchingCancelP(matNo);
				
		//매칭 간병인 이름 얻어오기
		String matCName = mcService.getNameC(memberNo);
		
		if(result > 0) {
			re.addAttribute("matCName", matCName);
			re.addAttribute("result", "cancell");
			return "redirect:patientMain.me";
		}else {
			throw new MatchingException(" 환자 매칭 신청 취소 실패");
		}

	}
	
	
	
	
	//간병인 매칭 신청 취소
	@GetMapping("matchingCancelC.mc")
	public String matchingCancelC(@RequestParam("matNo") int matNo, @RequestParam("ptCount") int ptCount, 
								RedirectAttributes re, HttpSession session) {
		
		int memberNo = ((Member)session.getAttribute("loginUser")).getMemberNo();		
		int result = mcService.matchingCancelC(matNo, memberNo);

		if(result > 0) {		
			String matPtName = mcService.getMatPtName(matNo, ptCount);			
			re.addAttribute("matPtName", matPtName);
			re.addAttribute("matPtCount", ptCount);
			re.addAttribute("result", "cancell");
			return "redirect:caregiverMain.me";
		}else {
			throw new MemberException("간병인 매칭 신청 취소 실패");
		}

	}
	
	

	//간병인 금액 받기
	@GetMapping("insertPayTransfer.mc")
	public String insertPayTransfer(HttpSession session) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		ArrayList<Pay> pArr = mcService.selectPayTransfer(loginUser.getMemberNo()); 	///matNo를 전부 가져와야한다.왜냐? 공동간병 거래한사람도 있을꺼잖아
		System.out.println("페이정보이건맞냐?" + pArr);
		int result = 0;
		int money = 0;
		if(!pArr.isEmpty()) {
			for(Pay p : pArr) {
				result += mcService.insertPayTransfer(loginUser,p);
				money += p.getPayMoney();
				int result2 = mcService.updatePayTransfer(p);
			} 
		}
		
		
		
		
		return "redirect:caregiverMain.me";
	}
	
	
	
	
	
}
