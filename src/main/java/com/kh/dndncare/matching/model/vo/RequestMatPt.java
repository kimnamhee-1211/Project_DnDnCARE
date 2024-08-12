package com.kh.dndncare.matching.model.vo;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

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
public class RequestMatPt {

	//matinfo
	private int matNo;
	private String groupLeader;	
	private String service;	
	private int ptCount;
	private String ptName;
	private String ptGender; // PT_GENDER
	private Date ptAge; // PT_AGE
	private int ptRealAge;
}
