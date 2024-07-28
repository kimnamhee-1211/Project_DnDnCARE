package com.kh.dndncare.member.model.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.member.model.dao.MemberMapper;
import com.kh.dndncare.member.model.vo.Matching;
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
	public ArrayList<Matching> calendarEvent(Member loginUser) {
		return mMapper.calendarEvent(loginUser);
	}

}
