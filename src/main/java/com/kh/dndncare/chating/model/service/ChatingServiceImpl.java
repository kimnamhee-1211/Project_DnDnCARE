package com.kh.dndncare.chating.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.dndncare.chating.model.dao.ChatingMapper;
import com.kh.dndncare.chating.model.vo.ChatingRoom;
import com.kh.dndncare.chating.model.vo.ChatingRoomMessage;

@Service
public class ChatingServiceImpl implements ChatingService {
	
	@Autowired
	private ChatingMapper chMapper;
	@Override
	public ChatingRoom getChatRoom(int memberNo, int chatRoomNo) {
		return chMapper.getChatRoom(memberNo,chatRoomNo);
	}
	@Override
	public int getMatMemberNo(int matPtNo) {
		return chMapper.getMatMemberNo(matPtNo);
	}
	@Override
	public int insertChatRoom(ChatingRoom chatingRoom) {
		return chMapper.insertChatRoom(chatingRoom);
	}
	@Override
	public int insertChatRoomMember(int chatRoomNo, int memberNo, int matMemberNo) {
		return chMapper.insertChatRoomMember(chatRoomNo,memberNo,matMemberNo);
	}
	@Override
	public int getChatRoomNo(int matPtNo) {
		return chMapper.getChatRoomNo(matPtNo);
	}
	@Override
    public void saveMessage(ChatingRoomMessage message) {
        chMapper.insertMessage(message);
    }

    @Override
    public List<ChatingRoomMessage> getMessagesByChatRoomNo(int chatRoomNo) {
        return chMapper.getMessagesByChatRoomNo(chatRoomNo);
    }
	@Override
	public ArrayList<ChatingRoom> getChatRoomList(int memberNo) {
		return chMapper.getChatRoomList(memberNo);
	}
	@Override
	public List<ChatingRoomMessage> getLatestMessages(int memberNo) {
		return chMapper.getLatestMessages(memberNo);
	}
    @Override
    public void sendMessage(ChatingRoomMessage message) {
        int participantCount = chMapper.getParticipantCount(message.getChatRoomNo());
        message.setReadCount(participantCount - 1);
        chMapper.insertMessage(message);
    }


    @Override
    public int getUnreadMessageCount(int chatRoomNo, int memberNo) {
        return chMapper.getUnreadMessageCount(chatRoomNo, memberNo);
    }
    
    @Override
    public int getParticipantCount(int chatRoomId) {
        return chMapper.getParticipantCount(chatRoomId);
    }
    
    @Override
    public int getMessageReadCount(int messageId) {
        return chMapper.getMessageReadCount(messageId);
    }
    @Override
    public void markAsRead(int chatRoomNo, int memberNo) {
        chMapper.markMessagesAsRead(chatRoomNo, memberNo);
    }
    @Override
    public List<Integer> markAsReadAndGetUpdatedMessages(int chatRoomNo, int memberNo) {
        return chMapper.markAsReadAndGetUpdatedMessages(chatRoomNo, memberNo);
    }

    @Override
    public List<Map<String, Object>> getMessageReadCounts(int chatRoomNo) {
        return chMapper.getMessageReadCounts(chatRoomNo);
    }
    

}
