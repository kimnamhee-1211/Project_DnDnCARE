package com.kh.dndncare.member.model.vo;

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
public class CareGiver {
	private int memberNo;
	private String caraImg;
	private String careCareer;
	private String careIntro;
	private int minMoney;
	private int maxMoney;
	private String careJoinStatus;
	private String careService;
	
	private String[] serviceName;
	private String[] disaseName;
	private String[] licenseName;
	

}
