package com.kh.dndncare.chating.controller;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.dndncare.chating.model.service.ChatingService;
import com.kh.dndncare.chating.model.vo.ChatingRoom;
import com.kh.dndncare.chating.model.vo.ChatingRoomMessage;
import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.vo.Member;

import jakarta.servlet.http.HttpSession;

@Controller
public class ChatingController {
	
	@Autowired
	private ChatingService chService;
	
//	@GetMapping("getChatList.ch")
//	public String getChatList(HttpSession session) {
//		Member loginUser = (Member)session.getAttribute("loginUser");
//		int memberNo = loginUser.getMemberNo();
//		
//		ArrayList<ChatingRoom> chatRoomList = chService.getChatRoomList(memberNo);
//		System.out.println(chatRoomList);
//		//ArrayList<ChatingRoomMessage> lastChatList = chService.getLastChatList
//		
//		
//		return null;
//	}
	
    @GetMapping("getChatList.ch")
    public String getChatRooms(Model model, HttpSession session) {
    	Member loginUser = (Member)session.getAttribute("loginUser");
		int memberNo = loginUser.getMemberNo();
        List<ChatingRoomMessage> latestMessages = chService.getLatestMessages(memberNo);
        if(latestMessages !=null) {    	
        model.addAttribute("latestMessages", latestMessages);
        return "chatList";
    } else {
    	throw new MemberException("채팅리스트 넘어가기 실패");
    }
       
}
	
	
	

	
	@GetMapping("createAndGetChat.ch")
	public String createAndGetChat(@RequestParam(value="matNo" ,required=false ) Integer matNo,
	                               @RequestParam(value= "matPtNo",required=false) Integer matPtNo,
	                               @RequestParam(value= "chatRoomNo",required=false) Integer chatRoomNo,
	                               HttpSession session,
	                               Model model) {
	    Member loginUser = (Member) session.getAttribute("loginUser");
	    int memberNo = loginUser.getMemberNo();
	    String memberName = loginUser.getMemberName();

	    ChatingRoom existingChatingRoom = chService.getChatRoom(memberNo, chatRoomNo);
	    if (existingChatingRoom != null) {
	        // Chat room already exists, redirect to it
	        model.addAttribute("chatRoomId", existingChatingRoom.getChatRoomNo());
	        model.addAttribute("userId", memberNo);
	        model.addAttribute("memberName", memberName);
	        return "chatRoom";
	    }

	    // Create new chat room
	    ChatingRoom chatingRoom = new ChatingRoom();
	    chatingRoom.setMatNo(matNo);

	    int matMemberNo = chService.getMatMemberNo(matPtNo);
	    int chatRoomResult = chService.insertChatRoom(chatingRoom);
	    
	    if (chatRoomResult <= 0) {
	        throw new MemberException("채팅방 생성에 실패했습니다.");
	    }

	    int newChatRoomNo = chService.getChatRoomNo(matPtNo);
	    int chatRoomMemberResult = chService.insertChatRoomMember(newChatRoomNo, memberNo, matMemberNo);

	    if (chatRoomMemberResult <= 0) {
	        throw new MemberException("채팅방 멤버 추가에 실패했습니다.");
	    }

	    model.addAttribute("chatRoomId", newChatRoomNo);
	    model.addAttribute("userId", memberNo);
	    return "chatRoom";
	}
	
	@MessageMapping("/chat/{chatRoomId}")
    @SendTo("/room/chat/{chatRoomId}")
    public ChatingRoomMessage sendMessage(@DestinationVariable("chatRoomId") int chatRoomId, ChatingRoomMessage chatMessage) {
        chatMessage.setChatRoomNo(chatRoomId);
        chatMessage.setWriteDate(new Date(System.currentTimeMillis()));
        chatMessage.setReadCount(0);
        chService.saveMessage(chatMessage);
        return chatMessage;
    }

    @GetMapping("/api/chat/messages/{chatRoomId}")
    @ResponseBody
    public List<ChatingRoomMessage> getChatHistory(@PathVariable("chatRoomId") int chatRoomId) {
        return chService.getMessagesByChatRoomNo(chatRoomId);
    }
}
