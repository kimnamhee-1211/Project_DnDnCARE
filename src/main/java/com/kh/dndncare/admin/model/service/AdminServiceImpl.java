package com.kh.dndncare.admin.model.service;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.admin.model.dao.AdminMapper;
import com.kh.dndncare.admin.model.vo.Attachment;
import com.kh.dndncare.board.model.vo.Board;

@Service
public class AdminServiceImpl implements AdminService{

	@Autowired
	private AdminMapper aMapper;

	@Override
	public int insertCareInfomation(Board b) {
		return aMapper.insertCareInfomation(b);
	}

	@Override
	public int insertAttachment(ArrayList<Attachment> aList) {
		return aMapper.insertAttachment(aList);
	}
}
