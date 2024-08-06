package com.kh.dndncare.matching.model.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;


import com.kh.dndncare.matching.model.vo.Hospital;
import com.kh.dndncare.matching.model.vo.MatMatptInfo;
import com.kh.dndncare.matching.model.vo.MatPtInfo;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.member.model.vo.InfoCategory;
import com.kh.dndncare.member.model.vo.Patient;

@Mapper
public interface MatchingMapper {
	
	int enrollHospital(Hospital ho);

	int enrollMatching(Matching ma);

	Patient getPatient(int memberNo);

	int enrollMatPtInfo(MatPtInfo jmPt);

	ArrayList<MatMatptInfo> getJmList(String hospitalName);

	MatMatptInfo getMatMatptInfo(int matNo);

	ArrayList<Patient> getPatientToMatNo(int matNo);

	ArrayList<InfoCategory> getInfo(int memberNo);

	int updatePatient(Patient patient);

	int insertMatching(Matching matching);

	int insertMatchingDate(HashMap<String, Object> map);

	int getMatNo(int ptNo);

	int insertWantInfo(Map<String, Object> params);

	int insertMatPtInfo(MatPtInfo matPtInfo);

	int deleteWantInfo(int memberNo);


}
