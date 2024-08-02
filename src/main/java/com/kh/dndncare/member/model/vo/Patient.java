package com.kh.dndncare.member.model.vo;


import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;



@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Patient {
	private int ptNo;
	private int memberNo;
	private String ptName;
	private String ptGender;
	private Date ptAge;
	private int ptWeight;
	private int ptHeight;
	private String ptAddress;
	private String ptRequest;
	private Date ptUpdateDate;
	
	private List<Integer> memberInfo;
	private List<Integer> wantInfo;
	
	private ArrayList<String> disease;
	private String diseaseLevel;
	
	
}
