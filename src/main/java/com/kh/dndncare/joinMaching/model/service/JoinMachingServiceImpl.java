package com.kh.dndncare.joinMaching.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.joinMaching.model.dao.JoinMachingMapper;


@Service
public class JoinMachingServiceImpl implements JoinMachingService{

	@Autowired
	private JoinMachingMapper jmMapper;
	
}
