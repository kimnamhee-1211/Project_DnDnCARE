package com.kh.dndncare.matching.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.matching.model.dao.MatchingMapper;
import com.kh.dndncare.matching.model.vo.CareReview;
import com.kh.dndncare.matching.model.vo.Hospital;
import com.kh.dndncare.matching.model.vo.MatMatptInfo;
import com.kh.dndncare.matching.model.vo.MatMatptInfoPt;
import com.kh.dndncare.matching.model.vo.MatPtInfo;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.member.model.vo.CareGiverMin;
import com.kh.dndncare.matching.model.vo.Pay;
import com.kh.dndncare.matching.model.vo.joinMatInfoMin;
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
	public ArrayList<InfoCategory> getInfo(int ptNo) {
		return mMapper.getInfo(ptNo);
	}

	
	//insert MatchingDate
	@Override
	public int insertMatDate(int matNo, String matDate) {
		return mMapper.insertMatDate(matNo, matDate);
	}
	
	//병원  데이터 get
	@Override
	public int getHospital(Hospital hospital) {
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
	public int deleteWantInfo(int memberNo) {
		return mMapper.deleteWantInfo(memberNo);
	}

	// 간병인 소개
	@Override
	public CareGiver selectIntro(int memberNo) {
		return mMapper.selectIntro(memberNo);
	}

	
	//매칭/매칭인포/환자/병원 한번에 가져오기
	@Override
	public ArrayList<MatMatptInfoPt> matPtInfoToCaregiver(int matNo) {
		return mMapper.matPtInfoToCaregiver(matNo);
	}
	
	//매칭테이블에 간병인 memberNo 넣기
	@Override
	public int requestMatching(int memberNo, int matNo) {
		return mMapper.requestMatching(memberNo, matNo);
	}
	
	//pt 이름 뽑기(공동간병일 경우 방 개설자)
	@Override
	public String getMatPtName(int matNo, int ptCount) {
		return mMapper.getMatPtName(matNo, ptCount);
	}
	
	//매칭방에 이미 매칭 신청을 한 간병인인지 아닌지 확인
	@Override
	public int requestMatCheck(int memberNo, int matNo) {
		// TODO Auto-generated method stub
		return mMapper.requestMatCheck(memberNo, matNo);
	}

	//나(간병인)의 현재 매칭 정보)
	@Override
	public ArrayList<MatMatptInfoPt> getMyMatching(int memberNo) {
		return mMapper.getMyMatching( memberNo);
	}
	
	//매칭 신청 내역
	@Override
	public ArrayList<MatMatptInfoPt> getMyRequestMat(int memberNo) {
		return mMapper.getMyRequestMat(memberNo);
	}

	//matching테이블에 간병인 memberNo들어왔는지 확인
	@Override
	public Integer getMatMemberNo(int matNo) {
		return mMapper.getMatMemberNo(matNo);
	}
	
	//간병인의 매칭 승낙
	@Override
	public int matchingApproveC(int matNo,  int memberNo) {
		return mMapper.matchingApproveC(matNo, memberNo);
	}

	@Override
	public int insertReview(HashMap<String, Object> map) {
		return mMapper.insertReview(map);
	}
	
	public MatMatptInfo selecMatPtInfo(int matNo, int memberNo) {
		return mMapper.selecMatPtInfo(matNo,memberNo);
	}
	public MatMatptInfo selectMatching(int matNo) {
		return mMapper.selectMatching(matNo);
	}


	@Override
	public int insertPay(Member loginUser, Pay p) {
		return mMapper.insertPay(loginUser, p);
	}

	@Override
	public String selectMatDate(int matNo) {
		return mMapper.selectMatDate(matNo);
	}
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

	public int insertMatPtInfo(MatPtInfo matPtInfo) {
		// TODO Auto-generated method stub
		return 0;//없는메소드	
	}
	
	
	//나(환자)의 현재 매칭 정보- 진행 + 결제대기 + 환자자 신청
	@Override
	public ArrayList<CareGiverMin> getMyMatchingP(int ptNo) {
		return mMapper.getMyMatchingP(ptNo);
	}

	//나(환자)의 현재 매칭 정보-- 간병인이 환자(나) 신청
	@Override
	public ArrayList<CareGiverMin> getMyMatchingPN(int ptNo) {
		return mMapper.getMyMatchingPN(ptNo);
	}
	
	
	//매칭 간병인 이름 얻어오기
	@Override
	public String getNameC(int memberNo) {
		return mMapper.getNameC(memberNo);
	}
	
	@Override
	public int insertMemberInfo(Map<String, Object> memberInfoParams) {
		return mMapper.insertMemberInfo(memberInfoParams);
	}

	@Override
	public int deleteMemberInfo(Map<String, Object> memberInfoParams) {
		return mMapper.deleteMemberInfo(memberInfoParams);
	}

	@Override
	public List<Integer> getCategoryNo(int memberNo) {
		return mMapper.getCategoryNo(memberNo);
	}
	
	//환자 -> 간병인 매칭 신청했을 경우 macthing 테이블에 간병인 memberNo 넣기
	@Override
	public int updateMatC(int matNo, int memberNoC) {
		return mMapper.updateMatC(matNo, memberNoC);
	}

	@Override
	public int matchingApproveP(int matNo, int memberNo) {
		return mMapper.matchingApproveP(matNo, memberNo);
	}

	//환자가 이미 신청한 내역인지 확인
	@Override
	public int CheckMatMemNo(int matNo) {
		return mMapper.CheckMatMemNo(matNo);
	}

	//환자 매칭 신청 취소
	@Override
	public int matchingCancelP(int matNo) {
		return mMapper.matchingCancelP(matNo);
	}

	//간병인 매칭 신청 취소
	@Override
	public int matchingCancelC(int matNo, int memberNo) {
		return mMapper.matchingCancelC(matNo, memberNo);
	}
	
	//ptCount get
	@Override
	public int getPtCount(int matNo) {
		return mMapper.getPtCount(matNo);
	}

	@Override
	public String getGroupLeader(int matNo, int ptNo) {
		return mMapper.getGroupLeader(matNo, ptNo);
	}

	//간병인 결제 금액 받기 jg
	@Override
	public ArrayList<Pay> selectPayTransfer(int memberNo) {
		
		return mMapper.selectPayTransfer2(memberNo);
	}


	//간병인  결제 금액 받기 jg 2 
	@Override
	public int insertPayTransfer(Member loginUser, Pay p) {
		return mMapper.insertPayTransfer(loginUser,p);
	}
	@Override
	public ArrayList<MatMatptInfo> serviceList(int memberNo) {
		return mMapper.serviceList(memberNo);
	}

	@Override
	public ArrayList<Matching> matPatientList(int memberNo) {
		return mMapper.matPatientList(memberNo);
	}

	
	//참여중인 공동간병그룹 만들기
	@Override
	public ArrayList<joinMatInfoMin> getMyJoinMat(int loginPt) {
		return mMapper.getMyJoinMat(loginPt);
	}
	@Override
	public int getHospitalNo(String hospitalName) {
	    Integer result = mMapper.getHospitalNo(hospitalName);
	    return result != null ? result : 0;
	}

	@Override
	public int insertHospital(String hospitalName, String hospitalAddress) {
		return mMapper.insertHospital(hospitalName,hospitalAddress);
	}

	@Override
	public String getRequest(int memberNo) {
		return mMapper.getRequest(memberNo);
	}
	//결제완료된 페이 상태수정
	@Override
	public int updatePayTransfer(Pay p) {
		return mMapper.updatePayTransfer(p);
	}





}
