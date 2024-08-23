package com.kh.dndncare.admin.model.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.admin.model.dao.AdminMapper;
import com.kh.dndncare.admin.model.vo.Attachment;
import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.board.model.vo.Reply;
import com.kh.dndncare.matching.model.vo.Pay;
import com.kh.dndncare.member.model.vo.Member;

@Service
public class AdminServiceImpl implements AdminService{

	@Autowired
	private AdminMapper aMapper;

	@Override
	public int insertCareInfomation(Board b) {
		return aMapper.insertCareInfomation(b);
	}

	@Override
	public int insertAttachment(ArrayList<Attachment> aList) {
		return aMapper.insertAttachment(aList);
	}

	@Override
	public int getCareInformationListCount() {
		return aMapper.getCareInformationListCount();
	}

	@Override
	public ArrayList<Board> selectAllCareInformation(PageInfo pi) {
		RowBounds rowBounds = new RowBounds((pi.getCurrentPage()-1)*pi.getBoardLimit(), pi.getBoardLimit());
		return aMapper.selectAllCareInformation(rowBounds); // 하나만 넘겨도 되려나?
	}

	@Override
	public ArrayList<Attachment> selectAttachment(ArrayList<Integer> bNoList) {
		return aMapper.selectAttachment(bNoList);
	}

	@Override
	public int changeStatusCareInformation(HashMap<String, Object> map) {
		return aMapper.changeStatusCareInformation(map);
	}

	@Override
	public int changeStatusAttachment(HashMap<String, Object> map) {
		return aMapper.changeStatusAttachment(map);
	}

	@Override
	public ArrayList<Attachment> selectOneAttachment(int boardNo) {
		return aMapper.selectOneAttachment(boardNo);
	}

	@Override
	public int deleteAttachment(ArrayList<Integer> removeAttmNoList) {
		return aMapper.deleteAttachment(removeAttmNoList);
	}

	@Override
	public Board selectOneBoard(int boardNo) {
		return aMapper.selectOneBoard(boardNo);
	}

	@Override
	public int deleteThumbnail(int boardNo) {
		return aMapper.deleteThumbnail(boardNo);
	}

	@Override
	public int insertThumbnail(Attachment thumbnail) {
		return aMapper.insertThumbnail(thumbnail);
	}

	@Override
	public int updateCareInformation(Board b) {
		return aMapper.updateCareInformation(b);
	}

	@Override
	public ArrayList<Board> selectCaregiverBoardList(PageInfo cpi) {
		RowBounds rowBounds = new RowBounds((cpi.getCurrentPage()-1)*cpi.getBoardLimit(), cpi.getBoardLimit());
		return aMapper.selectCaregiverBoardList(rowBounds);
	}

	@Override
	public ArrayList<Board> selectPatientBoardList(PageInfo ppi) {
		RowBounds rowBounds = new RowBounds((ppi.getCurrentPage()-1)*ppi.getBoardLimit(), ppi.getBoardLimit());
		return aMapper.selectPatientBoardList(rowBounds);
	}

	@Override
	public int getCaregiverListCount() {
		return aMapper.getCaregiverListCount();
	}

	@Override
	public int getPatientListCount() {
		return aMapper.getPatientListCount();
	}
	public ArrayList<Pay> selectPayDeposit(String type) {
		return aMapper.selectPayDeposit(type);
	}

	public int getMembersListCount() {
		return aMapper.getMembersListCount();
	}

	@Override
	public ArrayList<Member> selectWeekMembers(Object object, PageInfo pi) {
		RowBounds rowBounds = new RowBounds((pi.getCurrentPage()-1)*pi.getBoardLimit(), pi.getBoardLimit());
		return aMapper.selectWeekMembers(null, rowBounds);
	}

	@Override
	public int getAllMembersListCount() {
		return aMapper.getAllMembersListCount();
	}

	@Override
	public ArrayList<Member> selectAllMembers(Object object, PageInfo pi) {
		RowBounds rowBounds = new RowBounds((pi.getCurrentPage()-1)*pi.getBoardLimit(), pi.getBoardLimit());
		return aMapper.selectAllMembers(null, rowBounds);
	}

	@Override
	public int getSearchMemberListCount(HashMap<String, String> map) {
		return aMapper.getSearchMemberListCount(map);
	}

	@Override
	public ArrayList<Member> searchMembers(HashMap<String, String> map, PageInfo pi) {
		RowBounds rowBounds = new RowBounds((pi.getCurrentPage()-1)*pi.getBoardLimit(), pi.getBoardLimit());
		return aMapper.searchMembers(map, rowBounds);
	}

	@Override
	public int updateMembers(HashMap<String, Object> map) {
		return aMapper.updateMembers(map);
	}

	@Override
	public String getMemberAge(int memberNo) {
		return aMapper.getMemberAge(memberNo);
	}

	@Override
	public ArrayList<HashMap<String, Object>> getEnrollCount(HashMap<String, Integer> map) {
		return aMapper.getEnrollCount(map);
	}

	@Override
	public int insertAnnouncement(Board b) {
		return aMapper.insertAnnouncement(b);
	}

	@Override
	public int updateAdminBoardStatus(int boardNo, String boardStatus) {
		return aMapper.updateAdminBoardStatus(boardNo, boardStatus);
	}

	@Override
	public ArrayList<Board> adminSearchBoard(String searchType, String searchText, PageInfo pi) {
		RowBounds rowBounds = new RowBounds((pi.getCurrentPage()-1)*pi.getBoardLimit(), pi.getBoardLimit());
		return aMapper.adminSearchBoard(searchType, searchText);
	}

	@Override
	public int getSearchListCountAll(String searchType, String searchText) {
		return aMapper.getSearchListCountAll(searchType, searchText);
	}

	@Override
	public Board adminSelectBoard(int bNo) {
		return aMapper.adminSelectBoard(bNo);
	}

	@Override
	public ArrayList<Reply> adminSelectReply(int bNo) {
		return aMapper.adminSelectReply(bNo);
	}

	@Override
	public int adminDeleteBoard(int boardNo) {
		return aMapper.adminDeleteBoard(boardNo);
	}

	@Override
	public int adminDeleteReply(int rNo) {
		return aMapper.adminDeleteReply(rNo);
	}

}
