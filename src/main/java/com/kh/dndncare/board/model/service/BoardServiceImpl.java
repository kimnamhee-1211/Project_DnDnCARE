package com.kh.dndncare.board.model.service;

import java.util.ArrayList;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.board.model.dao.BoardMapper;
import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;

@Service
public class BoardServiceImpl implements BoardService {
	
	@Autowired
	private BoardMapper bMapper;

	@Override
	public int getListCountAll() {
		return bMapper.getListCountAll();
	}

	@Override
	public ArrayList<Board> selectBoardAllList(PageInfo pi) {
		int offset = (pi.getCurrentPage() - 1)*pi.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, pi.getBoardLimit());
		return bMapper.selectBoardAllList(rowBounds);
	}
}
