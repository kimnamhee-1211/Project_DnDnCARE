package com.kh.dndncare.member.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.member.model.dao.MemberMapper;
import com.kh.dndncare.member.model.vo.Member;

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
	public Member findIdResult(Member member) {
		return mMapper.findIdResult(member);
	}

}
