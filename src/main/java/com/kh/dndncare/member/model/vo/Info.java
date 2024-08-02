package com.kh.dndncare.member.model.vo;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString 
public class Info {
	private ArrayList<String> infoService;
	private ArrayList<String> infoServiceCareer;
	private ArrayList<String> infoCareer;
	private ArrayList<String> infoDisease;
	private ArrayList<String> infoLicense;
	private ArrayList<String> infoDiseaseLevel;
	private ArrayList<String> infoGender;
	private ArrayList<String> infoNational;
	private ArrayList<String> infoAgeGroup;
}
