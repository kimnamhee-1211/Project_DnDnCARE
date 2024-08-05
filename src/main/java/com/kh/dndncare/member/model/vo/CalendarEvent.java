package com.kh.dndncare.member.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class CalendarEvent {
	/*	캘린더 이벤트 출력하는데 필요한 정보들
	 * 	매칭번호, 제목, 시작날짜, 종료날짜
	 * 	금액, 병원번호, 병원주소, 병원이름
	 * 
	 * */
	private int matNo;
	private String title; // 캘린더 API 이벤트를 지정하기 위한 필드
	private Date start; // 캘린더 API 이벤트를 지정하기 위한 필드
	private Date end; // 캘린더 API 이벤트를 지정하기 위한 필드
	private int money;
	private int hospitalNo; 
	private String hospitalAddress;
	private String hospitalName;
}
