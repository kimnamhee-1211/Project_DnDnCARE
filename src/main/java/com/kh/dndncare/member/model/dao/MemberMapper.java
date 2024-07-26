package com.kh.dndncare.member.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.kh.dndncare.member.model.vo.Member;

@Mapper
public interface MemberMapper {

	Member login(Member m);

}
