package com.kh.dndncare.admin.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Complaint {

	private int complaintNo;
	private int matNo;
	private int senderMemberNo;
	private int reciverMemberNo;
	private String type;
	private String detail;
	private String status;
	private String result;
}
