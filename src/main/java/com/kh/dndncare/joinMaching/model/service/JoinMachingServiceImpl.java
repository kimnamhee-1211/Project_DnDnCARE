package com.kh.dndncare.joinMaching.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.joinMaching.model.dao.JoinMachingMapper;
import com.kh.dndncare.joinMaching.model.vo.Hospital;
import com.kh.dndncare.matching.model.vo.MatPtInfo;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.member.model.vo.Patient;


@Service
public class JoinMachingServiceImpl implements JoinMachingService{

	@Autowired
	private JoinMachingMapper jmMapper;

	//병원 테이블 등록
	@Override
	public int enrollHospital(Hospital ho) {
		
		return jmMapper.enrollHospital(ho);
	}

	//매칭 테이블 등록
	@Override
	public int enrollMatching(Matching ma) {
		
		return jmMapper.enrollMatching(ma);
	}

	//pt get
	@Override
	public Patient getPatient(int memberNo) {
		return jmMapper.getPatient(memberNo);
	}
	
	//매칭 pt info 테이블 등록
	@Override
	public int enrollMatPtInfo(MatPtInfo gmPt) {
		return jmMapper.enrollMatPtInfo(gmPt);
	}


	
}
