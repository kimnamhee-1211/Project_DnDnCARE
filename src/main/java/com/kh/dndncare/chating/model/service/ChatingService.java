package com.kh.dndncare.chating.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.kh.dndncare.chating.model.vo.ChatingRoom;
import com.kh.dndncare.chating.model.vo.ChatingRoomMessage;

public interface ChatingService {

	ChatingRoom getChatRoom(int memberNo, int chatRoomNo);

	int getMatMemberNo(int matPtNo);

	int insertChatRoom(ChatingRoom chatingRoom);

	int insertChatRoomMember(Integer finalChatRoomNo, int memberNo, int relatedMemberNo);

	int getChatRoomNo(Integer relatedMatPtNo);
	
    void saveMessage(ChatingRoomMessage message);
    
    List<ChatingRoomMessage> getMessagesByChatRoomNo(int chatRoomNo);

	ArrayList<ChatingRoom> getChatRoomList(int memberNo);

	List<ChatingRoomMessage> getLatestMessages(int memberNo);

	ChatingRoomMessage sendMessage(ChatingRoomMessage message);
    void markAsRead(int chatRoomNo, int memberNo);
    int getUnreadMessageCount(int chatRoomNo, int memberNo);
    int getParticipantCount(int chatRoomId);
    
    int getMessageReadCount(int messageId);
    List<Integer> markAsReadAndGetUpdatedMessages(int chatRoomNo, int memberNo);
    List<Map<String, Object>> getMessageReadCounts(int chatRoomNo);

	int getPtCount(Integer matNo);

	List<Integer> getMatPtNos(Integer matNo);

	int insertChatRoomMember2(int finalChatRoomNo, int cMemberNo, Integer firstMemberNo, Integer secondMemberNo);

	int insertChatRoomMember3(int finalChatRoomNo, int cMemberNo, Integer firstMemberNo, Integer secondMemberNo,
			Integer thirdMemberNo);

	List<Integer> getMatMemberNos2(Integer firstPtNo, Integer secondPtNo);

	List<Integer> getMatMemberNos3(Integer firstPtNo, Integer secondPtNo, Integer thirdPtNo);

	int getChatCount(Integer finalChatRoomNo);

	int getAlreadyChatRoomNo(Integer matNo, int memberNo, int relatedMemberNo);

	Integer getAlreadyChatRoomNo2(Integer matNo, int firstMemberNo, int secondMemberNo, int cMemberNo);

	Integer getAlreadyChatRoomNo3(Integer matNo, int firstMemberNo, int secondMemberNo, int thirdMemberNo,
			int cMemberNo);

	List<String> getParticipantMemberNames(Integer finalChatRoomNo);
    

}
