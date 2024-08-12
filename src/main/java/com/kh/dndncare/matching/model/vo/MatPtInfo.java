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
public class MatPtInfo {
	
	private int matNo;
	private int ptNo;
	private int antePay;
	private String service;
	private String matAddressInfo;
	private String matRequest;
	private String deposit;
	private String groupLeader;
	private int reviewNo;
	
	
	

}
