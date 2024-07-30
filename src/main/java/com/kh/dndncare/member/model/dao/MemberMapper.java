package com.kh.dndncare.member.model.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

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

	ArrayList<Member> selectAllMember();
	int enroll(Member m);

	int enrollCareGiver(CareGiver cg);

	int enrollInfoCategory(Object ob);

	int enrollPatient(Patient pt);


	Member findIdResult(Member member);
	ArrayList<Matching> calendarEvent(Member loginUser);



}
