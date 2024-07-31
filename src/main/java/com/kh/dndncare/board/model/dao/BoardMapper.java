package com.kh.dndncare.board.model.dao;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.Reply;

@Mapper
public interface BoardMapper {

	int getListCountAll(HashMap<String, Object> map);

	ArrayList<Board> selectBoardAllList(HashMap<String, Object> map, RowBounds rowBounds);

	int reCount();

	int insertBoard(Board b);

	Board selectBoard(int bId);

	int updateCount(int bId);

	ArrayList<Reply> selectReply(int bId);

	int getReplyCount(int boardNo);

	int insertReply(Reply r);


}
