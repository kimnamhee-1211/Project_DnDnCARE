package com.kh.dndncare.matching.model.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.dndncare.matching.model.vo.CareReview;
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

	int insertMatchingDate(@Param("matNo") int matNo, @Param("matchingDate") String matchingDate);
	
	ArrayList<CareReview> selectReviewList(int memberNo);

	Hospital getHospital(Hospital hospital);

	int reviewCount(int memberNo);

	int avgReviewScore(int memberNo);

}
