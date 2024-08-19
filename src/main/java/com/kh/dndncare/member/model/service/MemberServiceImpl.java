package com.kh.dndncare.member.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.member.model.dao.MemberMapper;
import com.kh.dndncare.member.model.vo.CalendarEvent;
import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.member.model.vo.CareGiverMin;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;
import com.kh.dndncare.sms.SmsService;

import net.nurigo.sdk.message.response.SingleMessageSentResponse;

@Service
public class MemberServiceImpl implements MemberService {
	
	private final SmsService smsService;
	
    public MemberServiceImpl(SmsService smsService) {
        this.smsService = smsService;
    }
	
	@Autowired
	private MemberMapper mMapper;
	
	@Override
	public Member login(Member m) {
		return mMapper.login(m);
	}
	
	
	//// 멤버 테이블만 있고 환자/ 간병인 테이블에 insert됮 않은 경우 멤버 테이블 삭제
	@Override
	public int noInfomemberdle() {
		return mMapper.noInfomemberdle();
	}

	//아이디 중복체크
	@Override
	public int idCheck(String id) {

		return mMapper.idCheck(id);
	}
	
	//닉네임 중복 체크
	@Override
	public int nickNameCheck(String nickName) {
		return mMapper.nickNameCheck(nickName);
	}

	
	


	@Override
	public ArrayList<CalendarEvent> caregiverCalendarEvent(Integer memberNo) {
		return mMapper.caregiverCalendarEvent(memberNo);
	}
	
	public ArrayList<Member> selectAllMember() {
		return mMapper.selectAllMember();
	}

	//member테이블 insert(회원가입)
	public int enroll(Member m) {
		return mMapper.enroll(m);
	}

	//간병인 테이블 insert(회원가입 -간병인)
	@Override
	public int enrollCareGiver(CareGiver cg) {
		return  mMapper.enrollCareGiver(cg);
	}

	//member_info insert (회원가입)
	@Override
	public int enrollInfoCategory(Object ob) {
		return mMapper.enrollInfoCategory(ob);
	}

	
	///환자 테이블 insert (환자 회원가입)
	@Override
	public int enrollPatient(Patient pt) {
		return  mMapper.enrollPatient(pt);
	}
	

	@Override
	public Member findIdResult(Member member) {
		return mMapper.findIdResult(member);
	}

