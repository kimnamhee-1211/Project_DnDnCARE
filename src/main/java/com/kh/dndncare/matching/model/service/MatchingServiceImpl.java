package com.kh.dndncare.matching.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.matching.model.dao.MatchingMapper;
import com.kh.dndncare.member.model.vo.Patient;

@Service
public class MatchingServiceImpl implements MatchingService {
	@Autowired
	MatchingMapper mcMapper;
	@Override
	public Patient selectPatient(int memberNo) {
		return mcMapper.selectPatient(memberNo);
	}

}
