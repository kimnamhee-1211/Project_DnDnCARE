package com.kh.dndncare.chating.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.dndncare.chating.model.vo.ChatingRoom;
import com.kh.dndncare.chating.model.vo.ChatingRoomMessage;

@Mapper
public interface ChatingMapper {

	ChatingRoom getChatRoom(@Param("memberNo") int memberNo, @Param("matNo") int matNo);

	int getMatMemberNo(int matPtNo);

	int insertChatRoom(ChatingRoom chatingRoom);

	int insertChatRoomMember(@Param("chatRoomNo") int chatRoomNo,@Param("memberNo") int memberNo, @Param("matMemberNo") int matMemberNo);

	int getChatRoomNo(int matPtNo);
	
	void insertMessage(ChatingRoomMessage message);
	
	List<ChatingRoomMessage> getMessagesByChatRoomNo(int chatRoomNo);

}
