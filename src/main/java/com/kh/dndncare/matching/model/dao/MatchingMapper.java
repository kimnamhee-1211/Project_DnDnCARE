package com.kh.dndncare.matching.model.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.dndncare.matching.model.vo.CareReview;
import com.kh.dndncare.matching.model.vo.Hospital;
import com.kh.dndncare.matching.model.vo.MatMatptInfo;
import com.kh.dndncare.matching.model.vo.MatMatptInfoPt;
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

	ArrayList<InfoCategory> getInfo(int ptNo);

	int insertMatDate(@Param("matNo") int matNo, @Param("matDate") String matDate);
	
	ArrayList<CareReview> selectReviewList(int memberNo);

	Hospital getHospital(Hospital hospital);

	Set<Integer> getloginMatNo(int memberNo);

	int getPtNo(int memberNo);

	String getMatDate(int matNo);

	int delMatPtInfo(@Param("matNo") int matNo, @Param("ptNo") int ptNo);

	int joinPtCount(int matNo);

	int delMatching(int matNo);

	int delMatchingDate(int matNo);
	int reviewCount(int memberNo);

	int avgReviewScore(int memberNo);
	int updatePatient(Patient patient);

	int insertMatching(Matching matching);

	int insertMatchingDate(HashMap<String, Object> map);

	int getMatNo(int ptNo);

	int insertWantInfo(Map<String, Object> params);

	int deleteWantInfo(int memberNo);

	ArrayList<MatMatptInfoPt> matPtInfoToCaregiver(@Param("matConfirm") String matConfirm, @Param("matNo") int matNo);

	int requestMatching(@Param("memberNo") int memberNo, @Param("matNo") int matNo);

	String getMatPtName(@Param("matNo") int matNo, @Param("ptCount") int ptCount);

	int requestMatCheck(@Param("memberNo") int memberNo, @Param("matNo") int matNo);

	ArrayList<MatMatptInfoPt> requestMgetMyMatchingatCheck(int memberNo);


}
