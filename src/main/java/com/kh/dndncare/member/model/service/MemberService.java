package com.kh.dndncare.member.model.service;

import java.util.List;

import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

public interface MemberService {

	Member login(Member m);

	int idCheck(String id);

	int enroll(Member m);

	int enrollCareGiver(CareGiver cg);

	int enrollInfoCategory(List<Integer> infoCategory);

	int enrollPatient(Patient pt);




}
