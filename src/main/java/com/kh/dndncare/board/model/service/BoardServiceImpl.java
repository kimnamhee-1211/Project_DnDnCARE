package com.kh.dndncare.board.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.admin.model.vo.Attachment;
import com.kh.dndncare.board.model.dao.BoardMapper;
import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.board.model.vo.Reply;

@Service
public class BoardServiceImpl implements BoardService {
	
	@Autowired
	private BoardMapper bMapper;

	@Override
	public int getListCountAll(HashMap<String, Object> map) {
		return bMapper.getListCountAll(map);
	}

	@Override
	public ArrayList<Board> selectBoardAllList(PageInfo pi, HashMap<String, Object> map) {
		int offset = (pi.getCurrentPage() - 1)*pi.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, pi.getBoardLimit());
		return bMapper.selectBoardAllList(map,rowBounds);
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

	@Override
	public int getReplyCount(int boardNo) {
		return bMapper.getReplyCount(boardNo);
	}

	@Override
	public int insertReply(Reply r) {
		return bMapper.insertReply(r);
	}

	@Override
	public int updateBoard(Board b) {
		return bMapper.updateBoard(b);
	}

	@Override
	public int deleteBoard(int bId) {
		return bMapper.deleteBoard(bId);
	}

	@Override
	public ArrayList<Board> searchBoard(PageInfo pi, HashMap<String, Object> map) {
		int offset = (pi.getCurrentPage() - 1)*pi.getBoardLimit();
		RowBounds rowBounds = new RowBounds(offset, pi.getBoardLimit());
		return bMapper.searchBoard(rowBounds, map);
	}

	@Override
	public int updateReply(Reply r) {
		return bMapper.updateReply(r);
	}

	@Override
	public int deleteReply(int rId) {
		return bMapper.deleteReply(rId);
	}

	@Override
	public int insertBoardLike(HashMap<String, Integer> map) {
		return bMapper.insertBoardLike(map);
	}

	@Override
	public int boardLikeCount(int boardNo) {
		return bMapper.boardLikeCount(boardNo);
	}

	@Override
	public int insertReplyLike(HashMap<String, Integer> map) {
		return bMapper.insertReplyLike(map);
	}

	@Override
	public int replyLikeCount(int rId) {
		return bMapper.replyLikeCount(rId);
	}

	@Override
	public int getCareInfomationListCount(HashMap<String, String> map) {
		return bMapper.getCareInfomationListCount(map);
	}

	@Override
	public ArrayList<Board> selectCareInformation(HashMap<String, String> map, PageInfo pi) {
		RowBounds rowBounds = new RowBounds((pi.getCurrentPage()-1)*pi.getBoardLimit(), pi.getBoardLimit());
		return bMapper.selectCareInformation(map, rowBounds);
	}

	@Override
	public ArrayList<Attachment> selectAttachment(ArrayList<Board> bList) {
		return bMapper.selectAttachment(bList);
	}

	@Override
	public int updateCareInformationCount(int boardNo) {
		return bMapper.updateCareInformationCount(boardNo);
	}

	@Override
	public ArrayList<Board> searchCareInformation(HashMap<String, String> map, PageInfo pi) {
		RowBounds rowBounds = new RowBounds((pi.getCurrentPage()-1)*pi.getBoardLimit(), pi.getBoardLimit());
		return bMapper.searchCareInformation(map, rowBounds);
	}




	
}
