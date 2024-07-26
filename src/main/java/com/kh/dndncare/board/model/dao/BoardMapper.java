package com.kh.dndncare.board.model.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.Reply;

@Mapper
public interface BoardMapper {

	int getListCountAll();

	ArrayList<Board> selectBoardAllList(String category, RowBounds rowBounds);

	int reCount();

	int insertBoard(Board b);

	Board selectBoard(int bId);

	int updateCount(int bId);

	ArrayList<Reply> selectReply(int bId);

}
