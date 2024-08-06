package com.kh.dndncare.matching.model.service;


import java.sql.Date;
import java.util.ArrayList;

import com.kh.dndncare.matching.model.vo.Hospital;
import com.kh.dndncare.matching.model.vo.MatMatptInfo;
import com.kh.dndncare.matching.model.vo.MatPtInfo;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.member.model.vo.InfoCategory;
import com.kh.dndncare.member.model.vo.Patient;

public interface MatchingService {
	
	//병원 테이블 등록
	int enrollHospital(Hospital ho);
	//매칭 테이블 등록
	int enrollMatching(Matching ma);
	//pt get
	Patient getPatient(int memberNo);
	//매칭 pt info 테이블 등록
	int enrollMatPtInfo(MatPtInfo jmPt);
	
	//병원으로 get matching & ptinfo 테이블 list
	ArrayList<MatMatptInfo> getJmList(String hospitalName);
	
	//matNo으로  get matching & ptinfo 테이블
	MatMatptInfo getMatMatptInfo(int matNo);
	
	//matNo로 get 공동 간병 참여자들 Patient
	ArrayList<Patient> getPatientToMatNo(int matNo);
	
	//get member info (대분류 : 소분류)
	ArrayList<InfoCategory> getInfo(int memberNo);
	
	//insert MatchingDate
	int insertMatchingDate(int matNo, String matchingDate);
	
	//병원  데이터 Count  get
	Hospital getHospital(Hospital hospital);

}
