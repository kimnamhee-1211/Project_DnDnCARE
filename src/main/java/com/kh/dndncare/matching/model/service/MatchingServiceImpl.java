package com.kh.dndncare.matching.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.matching.model.dao.MatchingMapper;
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

@Service
public class MatchingServiceImpl implements MatchingService {

	@Autowired
	private MatchingMapper mMapper;

	//병원 테이블 등록
	@Override
	public int enrollHospital(Hospital ho) {
		
		return mMapper.enrollHospital(ho);
	}

	//매칭 테이블 등록
	@Override
	public int enrollMatching(Matching ma) {
		
		return mMapper.enrollMatching(ma);
	}

	//pt get
	@Override
	public Patient getPatient(int memberNo) {
		return mMapper.getPatient(memberNo);
	}
	
	//매칭 pt info 테이블 등록
	@Override
	public int enrollMatPtInfo(MatPtInfo jmPt) {
		return mMapper.enrollMatPtInfo(jmPt);
	}

	//병원으로 get Matching & MatPtInfo
	@Override
	public ArrayList<MatMatptInfo> getJmList(String hospitalName) {
		
		return mMapper.getJmList(hospitalName);
	}

	//matNo으로  get matching & ptinfo 테이블	
	@Override
	public MatMatptInfo getMatMatptInfo(int matNo) {
		return mMapper.getMatMatptInfo(matNo);
	}
	
	//matNo로 get 공동 간병 참여자들 Patient
	@Override
	public ArrayList<Patient> getPatientToMatNo(int matNo) {
		return mMapper.getPatientToMatNo(matNo);
	}

	//get member info (대분류 : 소분류)
	public ArrayList<InfoCategory> getInfo(int memberNo) {
		return mMapper.getInfo(memberNo);
	}

	
	//insert MatchingDate
	@Override
	public int insertMatchingDate(int matNo, String matchingDate) {
		return mMapper.insertMatchingDate(matNo, matchingDate);
	}
	
	//병원  데이터 get
	@Override
	public Hospital getHospital(Hospital hospital) {
		return mMapper.getHospital(hospital);
	}

	//loginUser-MatNo get
	@Override
	public Set<Integer> getloginMatNo(int memberNo) {
		return mMapper.getloginMatNo(memberNo);
	}
	
	//loginUser-PtNo get
	@Override
	public int getPtNo(int memberNo) {
		return mMapper.getPtNo(memberNo);
	}
	
	//시간제일 경우 선택한 날짜 get
	@Override
	public String getMatDate(int matNo) {
		return mMapper.getMatDate(matNo);
	}

	//MatPtInfo del
	@Override
	public int delMatPtInfo(int matNo, int ptNo) {
		return mMapper.delMatPtInfo(matNo, ptNo);
	}

	//매칭에 참여하고 잇는 인원이 몇인지 => 매칭 table 한 튜플에 따른 matPtInfo 테이블 튜플 수
	public int joinPtCount(int matNo) {
		return mMapper.joinPtCount(matNo);
	}

	//매칭 테이블 del
	@Override
	public int delMatching(int matNo) {
		return mMapper.delMatching(matNo);
	}
	
	//매칭date  테이블 del
	@Override
	public int delMatchingDate(int matNo) {
		return mMapper.delMatchingDate(matNo);
	}

	@Override
	public ArrayList<CareReview> selectReviewList(int memberNo) {
		return mMapper.selectReviewList(memberNo);
	}

	@Override
	public int reviewCount(int memberNo) {
		return mMapper.reviewCount(memberNo);
	}

	@Override
	public Double avgReviewScore(int memberNo) {
		return mMapper.avgReviewScore(memberNo);
	}
 
	@Override
	public int updatePatient(Patient patient) {
		return mMapper.updatePatient(patient);
	}

	@Override
	public int insertMatchingDate(HashMap<String, Object> map) {
		return mMapper.insertMatchingDate(map);
	}

	@Override
	public int getMatNo(int ptNo) {
		return mMapper.getMatNo(ptNo);
	}

	@Override
	public int insertMatching(Matching matching) {
		return mMapper.insertMatching(matching);
	}

	@Override
	public int insertWantInfo(Map<String, Object> params) {
		return mMapper.insertWantInfo(params);
	}

	@Override
	public int insertMatPtInfo(MatPtInfo matPtInfo) {
		return mMapper.insertMatPtInfo(matPtInfo);
	}

	@Override
	public int deleteWantInfo(int memberNo) {
		return mMapper.deleteWantInfo(memberNo);
	}

	// 간병인 소개
	@Override
	public CareGiver selectIntro(int memberNo) {
		return mMapper.selectIntro(memberNo);
	}
	@Override
	public MatMatptInfo selecMatching(int matNo) {
		return mMapper.selectMatching(matNo);
	}

	@Override
	public int insertReview(HashMap<String, Object> map) {
		return mMapper.insertReview(map);
	}
	
	public MatMatptInfo selecMatPtInfo(int matNo, int memberNo) {
		return mMapper.selecMatPtInfo(matNo,memberNo);
	}

	@Override
	public int insertPay(Member loginUser, Pay p) {
		return mMapper.insertPay(loginUser,p);
	}

	@Override
	public int deleteReivew(int reviewNo) {
		return mMapper.deleteReview(reviewNo);
	}

	@Override
	public ArrayList<InfoCategory> getCaregiverInfo(int memberNo) {
		return mMapper.getCaregiverInfo(memberNo);
	}

	@Override
	public int updateReview(CareReview cr) {
		return mMapper.updateReview(cr);
	}






}
