package com.kh.dndncare.matching.model.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.matching.model.dao.MatchingMapper;
import com.kh.dndncare.matching.model.vo.Hospital;
import com.kh.dndncare.matching.model.vo.MatMatptInfo;
import com.kh.dndncare.matching.model.vo.MatPtInfo;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.member.model.vo.InfoCategory;
import com.kh.dndncare.member.model.vo.Patient;

@Service
public class MatchingServiceImpl implements MatchingService {

	@Autowired
	private MatchingMapper mMapper;

	//병원 테이블 등록
	@Override
	public int enrollHospital(Hospital ho) {
		
		return mMapper.enrollHospital(ho);
	}

	//매칭 테이블 등록
	@Override
	public int enrollMatching(Matching ma) {
		
		return mMapper.enrollMatching(ma);
	}

	//pt get
	@Override
	public Patient getPatient(int memberNo) {
		return mMapper.getPatient(memberNo);
	}
	
	//매칭 pt info 테이블 등록
	@Override
	public int enrollMatPtInfo(MatPtInfo jmPt) {
		return mMapper.enrollMatPtInfo(jmPt);
	}

	//병원으로 get Matching & MatPtInfo
	@Override
	public ArrayList<MatMatptInfo> getJmList(String hospitalName) {
		
		return mMapper.getJmList(hospitalName);
	}

	//matNo으로  get matching & ptinfo 테이블	
	@Override
	public MatMatptInfo getMatMatptInfo(int matNo) {
		return mMapper.getMatMatptInfo(matNo);
	}
	
	//matNo로 get 공동 간병 참여자들 Patient
	@Override
	public ArrayList<Patient> getPatientToMatNo(int matNo) {
		return mMapper.getPatientToMatNo(matNo);
	}

	//get member info (대분류 : 소분류)
	public ArrayList<InfoCategory> getInfo(int memberNo) {
		return mMapper.getInfo(memberNo);
	}

	@Override
	public int updatePatient(Patient patient) {
		return mMapper.updatePatient(patient);
	}

	@Override
	public int insertMatchingDate(String formattedDates) {
		return mMapper.insertMatchingDate(formattedDates);
	}




}
