package com.kh.dndncare.board.model.service;

import java.util.ArrayList;

import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.board.model.vo.Reply;

public interface BoardService {

	int getListCountAll();

	ArrayList<Board> selectBoardAllList(PageInfo pi);

	int insertBoard(Board b);

	Board selectBoard(int bId, int memberNo);

	ArrayList<Reply> selectReply(int bId);

}
