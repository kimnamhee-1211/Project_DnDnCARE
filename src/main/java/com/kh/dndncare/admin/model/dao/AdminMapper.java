package com.kh.dndncare.admin.model.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;

import com.kh.dndncare.admin.model.vo.Attachment;
import com.kh.dndncare.board.model.vo.Board;

@Mapper
public interface AdminMapper {

	int insertCareInfomation(Board b);

	int insertAttachment(ArrayList<Attachment> aList);

}
