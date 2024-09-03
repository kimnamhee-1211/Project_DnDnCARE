package com.kh.dndncare.board.model.service;

import java.util.ArrayList;
import java.util.HashMap;

import com.kh.dndncare.admin.model.vo.Attachment;
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

	int replyLikeCount(int rId);

	int getCareInfomationListCount(HashMap<String, String> map);

	ArrayList<Board> selectCareInformation(HashMap<String, String> map, PageInfo pi);

	ArrayList<Attachment> selectAttachment(ArrayList<Board> bList);

	int updateCareInformationCount(int boardNo);

	ArrayList<Board> searchCareInformation(HashMap<String, String> map, PageInfo pi);

	int getListCountQnA();

	ArrayList<Board> qnaBoardList(PageInfo pi);

	ArrayList<Board> myQnAList(int memberNo, PageInfo mpi);

	int getMyListCountQnA(int memberNo);







}
