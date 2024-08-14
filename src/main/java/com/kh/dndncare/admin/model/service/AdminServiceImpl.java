package com.kh.dndncare.admin.model.service;

import java.util.ArrayList;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.admin.model.dao.AdminMapper;
import com.kh.dndncare.admin.model.vo.Attachment;
import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;

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

	@Override
	public int getCareInformationListCount() {
		return aMapper.getCareInformationListCount();
	}

	@Override
	public ArrayList<Board> selectAllCareInformation(PageInfo pi) {
		RowBounds rowBounds = new RowBounds((pi.getCurrentPage()-1)*pi.getBoardLimit(), pi.getBoardLimit());
		return aMapper.selectAllCareInformation(rowBounds); // 하나만 넘겨도 되려나?
	}

	@Override
	public ArrayList<Attachment> selectAttachment(ArrayList<Integer> bNoList) {
		return aMapper.selectAttachment(bNoList);
	}

	@Override
	public int hideCareInformation(int boardNo) {
		return aMapper.hideCareInformation(boardNo);
	}

	@Override
	public int hideAttachment(int boardNo) {
		return aMapper.hideAttachment(boardNo);
	}

	@Override
	public ArrayList<Attachment> selectOneAttachment(int boardNo) {
		return aMapper.selectOneAttachment(boardNo);
	}

	@Override
	public int deleteAttachment(ArrayList<Integer> removeAttmNoList) {
		return aMapper.deleteAttachment(removeAttmNoList);
	}

	@Override
	public Board selectOneBoard(int boardNo) {
		return aMapper.selectOneBoard(boardNo);
	}

	@Override
	public int deleteThumbnail(int boardNo) {
		return aMapper.deleteThumbnail(boardNo);
	}

	@Override
	public int insertThumbnail(Attachment thumbnail) {
		return aMapper.insertThumbnail(thumbnail);
	}

	@Override
	public int updateCareInformation(Board b) {
		return aMapper.updateCareInformation(b);
	}

}
