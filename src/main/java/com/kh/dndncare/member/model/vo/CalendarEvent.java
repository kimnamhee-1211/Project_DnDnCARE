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
	
	private String beginTime;
	private String endTime;
	private int matMode; // 1:기간제, 2:시간제
	private String matchingType; // ptCount==1이면 개인간병, ptCount==2이면 공동간병
	private int ptCount; 
	private String matAddressInfo;
	private int ptNo;
	private String matDate; // 시간제 간병일 때의 근무날짜
	private Date beginDt;
	private Date endDt;
	
	
	
}
