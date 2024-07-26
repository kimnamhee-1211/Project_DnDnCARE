package com.kh.dndncare.board.model.dao;

import java.util.ArrayList;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.kh.dndncare.board.model.vo.Board;

@Mapper
public interface BoardMapper {

	int getListCountAll();

	ArrayList<Board> selectBoardAllList(RowBounds rowBounds);

}
