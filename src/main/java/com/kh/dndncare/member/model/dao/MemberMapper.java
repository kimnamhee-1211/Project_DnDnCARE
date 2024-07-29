package com.kh.dndncare.member.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.kh.dndncare.member.model.vo.CareGiver;
import com.kh.dndncare.member.model.vo.Member;
import com.kh.dndncare.member.model.vo.Patient;


@Mapper
public interface MemberMapper {

	Member login(Member m);

	int idCheck(String id);

	int enroll(Member m);

	int enrollCareGiver(CareGiver cg);

	int enrollnfoCategory(CareGiver cg);



}
