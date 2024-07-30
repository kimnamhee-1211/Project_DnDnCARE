package com.kh.dndncare.board.model.service;

import java.util.ArrayList;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.board.model.dao.BoardMapper;
import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.board.model.vo.Reply;

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

	@Override
	public int insertBoard(Board b) {
		return bMapper.insertBoard(b);
	}

	@Override
	public Board selectBoard(int bId, int memberNo) {
		Board b = bMapper.selectBoard(bId);
		if(b !=null) {
			if(memberNo != 0 && memberNo != b.getMemberNo()) {
				int result = bMapper.updateCount(bId);
				if(result>0) {
					b.setBoardCount(b.getBoardCount()+1);
				}
			}
		}
		return b;
	}

	@Override
	public ArrayList<Reply> selectReply(int bId) {
		return bMapper.selectReply(bId);
	}

	
}
