package com.kh.dndncare.admin.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.kh.dndncare.admin.model.vo.Attachment;
import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;

public interface AdminService {

	int insertCareInfomation(Board b);

	int insertAttachment(ArrayList<Attachment> aList);

	int getCareInformationListCount();

	ArrayList<Board> selectAllCareInformation(PageInfo pi);

	ArrayList<Attachment> selectAttachment(ArrayList<Integer> bNoList);

	int changeStatusCareInformation(HashMap<String, Object> map);

	int changeStatusAttachment(HashMap<String, Object> map);

	ArrayList<Attachment> selectOneAttachment(int boardNo);

	int deleteAttachment(ArrayList<Integer> removeAttmNoList);

	Board selectOneBoard(int boardNo);

	int deleteThumbnail(int boardNo);

	int insertThumbnail(Attachment thumbnail);

	int updateCareInformation(Board b);


}
