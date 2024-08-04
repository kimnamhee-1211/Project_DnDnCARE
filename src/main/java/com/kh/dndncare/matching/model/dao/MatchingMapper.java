package com.kh.dndncare.matching.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.kh.dndncare.member.model.vo.Patient;

@Mapper
public interface MatchingMapper {

	Patient selectPatient(int memberNo);

}
