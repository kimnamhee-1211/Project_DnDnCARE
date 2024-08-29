package com.kh.dndncare.member.model.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.Reply;
import com.kh.dndncare.matching.model.vo.CareReview;
import com.kh.dndncare.matching.model.vo.MatMatptInfo;
import com.kh.dndncare.matching.model.vo.MatMatptInfoPt;
import com.kh.dndncare.matching.model.vo.MatPtInfo;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.matching.model.vo.Pay;
import com.kh.dndncare.matching.model.vo.RequestMatPt;
import com.kh.dndncare.member.model.vo.CalendarEvent;
import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.member.model.vo.CareGiverMin;
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

@Mapper
public interface MemberMapper {

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

	HashMap<String, String> getCaregiverInfo(int memberNo);

	ArrayList<HashMap<String, String>> getCaregiverExp(int memberNo);

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

	ArrayList<Matching> selectMatchingList(RowBounds rowBounds, ArrayList<Integer> resultMatNoList);

	int getMatchingListCount(HashMap<String, Object> searchOption);

	ArrayList<Patient> selectMatchingMemberList(ArrayList<Integer> matNoList);

	ArrayList<MatPtInfo> selectMatchingPTInfoList(ArrayList<Integer> matNoList);

	ArrayList<HashMap<String, Integer>> searchDefaultMatNoList(HashMap<String, Object> searchDefaultMap);

	ArrayList<Integer> searchTermMatNoList(HashMap<String, Object> termMap);

	ArrayList<Integer> searchTimeMatNoList(HashMap<String, Object> termMap);

	ArrayList<HashMap<String, Integer>> searchCategoryMatNoList(ArrayList<Integer> tempMatNoList);

	ArrayList<Matching> searchMatchingList(RowBounds rowBounds, ArrayList<Integer> resultMatNoList);

	ArrayList<CareGiver> selectAllCaregiver(RowBounds rowBounds, Object obj);

	int getCaregiverListCount();

	ArrayList<HashMap<String, Integer>> getCaregiverScoreList(ArrayList<Integer> cNoList);

	ArrayList<CareGiver> searchDefaultCaregiverNoList(HashMap<String, Object> searchDefaultMap);

	ArrayList<HashMap<String, Integer>> searchCaregiverCategoryMNoList(ArrayList<Integer> cNoList);

	ArrayList<CareGiver> searchCaregiverList(RowBounds rowBounds, ArrayList<Integer> resultCaregiverNoList);	

	Patient selectPatient(int memberNo);

	List<Integer> selectInfoCategory(int memberNo);


	int updatePassword(HashMap<String, String> changeInfo);

	int deleteWantInfo(int memberNo);

	int deleteMemberInfo(int memberNo);

	int insertWantInfo(HashMap<String, Integer> info);

	ArrayList<HashMap<String, String>> selectWantInfo(int memberNo);

	int updatePatient(Patient p);

	int insertMemberInfo(HashMap<String, Integer> info);

	int updateMember(Member m);

	CareGiver selectCareGiver(int memberNo);

	int updateCareGiver(CareGiver cg);

	int updateMemberVer2(Member m);
	ArrayList<Board> mySelectBoardList(int mNo, RowBounds rowBounds);

	int getBoardListCount(int mNo);

	int boardLikeCount(int boardNo);

	int getReplyListCount(int mNo);

	ArrayList<Reply> mySelectReplyList(int mNo, RowBounds rowBounds);

	int replyLikeCount(int replyNo);

	int getLikeListCount(int mNo);

	ArrayList<Board> mySelectLikeList(int mNo, RowBounds rowBounds);

	int likeLikeCount(int boardNo);
	
	ArrayList<MatMatptInfo> selectMatList(int memberNo);


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

	int selectReviewYn(@Param("matNo") int matNo, @Param("ptNo")int ptNo);

	Double avgReviewScore2(int memberNo);

	ArrayList<MatMatptInfo> selectMatListPay(int memberNo);

	ArrayList<MatMatptInfoPt> selectMatRecord(int memberNo);

	ArrayList<MatMatptInfoPt> monthCountList(int memberNo);

	int updateImageProfile(@Param("memberNo") String memberNo,@Param("rename") String rename);

	CareGiver selectProfile(String memberNo);

	int deleteMember(int memberNo);

	ArrayList<Pay> selectPayTransfer(int memberNo);
	
	int getCountPendingMe(@Param("matNo") int matNo, @Param("memberNo") int memberNo);

	int getCountPt(int matNo);

	Integer getDelMemberNo();

	Member findPwdResult(Member member);
	Matching selectEndDateMat(int matNo);

}
