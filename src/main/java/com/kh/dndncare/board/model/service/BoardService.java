package com.kh.dndncare.board.model.service;

import java.util.ArrayList;

import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;

public interface BoardService {

	int getListCountAll();

	ArrayList<Board> selectBoardAllList(PageInfo pi);

}
