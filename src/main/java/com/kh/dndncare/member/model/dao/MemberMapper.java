package com.kh.dndncare.member.model.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

import com.kh.dndncare.member.model.vo.Matching;
import com.kh.dndncare.member.model.vo.Member;

@Mapper
public interface MemberMapper {

	Member login(Member m);

	int idCheck(String id);

	ArrayList<Matching> calendarEvent(Member loginUser);

}
