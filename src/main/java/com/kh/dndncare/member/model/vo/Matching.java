package com.kh.dndncare.member.model.vo;

import java.sql.Date;

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
public class Matching {
	private int matNo;//MAT_NO
	private Date beginDt;//BEGIN_DT
	private Date endDt;//END_DT
	private int money;//MONEY
	private String matConfirm;//MAT_CONFIRM
	private int matType;//MAT_TYPE
	private int hospitalNo;//HOSPTAL_NO
	private String hospitalName;
	
	private String beginTime;
	private String endTime;
	private int ptCount;
	
	
	
}
