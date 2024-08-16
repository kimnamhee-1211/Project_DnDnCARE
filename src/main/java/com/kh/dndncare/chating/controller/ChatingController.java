package com.kh.dndncare.chating.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.dndncare.chating.model.service.ChatingService;
import com.kh.dndncare.chating.model.vo.ChatingRoom;
import com.kh.dndncare.chating.model.vo.ChatingRoomMessage;
import com.kh.dndncare.chating.model.vo.ReadStatusMessage;
import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.vo.Member;

import jakarta.servlet.http.HttpSession;

@Controller
public class ChatingController {
	
	@Autowired
	private ChatingService chService;
	
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
	
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
	        
	        chService.markAsRead(existingChatingRoom.getChatRoomNo(), memberNo);
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
	    model.addAttribute("memberName",memberName);
	    return "chatRoom";
	}
	
	@MessageMapping("/chat/{chatRoomId}")
    @SendTo("/room/chat/{chatRoomId}")
    public ChatingRoomMessage sendMessage(@DestinationVariable("chatRoomId") int chatRoomId, ChatingRoomMessage chatMessage) {
        chatMessage.setChatRoomNo(chatRoomId);
        // 채팅방 참여자 수를 조회하여 readCount 설정
        int participantCount = chService.getParticipantCount(chatRoomId);
        chatMessage.setReadCount(participantCount - 1);  // 발신자를 제외한 참여자 수
        
        TimeZone koreaTimeZone = TimeZone.getTimeZone("Asia/Seoul");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(koreaTimeZone);
        chatMessage.setWriteDate(new Date()); // 현재 시간으로 설정
        
        chService.saveMessage(chatMessage);
        return chatMessage;
    }
	
    @GetMapping("/api/chat/messages/{chatRoomId}")
    @ResponseBody
    public List<Map<String, Object>> getChatHistory(@PathVariable("chatRoomId") int chatRoomId) {
        List<ChatingRoomMessage> messages = chService.getMessagesByChatRoomNo(chatRoomId);
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul")); // 한국 시간대로 설정
        
        return messages.stream().map(message -> {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("chatMessageNo", message.getChatMassageNo());
            messageMap.put("chatRoomNo", message.getChatRoomNo());
            messageMap.put("memberNo", message.getMemberNo());
            messageMap.put("chatContent", message.getChatContent());
            messageMap.put("readCount", message.getReadCount());
            messageMap.put("writeDate", sdf.format(message.getWriteDate()));
            messageMap.put("memberName", message.getMemberName());
            return messageMap;
        }).collect(Collectors.toList());
    }

//    @GetMapping("/api/chat/messages/{chatRoomId}")
//    @ResponseBody
//    public List<ChatingRoomMessage> getChatHistory(@PathVariable("chatRoomId") int chatRoomId) {
//        return chService.getMessagesByChatRoomNo(chatRoomId);
//    }
    
    //읽음 표시 관련 메소드 
    @PostMapping("/api/chat/markAsRead")
    @ResponseBody
    public Map<String, Object> markMessagesAsRead(@RequestParam("chatRoomNo") int chatRoomNo, @RequestParam("memberNo") int memberNo) {
        chService.markAsRead(chatRoomNo, memberNo);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        return response;
    }
    
    @MessageMapping("/chat/read/{chatRoomId}")
    public void markAsRead(@DestinationVariable("chatRoomId") String chatRoomId, 
                           @Payload ReadStatusMessage readStatusMessage) {
        chService.markAsRead(Integer.parseInt(chatRoomId), readStatusMessage.getMemberNo());

        // 읽음 상태 업데이트를 모든 참여자에게 브로드캐스트
        messagingTemplate.convertAndSend("/room/chat/" + chatRoomId + "/read", readStatusMessage);
    }

    @GetMapping("/api/chat/unreadCount")
    @ResponseBody
    public Map<String, Object> getUnreadMessageCount(@RequestParam("chatRoomNo") int chatRoomNo, @RequestParam("memberNo") int memberNo) {
        int unreadCount = chService.getUnreadMessageCount(chatRoomNo, memberNo);
        Map<String, Object> response = new HashMap<>();
        response.put("unreadCount", unreadCount);
        return response;
    }
    
    
    
    
    
    
    
}
