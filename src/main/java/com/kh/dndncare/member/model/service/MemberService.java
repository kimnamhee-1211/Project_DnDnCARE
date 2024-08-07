package com.kh.dndncare.member.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.kh.dndncare.member.model.vo.CalendarEvent;

import java.util.List;

import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.member.model.vo.Matching;

import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

public interface MemberService {

	Member login(Member m);
	
	int noInfomemberdle();

	int idCheck(String id);
	
	int nickNameCheck(String nickName);


	ArrayList<CalendarEvent> caregiverCalendarEvent(Member loginUser);

	ArrayList<Member> selectAllMember();
	int enroll(Member m);

	int enrollCareGiver(CareGiver cg);

	int enrollInfoCategory(Object ob);

	int enrollPatient(Patient pt);


	Member findIdResult(Member member);

	boolean sendSms(String phoneNumber, String string);
	ArrayList<Matching> calendarEvent(Member loginUser);

	HashMap<String, String> getCaregiverInfo(int memberNo);

	ArrayList<HashMap<String, String>> getCaregiverExp(int memberNo);

	ArrayList<Patient> selectPatientList(HashMap<String, Object> condition);

	ArrayList<HashMap<String, String>> getPatientExp(ArrayList<Integer> pNoList);

	ArrayList<Patient> choicePatientList(ArrayList<Integer> choiceNoList);

	ArrayList<HashMap<String, String>> getCaregiverWant(int memberNo);

	ArrayList<HashMap<String, String>> getPatientInfo(ArrayList<Integer> mNoList);




}
