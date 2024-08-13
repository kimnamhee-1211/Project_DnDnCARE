package com.kh.dndncare.chating.model.vo;

import java.sql.Date;

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
public class ChatingRoomMessage {
	private int chatMassageNo;
	private int chatRoomNo;
	private int memberNo;
	private String chatContent;
	private int readCount;
	private Date writeDate;
	private String memberName;
}
