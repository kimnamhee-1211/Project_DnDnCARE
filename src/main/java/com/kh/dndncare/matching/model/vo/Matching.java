package com.kh.dndncare.matching.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Matching {
	
	private int matNo;
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
	
	private int matType;//MAT_TYPE
	private String hospitalName;
	
}


