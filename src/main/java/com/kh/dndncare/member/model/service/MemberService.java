package com.kh.dndncare.member.model.service;

<<<<<<< HEAD
import java.util.ArrayList;

=======
import java.util.List;

import com.kh.dndncare.member.model.vo.CareGiver;
>>>>>>> refs/remotes/origin/namhee
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

public interface MemberService {

	Member login(Member m);

	int idCheck(String id);

<<<<<<< HEAD
	ArrayList<Member> selectAllMember();
=======
	int enroll(Member m);

	int enrollCareGiver(CareGiver cg);

	int enrollInfoCategory(List<Integer> infoCategory);

	int enrollPatient(Patient pt);



>>>>>>> refs/remotes/origin/namhee

}
