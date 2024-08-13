package com.kh.dndncare.chating.model.service;

import java.util.ArrayList;
import java.util.List;

import com.kh.dndncare.chating.model.vo.ChatingRoom;
import com.kh.dndncare.chating.model.vo.ChatingRoomMessage;

public interface ChatingService {

	ChatingRoom getChatRoom(int memberNo, int chatRoomNo);

	int getMatMemberNo(int matPtNo);

	int insertChatRoom(ChatingRoom chatingRoom);

	int insertChatRoomMember(int chatRoomNo, int memberNo, int matMemberNo);

	int getChatRoomNo(int matPtNo);
	
    void saveMessage(ChatingRoomMessage message);
    
    List<ChatingRoomMessage> getMessagesByChatRoomNo(int chatRoomNo);

	ArrayList<ChatingRoom> getChatRoomList(int memberNo);

	List<ChatingRoomMessage> getLatestMessages(int memberNo);

}
