package com.kh.dndncare.member.model.service;

import java.util.ArrayList;

import com.kh.dndncare.member.model.vo.Member;

public interface MemberService {

	Member login(Member m);

	int idCheck(String id);

	ArrayList<Member> selectAllMember();

}
