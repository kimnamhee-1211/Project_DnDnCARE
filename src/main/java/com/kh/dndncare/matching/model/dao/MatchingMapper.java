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
import com.kh.dndncare.matching.model.vo.MatPtInfo;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.matching.model.vo.Pay;
import com.kh.dndncare.member.model.vo.InfoCategory;
import com.kh.dndncare.member.model.vo.Member;
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

	Set<Integer> getloginMatNo(int memberNo);

	int getPtNo(int memberNo);

	String getMatDate(int matNo);

	int delMatPtInfo(@Param("matNo") int matNo, @Param("ptNo") int ptNo);

	int joinPtCount(int matNo);

	int delMatching(int matNo);

	int delMatchingDate(int matNo);
	int reviewCount(int memberNo);

	Double avgReviewScore(int memberNo);
	
	int updatePatient(Patient patient);

	int insertMatching(Matching matching);

	int insertMatchingDate(HashMap<String, Object> map);

	int getMatNo(int ptNo);

	int insertWantInfo(Map<String, Object> params);

	int insertMatPtInfo(MatPtInfo matPtInfo);

	int deleteWantInfo(int memberNo);

	CareGiver selectIntro(int memberNo);
	
	MatMatptInfo selectMatching(int matNo);

	int insertReview(HashMap<String, Object> map);
	
	MatMatptInfo selecMatPtInfo(@Param("matNo")int matNo,@Param("memberNo") int memberNo);

	int insertPay(@Param("loginUser")Member loginUser,@Param("p") Pay p);

	int deleteReview(int reviewNo);


}
