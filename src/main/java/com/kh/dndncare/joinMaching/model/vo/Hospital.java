package com.kh.dndncare.joinMaching.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Hospital {
	
	private String hospitalNm; //의료기관명
	private String addr; //주소
	private String tel; //전화번호

}
