package com.kh.dndncare.member.model.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.dndncare.matching.model.vo.Matching;

import com.kh.dndncare.member.model.vo.CalendarEvent;

import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

@Mapper
public interface MemberMapper {

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

	HashMap<String, String> getCaregiverInfo(int memberNo);

	ArrayList<HashMap<String, String>> getCaregiverExp(int memberNo);

	ArrayList<Patient> selectPatientList(String caregiverCity);

	ArrayList<HashMap<String, String>> getPatientExp(ArrayList<Integer> pNoList);

	ArrayList<Patient> choicePatientList(ArrayList<Integer> choiceNoList);

	

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

}
