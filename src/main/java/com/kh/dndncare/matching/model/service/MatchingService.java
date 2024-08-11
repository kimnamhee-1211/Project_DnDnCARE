package com.kh.dndncare.matching.model.service;


import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.kh.dndncare.matching.model.vo.CareReview;
import com.kh.dndncare.matching.model.vo.Hospital;
import com.kh.dndncare.matching.model.vo.MatMatptInfo;
import com.kh.dndncare.matching.model.vo.MatMatptInfoPt;
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
	ArrayList<InfoCategory> getInfo(int ptNo);
	
	//insert MatchingDate
	int insertMatDate(int matNo, String matDate);
	
	//병원  데이터 Count  get
	Hospital getHospital(Hospital hospital);
	
	//loginUser-MatNo get
	Set<Integer> getloginMatNo(int memberNo);
	
	//loginUser-PtNo get
	int getPtNo(int memberNo);
	
	//시간제일 경우 선택한 날짜 get
	String getMatDate(int matNo);
	
	//MatPtInfo del
	int delMatPtInfo(int matNo, int ptNo);
	
	//매칭에 참여하고 잇는 인원이 몇인지 => 매칭 table 한 튜플에 따른 matPtInfo 테이블 튜플 수
	int joinPtCount(int matNo);
	
	//매칭 테이블 del
	int delMatching(int matNo);
	
	
	//매칭date 테이블 del
	int delMatchingDate(int matNo);
	ArrayList<CareReview> selectReviewList(int memberNo);
	
	int reviewCount(int memberNo);
	
	int avgReviewScore(int memberNo);
	

	int updatePatient(Patient patient);
	int insertMatchingDate(HashMap<String, Object> map);
	int getMatNo(int ptNo);
	int insertMatching(Matching matching);
	int insertWantInfo(Map<String, Object> params);
	int deleteWantInfo(int memberNo);
	
	
	//매칭/매칭인포/환자/병원 한번에 가져오기
	ArrayList<MatMatptInfoPt> matPtInfoToCaregiver(int matNo);
	
	//매칭테이블에 간병인 memberNo 넣기
	int requestMatching(int memberNo, int matNo);
	
	//pt 이름 뽑기(공동간병일 경우 방 개설자)
	String getMatPtName(int matNo, int ptCount );
	
	//이미 매칭을 신청한 사람인지 확인
	int requestMatCheck(int memberNo, int matNo);
	
	//나의 현재 매칭 정보
	ArrayList<MatMatptInfoPt> getMyMatching(int memberNo);
	
	//매칭 신청 내역
	ArrayList<MatMatptInfoPt> getMyRequestMat(int memberNo);
	
	//matching테이블에 간병인 memberNo들어왔는지 확인
	Integer getMatMemberNo(int matNo);
	
	//간병인의 매칭 승낙
	int matchingApproveC(int matNo, int memberNo);



}
