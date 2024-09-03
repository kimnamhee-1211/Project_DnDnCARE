package com.kh.dndncare.board.model.vo;

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
public class Board {
	private int boardNo;
	private int memberNo;
	private String boardTitle;
	private String boardContent;
	private String boardStatus;
	private Date boardCreateDate;
	private Date boardUpdateDate;
	private int boardCount;
	private int categoryNo;
	private int areaNo;
	private String categoryName;
	private String areaName;
	private String memberNickName;
	private String memberCategory;
	private int answerYN;
	private int passHours;
}
