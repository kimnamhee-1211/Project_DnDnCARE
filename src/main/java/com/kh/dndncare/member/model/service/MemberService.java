package com.kh.dndncare.member.model.service;

import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

public interface MemberService {

	Member login(Member m);

	int idCheck(String id);

	int enroll(Member m);

	int getMemberNo(String memberId);

	int enrollCareGiver(CareGiver cg);

	int enrollExpService(CareGiver cg);
	
	int enrollDisase(CareGiver cg, Patient pt, String category);

	int enrollLicense(CareGiver cg);

	int enrollCaregiverWantPt(Patient pt);

	int enrollDisaseLevel(CareGiver cg, Patient pt);


}
