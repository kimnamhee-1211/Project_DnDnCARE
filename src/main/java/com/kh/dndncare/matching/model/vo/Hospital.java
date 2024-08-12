package com.kh.dndncare.matching.model.vo;

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
	
	private int hospitalNo; 
	private String hospitalName;
	private String hospitalAddress; 

}
