package com.kh.dndncare.joinMaching.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.kh.dndncare.joinMaching.model.service.JoinMachingService;
import com.kh.dndncare.joinMaching.model.vo.Hospital;

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
	public String joinMachingMainEnroll() {
		return "joinMachingEnroll";
	}
	
	
	
}
