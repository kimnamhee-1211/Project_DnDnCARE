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
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.member.model.dao.MemberMapper;

import com.kh.dndncare.member.model.vo.CalendarEvent;

import com.kh.dndncare.member.model.vo.CareGiver;
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
	
	@Override
	public int noInfomemberdle() {
		return mMapper.noInfomemberdle();
	}


	@Override
	public int idCheck(String id) {

		return mMapper.idCheck(id);
	}
	
	@Override
	public int nickNameCheck(String nickName) {
		return mMapper.nickNameCheck(nickName);
	}

	
	


	@Override
	public ArrayList<CalendarEvent> caregiverCalendarEvent(Member loginUser) {
		return mMapper.caregiverCalendarEvent(loginUser);
	}
	
	public ArrayList<Member> selectAllMember() {
		return mMapper.selectAllMember();
	}

	public int enroll(Member m) {
		return mMapper.enroll(m);
	}

	@Override
	public int enrollCareGiver(CareGiver cg) {
		return  mMapper.enrollCareGiver(cg);
	}

	@Override
	public int enrollInfoCategory(Object ob) {
		return mMapper.enrollInfoCategory(ob);
	}

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
	public ArrayList<Matching> calendarEvent(Member loginUser) {
		// TODO Auto-generated method stub 이거 뭐여
		return null;
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

}
