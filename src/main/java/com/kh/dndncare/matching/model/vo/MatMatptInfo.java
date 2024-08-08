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
	private int hospitalNo;
	private int memberNo;	
	private int ptCount;
	private String beginTime;
	private String endTime;
	private int matMode;
	
	//endDT를 지난 날짜 체크
	private boolean isAfterDate;
	
	//loginUser의 그룹 간병 참여 여부 확인용
	private String join;
	
	//간병인이름도 같이담기
	private String memberName;
	//기간제 계산 자바스크립트에서안하려구만듬
	private long days;
	private long times;
}
