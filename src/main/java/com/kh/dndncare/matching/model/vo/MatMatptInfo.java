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
public class MatMatptInfo {
	
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
	private int matType;
	private int hospitalNo;
	private int memberNo;	
	private int ptCount;
	private String beginTime;
	private String endTime;
	
	//endDT를 지난 날짜 체크
	private boolean isAfterDate;
}
