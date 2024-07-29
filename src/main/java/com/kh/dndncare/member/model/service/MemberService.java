package com.kh.dndncare.member.model.service;

import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

public interface MemberService {

	Member login(Member m);

	int idCheck(String id);

	int enroll(Member m);

	int enrollCareGiver(CareGiver cg);

	int enrollnfoCategory(CareGiver cg);




}
