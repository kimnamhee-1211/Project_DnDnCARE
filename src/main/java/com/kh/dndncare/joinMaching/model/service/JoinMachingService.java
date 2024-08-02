package com.kh.dndncare.joinMaching.model.service;

import com.kh.dndncare.joinMaching.model.vo.Hospital;
import com.kh.dndncare.matching.model.vo.MatPtInfo;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.member.model.vo.Patient;

public interface JoinMachingService {
	
	//병원 테이블 등록
	int enrollHospital(Hospital ho);
	//매칭 테이블 등록
	int enrollMatching(Matching ma);
	//pt get
	Patient getPatient(int memberNo);
	//매칭 pt info 테이블 등록
	int enrollMatPtInfo(MatPtInfo gmPt);



}