	@Override
	public boolean sendSms(String phoneNumber, String text) {
	    try {
	        SingleMessageSentResponse response = smsService.sendSms(phoneNumber, "01077651258", text);
	        return response.getStatusCode().equals("2000");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}


	@Override
	public Patient selectPatient(int memberNo) {
		return mMapper.selectPatient(memberNo);
	}
	public HashMap<String, String> getCaregiverInfo(int memberNo) {
		return mMapper.getCaregiverInfo(memberNo);
	}

	@Override
	public ArrayList<HashMap<String, String>> getCaregiverExp(int memberNo) {
		return mMapper.getCaregiverExp(memberNo);
	}

	@Override
	public ArrayList<Patient> selectPatientList(HashMap<String, Object> condition) {
		return mMapper.selectPatientList(condition);
	}

	@Override
	public ArrayList<HashMap<String, String>> getPatientExp(ArrayList<Integer> pNoList) {
		return mMapper.getPatientExp(pNoList);
	}

	@Override
	public ArrayList<Patient> choicePatientList(ArrayList<Integer> choiceNoList) {
		return mMapper.choicePatientList(choiceNoList);
	}

	@Override
	public ArrayList<HashMap<String, String>> getCaregiverWant(int memberNo) {
		return mMapper.getCaregiverWant(memberNo);
	}

	@Override
	public ArrayList<HashMap<String, Object>> getPatientInfo(ArrayList<Integer> mNoList) {
		return mMapper.getPatientInfo(mNoList);
	}

	@Override
	public HashMap<String, String> getPatientMyInfo(int memberNo) {
		return mMapper.getPatientMyInfo(memberNo);
	}

	@Override
	public ArrayList<HashMap<String, String>> getPatientMyExp(int memberNo) {
		return mMapper.getPatientMyExp(memberNo);
	}

	@Override
	public ArrayList<HashMap<String, String>> getCaregiverMyWant(int memberNo) {
		return mMapper.getCaregiverMyWant(memberNo);
	}

	@Override
	public ArrayList<HashMap<String, Object>> selectCaregiverList(HashMap<String, Object> condition) {
		return mMapper.selectCaregiverList(condition);
	}

	@Override
	public ArrayList<HashMap<String, Object>> selectCaregiverInfo(ArrayList<Integer> mNoList) {
		return mMapper.selectCaregiverInfo(mNoList);
	}

	@Override
	public ArrayList<CareGiver> choiceCaregiverList(ArrayList<Integer> choiceNoList) {
		return mMapper.choiceCaregiverList(choiceNoList);
	}

	@Override
	public ArrayList<HashMap<String, Integer>> getPatientEvent(int memberNo) {
		return mMapper.getPatientEvent(memberNo);
	}

	@Override
	public ArrayList<CalendarEvent> patientCalendarEvent(ArrayList<Integer> matNoList) {
		return mMapper.patientCalendarEvent(matNoList);
	}

	@Override
	public ArrayList<Member> selectMemberList(ArrayList<Integer> memberNoList) {
		return mMapper.selectMemberList(memberNoList);
	}

	@Override
	public ArrayList<Matching> selectMatchingList(PageInfo pi, ArrayList<Integer> resultMatNoList) {
		RowBounds rowBounds = new RowBounds((pi.getCurrentPage()-1)*pi.getBoardLimit(), pi.getBoardLimit());
		return mMapper.selectMatchingList(rowBounds, resultMatNoList);
	}

	@Override
	public int getMatchingListCount(HashMap<String, Object> searchOption) {
		return mMapper.getMatchingListCount(searchOption);
	}

	@Override
	public ArrayList<Member> selectMatchingMemberList(ArrayList<Integer> matNoList) {
		return mMapper.selectMatchingMemberList(matNoList);
	}

	@Override
	public ArrayList<MatPtInfo> selectMatchingPTInfoList(ArrayList<Integer> matNoList) {
		return mMapper.selectMatchingPTInfoList(matNoList);
	}

	@Override
	public ArrayList<HashMap<String, Integer>> searchDefaultMatNoList(HashMap<String, Object> searchDefaultMap) {
		return mMapper.searchDefaultMatNoList(searchDefaultMap);
	}

	@Override
	public ArrayList<Integer> searchTermMatNoList(HashMap<String, Object> termMap) {
		return mMapper.searchTermMatNoList(termMap);
	}

	@Override
	public ArrayList<Integer> searchTimeMatNoList(HashMap<String, Object> termMap) {
		return mMapper.searchTimeMatNoList(termMap);
	}

	@Override
	public ArrayList<HashMap<String, Integer>> searchCategoryMatNoList(ArrayList<Integer> tempMatNoList) {
		return mMapper.searchCategoryMatNoList(tempMatNoList);
	}

	@Override
	public ArrayList<Matching> searchMatchingList(PageInfo pi, ArrayList<Integer> resultMatNoList) {
		RowBounds rowBounds = new RowBounds((pi.getCurrentPage()-1)*pi.getBoardLimit(), pi.getBoardLimit());
		return mMapper.searchMatchingList(rowBounds, resultMatNoList);
	}

	@Override
	public ArrayList<CareGiver> selectAllCaregiver(PageInfo pi) {
		RowBounds rowBounds = new RowBounds((pi.getCurrentPage()-1)*pi.getBoardLimit(), pi.getBoardLimit());
		return mMapper.selectAllCaregiver(rowBounds, null);
	}

	@Override
	public int getCaregiverListCount() {
		return mMapper.getCaregiverListCount();
	}

	@Override
	public ArrayList<HashMap<String, Integer>> getCaregiverScoreList(ArrayList<Integer> cNoList) {
		return mMapper.getCaregiverScoreList(cNoList);
	}

	@Override
	public ArrayList<CareGiver> searchDefaultCaregiverNoList(HashMap<String, Object> searchDefaultMap) {
		return mMapper.searchDefaultCaregiverNoList(searchDefaultMap);
	}

	@Override
	public ArrayList<HashMap<String, Integer>> searchCaregiverCategoryMNoList(ArrayList<Integer> cNoList) {
		return mMapper.searchCaregiverCategoryMNoList(cNoList);
	}

	@Override
	public ArrayList<CareGiver> searchCaregiverList(PageInfo pi, ArrayList<Integer> resultCaregiverNoList) {
		RowBounds rowBounds = new RowBounds((pi.getCurrentPage()-1)*pi.getBoardLimit(), pi.getBoardLimit());
		return mMapper.searchCaregiverList(rowBounds, resultCaregiverNoList);
	}



	


	@Override
	public List<Integer> selectInfoCategory(int memberNo) {
		return mMapper.selectInfoCategory(memberNo);
	}


	public int updatePassword(HashMap<String, String> changeInfo) {
		return mMapper.updatePassword(changeInfo);
	}

	@Override
	public List<Integer> selectMemberInfo(int memberNo) {
		return null;
		/* return mMapper.selectMemberInfo(memberNo); */
	}

	@Override
	public int deleteWantInfo(int memberNo) {
		return mMapper.deleteWantInfo(memberNo);
	}

	@Override
	public int insertWantInfo(HashMap<String, Integer> info) {
		return mMapper.insertWantInfo(info);
	}

	@Override
	public ArrayList<HashMap<String, String>> selectWantInfo(int memberNo) {
		return mMapper.selectWantInfo(memberNo);
	}

	@Override
	public int updatePatient(Patient p) {
		return mMapper.updatePatient(p);
	}

	@Override
	public int insertMemberInfo(HashMap<String, Integer> info) {
		return mMapper.insertMemberInfo(info);
	}

	@Override
	public int deleteMemberInfo(int memberNo) {
		return mMapper.deleteMemberInfo(memberNo);
	}

	@Override
	public int updateMember(Member m) {
		return mMapper.updateMember(m);
	}

	@Override
	public CareGiver selectCareGiver(int memberNo) {
		return mMapper.selectCareGiver(memberNo);
	}

	@Override
	public int updateCareGiver(CareGiver cg) {
		return mMapper.updateCareGiver(cg);
	}

	@Override
	public int updateMemberVer2(Member m) {
		return mMapper.updateMemberVer2(m);
	}
	
	public ArrayList<Board> mySelectBoardList(PageInfo pi,int mNo) {
		int offset = (pi.getCurrentPage() - 1)*pi.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, pi.getBoardLimit());
		return mMapper.mySelectBoardList(mNo, rowBounds);
	}

