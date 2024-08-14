package com.kh.dndncare.admin.model.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.kh.dndncare.admin.model.vo.Attachment;
import com.kh.dndncare.board.model.vo.Board;

@Mapper
public interface AdminMapper {

	int insertCareInfomation(Board b);

	int insertAttachment(ArrayList<Attachment> aList);

	int getCareInformationListCount();

	ArrayList<Board> selectAllCareInformation(RowBounds rowBounds);

	ArrayList<Attachment> selectAttachment(ArrayList<Integer> bNoList);

	int hideCareInformation(int boardNo);

	int hideAttachment(int boardNo);

	ArrayList<Attachment> selectOneAttachment(int boardNo);

	int deleteAttachment(ArrayList<Integer> removeAttmNoList);

	Board selectOneBoard(int boardNo);

	int deleteThumbnail(int boardNo);

	int insertThumbnail(Attachment thumbnail);

	int updateCareInformation(Board b);


}
