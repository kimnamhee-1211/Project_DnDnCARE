package com.kh.dndncare.member.model.vo;

import java.sql.Date;
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
	private String memberPay;
	private Date memberUpdateDate;
}
