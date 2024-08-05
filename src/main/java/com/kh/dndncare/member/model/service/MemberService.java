package com.kh.dndncare.member.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.kh.dndncare.member.model.vo.CalendarEvent;

import java.util.List;

import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.matching.model.vo.Matching;
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

	ArrayList<HashMap<String, String>> getPatientExp(ArrayList<Integer> pNoList);

	ArrayList<Patient> choicePatientList(ArrayList<Integer> choiceNoList);



}
