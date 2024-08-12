package com.kh.dndncare.chating.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.dndncare.chating.model.service.ChatingService;
import com.kh.dndncare.chating.model.vo.ChatingRoom;
import com.kh.dndncare.member.model.vo.Member;

import jakarta.servlet.http.HttpSession;

@Controller
public class ChatingController {
	
	@Autowired
	private ChatingService chService;
	
	@GetMapping("createAndGetChat.ch")
	public String createAndGetChat(@RequestParam("matNo") int matNo,HttpSession session) {
//		Member loginUser = (Member)session.getAttribute("loginUser");
//		int memberNo = loginUser.getMemberNo();
//		
//		ChatingRoom chatingRoom = chService.getChatRoom(memberNo,matNo);
//		if(chatingRoom == null) {
//			int chatRoomResult = chService.insertChatRoom(matNo);
//			int chatRoomMemberResult = chService.insertChatRoomMember(matNo);
//			int chatRoomMessageResult = chService.insertChatMessage();
//			
//		}
		return null;
	}
}
