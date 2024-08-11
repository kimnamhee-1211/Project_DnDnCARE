package com.kh.dndncare.member.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.member.model.dao.MemberMapper;
import com.kh.dndncare.member.model.vo.CalendarEvent;
import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.member.model.vo.MatPtInfo;
import com.kh.dndncare.member.model.vo.Matching;
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
	public ArrayList<CalendarEvent> caregiverCalendarEvent(Integer memberNo) {
		return mMapper.caregiverCalendarEvent(memberNo);
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



	



}
