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


}
