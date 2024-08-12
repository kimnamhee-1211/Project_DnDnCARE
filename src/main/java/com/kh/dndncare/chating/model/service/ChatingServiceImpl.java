package com.kh.dndncare.chating.model.service;

import java.util.List;

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
	public ChatingRoom getChatRoom(int memberNo, int matNo) {
		return chMapper.getChatRoom(memberNo,matNo);
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

}
