package com.kh.dndncare.chating.model.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.dndncare.chating.model.vo.ChatingRoom;
import com.kh.dndncare.chating.model.vo.ChatingRoomMessage;

@Mapper
public interface ChatingMapper {

	ChatingRoom getChatRoom(@Param("memberNo") int memberNo, @Param("chatRoomNo") int chatRoomNo);

	int getMatMemberNo(int matPtNo);

	int insertChatRoom(ChatingRoom chatingRoom);

	int insertChatRoomMember(@Param("finalChatRoomNo") Integer finalChatRoomNo,@Param("memberNo") int memberNo, @Param("relatedMemberNo") int relatedMemberNo);

	int getChatRoomNo(Integer relatedMatPtNo);
	
	void insertMessage(ChatingRoomMessage message);
	
	List<ChatingRoomMessage> getMessagesByChatRoomNo(int chatRoomNo);

	ArrayList<ChatingRoom> getChatRoomList(int memberNo);

	List<ChatingRoomMessage> getLatestMessages(int memberNo);
	
    // 채팅방의 참여자 수를 조회
    int getParticipantCount(int chatRoomNo);

    // 메시지의 readCount 업데이트
    void updateMessageReadCount(@Param("chatMessageNo") int chatMessageNo, @Param("readCount") int readCount);

    // 사용자가 읽지 않은 메시지 조회
    List<ChatingRoomMessage> getUnreadMessages(@Param("chatRoomNo") int chatRoomNo, @Param("memberNo") int memberNo);

    // 읽지 않은 메시지 수 조회
    int getUnreadMessageCount(@Param("chatRoomNo") int chatRoomNo, @Param("memberNo") int memberNo);
    
    int getMessageReadCount(int messageId);

	void markMessagesAsRead(@Param("chatRoomNo") int chatRoomNo, @Param("memberNo") int memberNo);

	List<Integer> markAsReadAndGetUpdatedMessages(int chatRoomNo, int memberNo);

	List<Map<String, Object>> getMessageReadCounts(int chatRoomNo);

	int getPtCount(Integer matNo);

	List<Integer> getMatPtNos(Integer matNo);

	int insertChatRoomMember2(@Param("finalChatRoomNo") int finalChatRoomNo, @Param("cMemberNo") int cMemberNo, @Param("firstMemberNo") Integer firstMemberNo , @Param("secondMemberNo") Integer secondMemberNo);

	int insertChatRoomMember3(@Param("finalChatRoomNo") int finalChatRoomNo, @Param("cMemberNo") int cMemberNo, @Param("firstMemberNo") Integer firstMemberNo , @Param("secondMemberNo") Integer secondMemberNo,
							  @Param("thirdMemberNo") Integer thirdMemberNo);

	List<Integer> getMatMemberNos2(@Param("firstPtNo") Integer firstPtNo, @Param("secondPtNo") Integer secondPtNo);

	List<Integer> getMatMemberNos3(@Param("firstPtNo") Integer firstPtNo, @Param("secondPtNo") Integer secondPtNo, @Param("thirdPtNo") Integer thirdPtNo);

	
 
}
