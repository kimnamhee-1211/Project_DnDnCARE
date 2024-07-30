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
public class CareGiver {
	private int memberNo;
	private String careImg;
	private String careIntro;
	private int minMoney;
	private int maxMoney;
	private String careJoinStatus;
	private String careService;
	private Date careUpdateDate;
	
	private List<Integer> infoCategory;
}
