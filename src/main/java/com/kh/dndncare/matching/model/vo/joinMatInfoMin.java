package com.kh.dndncare.matching.model.vo;

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
public class joinMatInfoMin {
	
	//hospital
	private String hospitalName;
	private String hospitalAddress;
	
	//mat
	private int matNo;
	private int ptCount;
	private String matMode;
	
	//그룹 리더만 
	private int ptNo;
	private String ptName;
	

}

