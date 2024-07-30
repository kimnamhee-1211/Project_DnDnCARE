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
	private String title; // 캘린더 API 이벤트를 지정하기 위한 필드
	private Date start; // 캘린더 API 이벤트를 지정하기 위한 필드
	private Date end; // 캘린더 API 이벤트를 지정하기 위한 필드
}
