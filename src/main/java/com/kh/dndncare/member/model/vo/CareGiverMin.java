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
public class CareGiverMin {
	private int memberNo;
	private String careImg;
	private String memberNational;
	private String memberGender;
	private String memberName;
	private Date memberAge;
	private int age;
	private Date beginDt;
	private Date endDt;
	private String matConfirm;

	
}
