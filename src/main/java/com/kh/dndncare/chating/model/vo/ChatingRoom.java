package com.kh.dndncare.chating.model.vo;

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
public class ChatingRoom {
	private int chatRoomNo;
	private int matNo;
	private String chatStatus;
	private String chatTitle;
}
