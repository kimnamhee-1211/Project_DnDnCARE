package com.kh.dndncare.chating.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	public int insertChatRoomMember(Integer finalChatRoomNo, int memberNo, int relatedMemberNo) {
		return chMapper.insertChatRoomMember(finalChatRoomNo,memberNo,relatedMemberNo);
	}
	@Override
	public int getChatRoomNo(Integer relatedMatPtNo) {
		return chMapper.getChatRoomNo(relatedMatPtNo);
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
	public ChatingRoomMessage sendMessage(ChatingRoomMessage message) {
	    int participantCount = chMapper.getParticipantCount(message.getChatRoomNo());
	    message.setReadCount(participantCount - 1);
	    chMapper.insertMessage(message);
	    return message;
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
    @Transactional
    public void markAsRead(int chatRoomNo, int memberNo) {
        chMapper.markMessagesAsRead(chatRoomNo, memberNo);
        chMapper.updateReadByMembers(chatRoomNo, memberNo);
    }
    @Override
    public List<Integer> markAsReadAndGetUpdatedMessages(int chatRoomNo, int memberNo) {
        return chMapper.markAsReadAndGetUpdatedMessages(chatRoomNo, memberNo);
    }

    @Override
    public List<Map<String, Object>> getMessageReadCounts(int chatRoomNo) {
        List<ChatingRoomMessage> messages = chMapper.getMessagesByChatRoomNo(chatRoomNo);
        
        
        return messages.stream().map(message -> {
            Map<String, Object> readCountInfo = new HashMap<>();
            readCountInfo.put("CHAT_MASSAGE_NO", message.getChatMassageNo());
            readCountInfo.put("READ_COUNT", message.getReadCount());
            return readCountInfo;
        }).collect(Collectors.toList());
    }
    
	@Override
	public int getPtCount(Integer matNo) {
		return chMapper.getPtCount(matNo);
	}
	@Override
	public List<Integer> getMatPtNos(Integer matNo) {
		return chMapper.getMatPtNos(matNo);
	}
	@Override
	public int insertChatRoomMember2(int finalChatRoomNo, int cMemberNo, Integer firstMemberNo,
			Integer secondMemberNo) {
		return chMapper.insertChatRoomMember2(finalChatRoomNo,cMemberNo,firstMemberNo,secondMemberNo);
	}
	@Override
	public int insertChatRoomMember3(int finalChatRoomNo, int cMemberNo, Integer firstMemberNo, Integer secondMemberNo,
			Integer thirdMemberNo) {
		return chMapper.insertChatRoomMember3(finalChatRoomNo,cMemberNo,firstMemberNo,secondMemberNo,thirdMemberNo);
	}
	@Override
	public List<Integer> getMatMemberNos2(Integer firstPtNo, Integer secondPtNo) {
		return chMapper.getMatMemberNos2(firstPtNo,secondPtNo);
	}
	@Override
	public List<Integer> getMatMemberNos3(Integer firstPtNo, Integer secondPtNo, Integer thirdPtNo) {
		return chMapper.getMatMemberNos3(firstPtNo,secondPtNo,thirdPtNo);
	}
	@Override
	public int getChatCount(Integer finalChatRoomNo) {
		// TODO Auto-generated method stub
		return chMapper.getChatCount(finalChatRoomNo);
	}
	
	@Override
	public int getAlreadyChatRoomNo(Integer matNo, int memberNo, int relatedMemberNo) {
		return chMapper.getAlreadyChatRoomNo(matNo,memberNo,relatedMemberNo);
	}
    

}
