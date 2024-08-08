package com.kh.dndncare.member.model.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;


import com.kh.dndncare.member.model.vo.CalendarEvent;

import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.member.model.vo.Matching;

import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

@Mapper
public interface MemberMapper {

	Member login(Member m);

	int noInfomemberdle();
	
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



	



}
