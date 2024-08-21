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
public class Pay {
	private int payNo;
	private int matNo;
	private int memberNo;
	private Date payDate;
	private int payMoney;
	private String accountName;
	private String accountPhone;
	private String accountEmail;
	private String mercharntUid;
	private String payService;
}
