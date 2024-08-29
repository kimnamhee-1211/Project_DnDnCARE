package com.kh.dndncare.member.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.member.model.vo.CalendarEvent;

import java.util.List;

import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.board.model.vo.Reply;
import com.kh.dndncare.matching.model.vo.CareReview;
import com.kh.dndncare.matching.model.vo.MatMatptInfo;
import com.kh.dndncare.matching.model.vo.MatPtInfo;
import com.kh.dndncare.matching.model.vo.MatMatptInfoPt;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.matching.model.vo.Pay;
import com.kh.dndncare.matching.model.vo.RequestMatPt;
import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.member.model.vo.CareGiverMin;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

public interface MemberService {

	Member login(Member m);
	
	int noInfomemberdle(int memberNo);

	int idCheck(String id);
	
	int nickNameCheck(String nickName);


	ArrayList<CalendarEvent> caregiverCalendarEvent(Integer memberNo);

	ArrayList<Member> selectAllMember();
	
	int enroll(Member m);

	int enrollCareGiver(CareGiver cg);

	int enrollInfoCategory(Object ob);

	int enrollPatient(Patient pt);


	Member findIdResult(Member member);

	boolean sendSms(String phoneNumber, String string);


	Patient selectPatient(int memberNo);
	HashMap<String, String> getCaregiverInfo(int memberNo);

	List<Integer> selectInfoCategory(int memberNo);


	int updatePassword(HashMap<String, String> changeInfo);
	ArrayList<HashMap<String, String>> getCaregiverExp(int memberNo);

	List<Integer> selectMemberInfo(int memberNo);

	int deleteWantInfo(int i);

	int insertWantInfo(HashMap<String, Integer> info);

	ArrayList<HashMap<String, String>> selectWantInfo(int memberNo);

	int updatePatient(Patient p);

	int insertMemberInfo(HashMap<String, Integer> info);

	int deleteMemberInfo(int memberNo);

	int updateMember(Member m);
	ArrayList<Patient> selectPatientList(String caregiverCity);
	ArrayList<Patient> selectPatientList(HashMap<String, Object> condition);

	ArrayList<HashMap<String, String>> getPatientExp(ArrayList<Integer> pNoList);

	ArrayList<Patient> choicePatientList(ArrayList<Integer> choiceNoList);

	ArrayList<HashMap<String, String>> getCaregiverWant(int memberNo);

	ArrayList<HashMap<String, Object>> getPatientInfo(ArrayList<Integer> mNoList);

	HashMap<String, String> getPatientMyInfo(int memberNo);

	ArrayList<HashMap<String, String>> getPatientMyExp(int memberNo);

	ArrayList<HashMap<String, String>> getCaregiverMyWant(int memberNo);

	ArrayList<HashMap<String, Object>> selectCaregiverList(HashMap<String, Object> condition);

	ArrayList<HashMap<String, Object>> selectCaregiverInfo(ArrayList<Integer> mNoList);

	ArrayList<CareGiver> choiceCaregiverList(ArrayList<Integer> choiceNoList);

	ArrayList<HashMap<String, Integer>> getPatientEvent(int memberNo);

	ArrayList<CalendarEvent> patientCalendarEvent(ArrayList<Integer> matNoList);

	ArrayList<Member> selectMemberList(ArrayList<Integer> memberNoList);

	ArrayList<Matching> selectMatchingList(PageInfo pi, ArrayList<Integer> resultMatNoList);

	int getMatchingListCount(HashMap<String, Object> searchOption);

	ArrayList<Patient> selectMatchingMemberList(ArrayList<Integer> matNoList);

	ArrayList<MatPtInfo> selectMatchingPTInfoList(ArrayList<Integer> matNoList);

	ArrayList<HashMap<String, Integer>> searchDefaultMatNoList(HashMap<String, Object> searchDefaultMap);

	ArrayList<Integer> searchTermMatNoList(HashMap<String, Object> termMap);

	ArrayList<Integer> searchTimeMatNoList(HashMap<String, Object> termMap);

	ArrayList<HashMap<String, Integer>> searchCategoryMatNoList(ArrayList<Integer> tempMatNoList);

	ArrayList<Matching> searchMatchingList(PageInfo pi, ArrayList<Integer> resultMatNoList);

	ArrayList<CareGiver> selectAllCaregiver(PageInfo pi);

	int getCaregiverListCount();

	ArrayList<HashMap<String, Integer>> getCaregiverScoreList(ArrayList<Integer> cNoList);

	ArrayList<CareGiver> searchDefaultCaregiverNoList(HashMap<String, Object> searchDefaultMap);

	ArrayList<HashMap<String, Integer>> searchCaregiverCategoryMNoList(ArrayList<Integer> cNoList);

	ArrayList<CareGiver> searchCaregiverList(PageInfo pi, ArrayList<Integer> resultCaregiverNoList);




	CareGiver selectCareGiver(int memberNo);

	int updateCareGiver(CareGiver cg);

	int updateMemberVer2(Member m);

	ArrayList<Board> mySelectBoardList(PageInfo pi, int mNo);

	int getBoardListCount(int mNo);

	int boardLikeCount(int boardNo);

	int getReplyListCount(int mNo);

	ArrayList<Reply> mySelectReplyList(PageInfo replyPi, int mNo);

	int replyLikeCount(int replyNo);

	int getLikeListCount(int mNo);

	ArrayList<Board> mySelectLikeList(PageInfo likePi, int mNo);

	int likeLikeCount(int boardNo);

	ArrayList<MatMatptInfo> selectMatList(int i);

	ArrayList<MatMatptInfoPt> getMatMatptInfoPt(int memberNo);

	ArrayList<RequestMatPt> getRequestMatPt(int memberNo);

	ArrayList<CareGiver> selectCareGiverList();

	Member selectSocialLogin(String code);
	ArrayList<CareReview> reviewList(int ptNo);

	ArrayList<CareReview> selectReviewList(int reviewNo);

	int getPtNo(int memberNo);

	void nn(int mId);
	ArrayList<CareGiverMin> getRequestCaregiver(int ptNo);
	ArrayList<CareReview> caregiverReviewList(int memberNo);

	ArrayList<CareReview> monthScoreList(int memberNo);

	ArrayList<CareReview> sumAvgScore(int memberNo);

	ArrayList<MatMatptInfoPt> useMonth(int ptNo);

	int selectReviewYn(int matNo, int ptNo);

	Double avgReviewScore2(int memberNo);

	ArrayList<MatMatptInfo> selectMatListPay(int memberNo);

	ArrayList<MatMatptInfoPt> selectMatRecord(int memberNo);

	ArrayList<MatMatptInfoPt> monthCountList(int memberNo);
	
	int updateImageProfile(String memberNo, String rename);

	CareGiver selectProfile(String memberNo);

	int deleteMember(int memberNo);

	ArrayList<Pay> selectPayTransfer(int memberNo);
	
	int getCountPendingMe(int matNo, int memberNo);

	int getCountPt(int matNo);

	Integer getDelMemberNo();

	Matching selectEndDateMat(int matNo);



}
