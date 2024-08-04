package com.kh.dndncare.matching.model.service;

import com.kh.dndncare.member.model.vo.Patient;

public interface MatchingService {

	Patient selectPatient(int memberNo);

}
