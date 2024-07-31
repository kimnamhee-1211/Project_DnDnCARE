package com.kh.dndncare.board.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.board.model.vo.Reply;

public interface BoardService {

	int getListCountAll(HashMap<String, Object> map);

	ArrayList<Board> selectBoardAllList(PageInfo pi, HashMap<String, Object> map);

	int insertBoard(Board b);

	Board selectBoard(int bId, int memberNo);

	ArrayList<Reply> selectReply(int bId);

	int getReplyCount(int boardNo);

	int insertReply(Reply r);

	int updateBoard(Board b);

	int deleteBoard(int bId);

	ArrayList<Board> searchBoard(PageInfo pi, HashMap<String, Object> map);

	int updateReply(Reply r);

	int deleteReply(int rId);

	int insertBoardLike(HashMap<String, Integer> map);

	int boardLikeCount(int boardNo);

	int insertReplyLike(HashMap<String, Integer> map);


}
