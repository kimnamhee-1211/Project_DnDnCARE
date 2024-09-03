package com.kh.dndncare.member.model.vo;

import java.sql.Date;
import java.util.HashMap;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.sql.Date;



@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Member {
	private int memberNo;
	private String memberId;
	private String memberPwd;
	private String memberName;
	private String memberGender;
	private String memberNickName;
	private Date memberAge;
	private String memberPhone;
	private String memberEmail;
	private Date memberCreateDate;
	private String memberAddress;
	private String memberCategory;
	private String memberStatus;
	private String memberNational;
	private String memberSocailToken;
	private Date memberUpdateDate;
	
	//카테고리 추가하기
	
	private int memberRealAge; // 나이 계산용 필드 추가
	private String career;
	private String license;
	private int matNo;
	private String groupLeader;
}
