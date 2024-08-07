package com.kh.dndncare.matching.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class MatMatptInfoPt {
	
	//hospital
	private String hospitalName;
	private String hospitalAddress; 
	
	//matinfo
	private int matNo;
	private int ptNo;
	private int antePay;
	private String service;
	private String matAddressInfo;
	private String matRequest;
	private String deposit;
	private String groupLeader;
	
	//matching
	private Date beginDt;
	private Date endDt;
	private int money;
	private String matConfirm;
	private int hospitalNo;
	private int memberNo;	
	private int ptCount;
	private String beginTime;
	private String endTime;
	private int matMode;
	
	//patient
	private String ptName;
	private String ptGender; // PT_GENDER
	private Date ptAge; // PT_AGE
	private int ptWeight; // PT_WEIGHT
	private int ptHeight; // PT_HEIGHT
	private String ptAddress; // PT_ADDRESS
	private String ptRequest; // PT_REQUEST
	private Date ptUpdateDate;
	private int ptRealAge;
	
	//노출 위한 주소 (시까지만 노출)
	private String ptAddressMin;
	
}
