package com.kh.dndncare.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.member.model.dao.MemberMapper;
import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;

@Service
public class MemberServiceImpl implements MemberService {
	@Autowired
	private MemberMapper mMapper;
	
	@Override
	public Member login(Member m) {
		return mMapper.login(m);
	}

	@Override
	public int idCheck(String id) {

		return mMapper.idCheck(id);
	}

	@Override
	public int enroll(Member m) {
		return mMapper.enroll(m);
	}

	@Override
	public int getMemberNo(String memberId) {
		return mMapper.getMemberNo(memberId);
	}

	@Override
	public int enrollCareGiver(CareGiver cg) {
		return  mMapper.enrollCareGiver(cg);
	}

	@Override
	public int enrollExpService(CareGiver cg) {
		return mMapper.enrollExpService(cg);
	}
	
	@Override
	public int enrollDisase(CareGiver cg, Patient pt, String category) {
		return mMapper.enrollDisase(cg, pt, category);
	}


	@Override
	public int enrollLicense(CareGiver cg) {
		return mMapper.enrollLicense(cg);
	}

	@Override
	public int enrollCaregiverWantPt(Patient pt) {
		// TODO Auto-generated method stub
		return mMapper.enrollCaregiverWantPt(pt);
	}

	@Override
	public int enrollDisaseLevel(CareGiver cg, Patient pt) {
		// TODO Auto-generated method stub
		return mMapper.enrollDisaseLevel(cg, pt);
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
