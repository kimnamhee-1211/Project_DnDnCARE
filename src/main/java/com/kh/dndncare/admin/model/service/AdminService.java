package com.kh.dndncare.admin.model.service;

import java.util.ArrayList;

import com.kh.dndncare.admin.model.vo.Attachment;
import com.kh.dndncare.board.model.vo.Board;

public interface AdminService {

	int insertCareInfomation(Board b);

	int insertAttachment(ArrayList<Attachment> aList);

}
