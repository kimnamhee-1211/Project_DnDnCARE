package com.kh.dndncare.member.model.service;

import com.kh.dndncare.member.model.vo.Member;

public interface MemberService {

	Member login(Member m);

	int idCheck(String id);

	Member findIdResult(Member member);

	boolean sendSms(String phoneNumber, String string);

}
