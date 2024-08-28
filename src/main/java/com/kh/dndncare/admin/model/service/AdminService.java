package com.kh.dndncare.admin.model.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import com.kh.dndncare.admin.model.vo.Attachment;
import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.board.model.vo.Reply;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.matching.model.vo.Pay;
import com.kh.dndncare.member.model.vo.Member;

public interface AdminService {

	int insertCareInfomation(Board b);

	int insertAttachment(ArrayList<Attachment> aList);

	int getCareInformationListCount();

	ArrayList<Board> selectAllCareInformation(PageInfo pi);

	ArrayList<Attachment> selectAttachment(ArrayList<Integer> bNoList);

	int changeStatusCareInformation(HashMap<String, Object> map);

	int changeStatusAttachment(HashMap<String, Object> map);

	ArrayList<Attachment> selectOneAttachment(int boardNo);

	int deleteAttachment(ArrayList<Integer> removeAttmNoList);

	Board selectOneBoard(int boardNo);

	int deleteThumbnail(int boardNo);

	int insertThumbnail(Attachment thumbnail);

	int updateCareInformation(Board b);

	ArrayList<Board> selectCaregiverBoardList(PageInfo cpi);

	ArrayList<Board> selectPatientBoardList(PageInfo ppi);

	int getCaregiverListCount();

	int getPatientListCount();
	ArrayList<Pay> selectPayDeposit(String type);
	int getMembersListCount();

	ArrayList<Member> selectWeekMembers(Object object, PageInfo pi);

	int getAllMembersListCount();

	ArrayList<Member> selectAllMembers(Object object, PageInfo pi);

	int getSearchMemberListCount(HashMap<String, String> map);

	ArrayList<Member> searchMembers(HashMap<String, String> map, PageInfo pi);

	int updateMembers(HashMap<String, Object> map);

	String getMemberAge(int memberNo);

	ArrayList<HashMap<String, Object>> getEnrollCount(HashMap<String, Integer> map);

	int insertAnnouncement(Board b);

	int updateAdminBoardStatus(int boardNo, String boardStatus);

	ArrayList<Board> adminSearchBoard(String searchType, String searchText, PageInfo pi);

	int getSearchListCountAll(String searchType, String searchText);

	Board adminSelectBoard(int bNo);

	ArrayList<Reply> adminSelectReply(int bNo);

	int adminDeleteBoard(int boardNo);

	int adminDeleteReply(int rNo);

	ArrayList<Matching> selectMatchings();
	int adminUpdateBoard(Board b);

	int checkAdminId(String memberId);

	int insertMember(Member m);



}
