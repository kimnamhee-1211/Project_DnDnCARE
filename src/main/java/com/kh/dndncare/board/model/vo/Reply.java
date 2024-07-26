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
public class Reply {
	private int replyNo;
	private String replyContent;
	private int memberNo;
	private Date replyCreateDate;
	private Date replyUpdateDate;
	private String replyStatus;
	private int refBoardNo;
	private String memberNickName;
}
