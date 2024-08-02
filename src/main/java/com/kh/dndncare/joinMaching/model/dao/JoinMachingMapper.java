package com.kh.dndncare.joinMaching.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.kh.dndncare.joinMaching.model.vo.Hospital;
import com.kh.dndncare.matching.model.vo.MatPtInfo;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.member.model.vo.Patient;

@Mapper
public interface JoinMachingMapper {

	int enrollHospital(Hospital ho);

	int enrollMatching(Matching ma);

	Patient getPatient(int memberNo);

	int enrollMatPtInfo(MatPtInfo gmPt);

}
