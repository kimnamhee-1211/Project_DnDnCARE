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
	private int memberNo;
	private String ptGender;
	private Date ptAge;
	private int ptWeight;
	private int ptHeight;
	private String ptService;
	private String ptAddress;
	private String ptRequest;
	
	private List<Integer> infoCategory;
}
