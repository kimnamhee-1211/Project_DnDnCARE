package com.kh.dndncare.member.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.member.model.dao.MemberMapper;

import com.kh.dndncare.member.model.vo.CalendarEvent;

import com.kh.dndncare.member.model.vo.CareGiver;
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
	public ArrayList<HashMap<String, String>> getPatientInfo(ArrayList<Integer> mNoList) {
		return mMapper.getPatientInfo(mNoList);
	}


	



}