	@Override
	public int getBoardListCount(int mNo) {
		return mMapper.getBoardListCount(mNo);
	}

	@Override
	public int boardLikeCount(int boardNo) {
		return mMapper.boardLikeCount(boardNo);
	}

	@Override
	public int getReplyListCount(int mNo) {
		return mMapper.getReplyListCount(mNo);
	}

	@Override
	public ArrayList<Reply> mySelectReplyList(PageInfo replyPi, int mNo) {
		int offset = (replyPi.getCurrentPage() - 1)*replyPi.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, replyPi.getBoardLimit());
		return mMapper.mySelectReplyList(mNo, rowBounds);
	}

	@Override
	public int replyLikeCount(int replyNo) {
		return mMapper.replyLikeCount(replyNo);
	}

	@Override
	public int getLikeListCount(int mNo) {
		return mMapper.getLikeListCount(mNo);
	}

	@Override
	public ArrayList<Board> mySelectLikeList(PageInfo likePi, int mNo) {
		int offset = (likePi.getCurrentPage() - 1)*likePi.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, likePi.getBoardLimit());
		return mMapper.mySelectLikeList(mNo, rowBounds);
	}

	@Override
	public int likeLikeCount(int boardNo) {
		return mMapper.likeLikeCount(boardNo);
	}
	@Override
	public ArrayList<MatMatptInfo> selectMatList(int memberNo) {
		return mMapper.selectMatList(memberNo);
	}
	
	//MatMatptInfoPt get - 환자매칭 모든 정보
	@Override
	public ArrayList<MatMatptInfoPt> getMatMatptInfoPt(int memberNo) {
		return mMapper.getMatMatptInfoPt(memberNo);
	}
	
	//loginUser(간병인)에게 매칭을 신청한 대상 이름 불러오기
	@Override
	public ArrayList<RequestMatPt> getRequestMatPt(int memberNo) {
		return mMapper.getRequestMatPt(memberNo);
	}
		
	@Override
	public ArrayList<CareGiver> selectCareGiverList() {
		return mMapper.selectCareGiverList();

	}

	@Override
	public Member selectSocialLogin(String code) {
		return mMapper.selectSocialLogin(code);
	}
	public ArrayList<CareReview> reviewList(int ptNo) {
		return mMapper.reviewList(ptNo);
	}

	@Override
	public ArrayList<CareReview> selectReviewList(int reviewNo) {
		return mMapper.selectReviewList(reviewNo);
	}

	@Override
	public int getPtNo(int memberNo) {
		return mMapper.getPtNo(memberNo);
	}

	
	//매칭 신청한 간병인 목록 가져오기
	@Override
	public ArrayList<CareGiverMin> getRequestCaregiver(int ptNo) {
		return mMapper.getRequestCaregiver(ptNo);
	}

	@Override
	public ArrayList<Patient> selectPatientList(String caregiverCity) {
		return null;	//죽은메소드라고함

	}


	@Override
	public void nn(int mId) {
		mMapper.nn(mId);
	}


	@Override
	public ArrayList<CareReview> caregiverReviewList(int memberNo) {
		return mMapper.caregiverReviewList(memberNo);
	}


	@Override
	public ArrayList<CareReview> monthScoreList(int memberNo) {
		return mMapper.monthScoreList(memberNo);
	}


	@Override
	public ArrayList<CareReview> sumAvgScore(int memberNo) {
		return mMapper.sumAvgScore(memberNo);
	}


	@Override
	public ArrayList<MatMatptInfoPt> useMonth(int ptNo) {
		return mMapper.useMonth(ptNo);
	}


	@Override
	public int selectReviewYn(int matNo, int ptNo) {
		return mMapper.selectReviewYn(matNo, ptNo);
	}


	@Override
	public Double avgReviewScore2(int memberNo) {
		return mMapper.avgReviewScore2(memberNo);
	}


	@Override
	public ArrayList<MatMatptInfo> selectMatListPay(int memberNo) {
		return mMapper.selectMatListPay(memberNo);
	}


	@Override
	public int updateImageProfile(String memberNo, String rename) {
		return mMapper.updateImageProfile(memberNo,rename);
	}


	@Override
	public CareGiver selectProfile(String memberNo) {
		return mMapper.selectProfile(memberNo);
	}


	@Override
	public int deleteMember(int memberNo) {
		return mMapper.deleteMember(memberNo);
	}


	@Override
	public ArrayList<Pay> selectPayTransfer(int memberNo) {
		return mMapper.selectPayTransfer(memberNo);
	}
	public int getCountPendingMe(int matNo, int memberNo) {
		// TODO Auto-generated method stub
		return mMapper.getCountPendingMe(matNo, memberNo);
	}

}
