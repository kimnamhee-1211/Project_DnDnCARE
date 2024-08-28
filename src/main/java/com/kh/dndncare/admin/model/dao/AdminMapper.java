package com.kh.dndncare.admin.model.dao;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.kh.dndncare.admin.model.vo.Attachment;
import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.Reply;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.matching.model.vo.Pay;
import com.kh.dndncare.member.model.vo.Member;

@Mapper
public interface AdminMapper {

	int insertCareInfomation(Board b);

	int insertAttachment(ArrayList<Attachment> aList);

	int getCareInformationListCount();

	ArrayList<Board> selectAllCareInformation(RowBounds rowBounds);

	ArrayList<Attachment> selectAttachment(ArrayList<Integer> bNoList);

	int changeStatusCareInformation(HashMap<String, Object> map);

	int changeStatusAttachment(HashMap<String, Object> map);

	ArrayList<Attachment> selectOneAttachment(int boardNo);

	int deleteAttachment(ArrayList<Integer> removeAttmNoList);

	Board selectOneBoard(int boardNo);

	int deleteThumbnail(int boardNo);

	int insertThumbnail(Attachment thumbnail);

	int updateCareInformation(Board b);

	ArrayList<Board> selectCaregiverBoardList(RowBounds rowBounds);

	ArrayList<Board> selectPatientBoardList(RowBounds rowBounds);

	int getCaregiverListCount();

	int getPatientListCount();
	ArrayList<Pay> selectPayDeposit(String type);

	int getMembersListCount();

	ArrayList<Member> selectWeekMembers(Object object, RowBounds rowBounds);

	int getAllMembersListCount();

	ArrayList<Member> selectAllMembers(Object object, RowBounds rowBounds);

	int getSearchMemberListCount(HashMap<String, String> map);

	ArrayList<Member> searchMembers(HashMap<String, String> map, RowBounds rowBounds);

	int updateMembers(HashMap<String, Object> map);

	String getMemberAge(int memberNo);

	ArrayList<HashMap<String, Object>> getEnrollCount(HashMap<String, Integer> map);

	int insertAnnouncement(Board b);

	int updateAdminBoardStatus(@Param("boardNo") int boardNo, @Param("boardStatus") String boardStatus);

	ArrayList<Board> adminSearchBoard(@Param("searchType") String searchType, @Param("searchText") String searchText);

	int getSearchListCountAll(@Param("searchType") String searchType, @Param("searchText") String searchText);

	Board adminSelectBoard(int bNo);

	ArrayList<Reply> adminSelectReply(int bNo);

	int adminDeleteBoard(int boardNo);

	int adminDeleteReply(int rNo);

	ArrayList<Matching> selectMatchings();
	
	int adminUpdateBoard(Board b);

	int getAdminQnABoardCount();
	
	ArrayList<Board> adminQnABoardList(RowBounds rowBounds);



}
