package com.kh.dndncare.member.model.vo;

import java.sql.Date;
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
	private int memberNo; // MEMBER.MEMBER_NO
	private String ptName;
	private String ptGender; // PT_GENDER
	private Date ptAge; // PT_AGE
	private int ptWeight; // PT_WEIGHT
	private int ptHeight; // PT_HEIGHT
	private String ptService; // PT_SERVICE
	private String ptAddress; // PT_ADDRESS
	private String ptRequest; // PT_REQUEST
	private Date ptUpdateDate;
	
	private List<Integer> infoCategory;
	
	// 자동추천 후보에 대한 정보 조회를 위한 필드값 추가(시작)
	private int ptRealAge;
	private int matNo;
	private int matType;
	private int hosInfo; // HOS_INFO
	private String memberNational; // MEMBER_NATIONAL
	private String service;
	private String matRequest;
	private Date beginDt; // BEGIN_DT
	private Date endDt; // END_DT
	private int money; // MONEY
	private String ptDisease;
	private int ptCount;
	// 자동추천 후보에 대한 정보 조회를 위한 필드값 추가(끝)
}
