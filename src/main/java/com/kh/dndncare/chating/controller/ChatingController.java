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
    public String createAndGetChat(@RequestParam(value="matNo", required=false) Integer matNo,
                                   @RequestParam(value="matPtNo", required=false) Integer matPtNo,
                                   @RequestParam(value="chatRoomNo", required=false) Integer chatRoomNo,
                                   @RequestParam(value ="caregiverMemberNo", required=false) Integer caregiverMemberNo,
                                   HttpSession session,
                                   Model model) {
        Member loginUser = (Member) session.getAttribute("loginUser");
        int memberNo = loginUser.getMemberNo();
        String memberName = loginUser.getMemberName();

        Integer finalChatRoomNo = null;
        int relatedMemberNo = 0;
        int relatedMatPtNo = 0;
        int cMemberNo = 0;
        int firstMemberNo;
        int secondMemberNo;
        int thirdMemberNo;
        int firstPtNo;
        int secondPtNo;
        int thirdPtNo;
        
        
        if (chatRoomNo != null) {
            // 기존 채팅방으로 이동
            ChatingRoom existingChatingRoom = chService.getChatRoom(memberNo, chatRoomNo);
            if (existingChatingRoom != null) {
                finalChatRoomNo = existingChatingRoom.getChatRoomNo();
                chService.markAsRead(finalChatRoomNo, memberNo);
            } else {
                throw new MemberException("존재하지 않는 채팅방입니다.");
            }
        } else {
            // 새 채팅방 생성
            if (matNo == null) {
                throw new MemberException("채팅방 생성에 필요한 정보가 부족합니다.");
            }

            
            
            int ptCount = chService.getPtCount(matNo);
            
            // 개인간병일 때
            if(ptCount == 1) {
                //간병인이 공개구인 페이지에서 신청했을 때
            	ChatingRoom newChatingRoom = new ChatingRoom();
                newChatingRoom.setMatNo(matNo);
            	List<Integer> ptNos = chService.getMatPtNos(matNo);          
                if("C".equals(loginUser.getMemberCategory())) {
                	matPtNo = ptNos.get(0);
                    relatedMemberNo = chService.getMatMemberNo(matPtNo);
                } // 환자가 간병인 상세페이지에서 신청했을 때
                else if("P".equals(loginUser.getMemberCategory())) {
                    relatedMemberNo = caregiverMemberNo;
                }
                // 이미 같은 멤버 구성끼리 속한 채팅방번호가 있는지 있으면 거기로 바로 쏘기
                Integer alreadyChatRoomNo = chService.getAlreadyChatRoomNo(matNo,memberNo,relatedMemberNo);
                if(alreadyChatRoomNo != -1) {
                	finalChatRoomNo = alreadyChatRoomNo;
                } else {
                	int chatRoomResult = chService.insertChatRoom(newChatingRoom);
                
	                if (chatRoomResult <= 0) {
	                   throw new MemberException("채팅방 생성에 실패했습니다.");
	                }
	                relatedMatPtNo = matPtNo;
	                System.out.println(relatedMatPtNo);
	                finalChatRoomNo = chService.getChatRoomNo(relatedMatPtNo);
	                System.out.println("finalChatRoomNo" + finalChatRoomNo);
	                
	                if (finalChatRoomNo == null) {
	                    throw new MemberException("채팅방 번호를 가져오는데 실패했습니다.");
	                }
	                
	                int chatRoomMemberResult = chService.insertChatRoomMember(finalChatRoomNo, memberNo, relatedMemberNo);
	                 
	                if (chatRoomMemberResult <= 0) {
	                   throw new MemberException("채팅방 멤버 추가에 실패했습니다.");
	                }
                }
                	
                
                
                
            }
            //공동간병에서 참여환자수가 2일때 
            else if(ptCount == 2) {
            	ChatingRoom newChatingRoom = new ChatingRoom();
                newChatingRoom.setMatNo(matNo);
                List<Integer> ptNos = chService.getMatPtNos(matNo);
                System.out.println("ptNos 첫번째 :" + ptNos.get(0));
                System.out.println("ptNos 두번째 :" + ptNos.get(1));
                if("C".equals(loginUser.getMemberCategory())) {
                    cMemberNo = memberNo;           		
                } else if("P".equals(loginUser.getMemberCategory())) {
                    cMemberNo = caregiverMemberNo;
                }
                
                firstPtNo = ptNos.get(0);
                secondPtNo = ptNos.get(1);
                
                List<Integer> memberNos = chService.getMatMemberNos2(firstPtNo,secondPtNo);
                
                firstMemberNo = memberNos.get(0);
                secondMemberNo = memberNos.get(1);
                
                int chatRoomResult = chService.insertChatRoom(newChatingRoom);
                
                if (chatRoomResult <= 0) {
                   throw new MemberException("채팅방 생성에 실패했습니다.");
                }
                relatedMatPtNo = firstPtNo;
                finalChatRoomNo = chService.getChatRoomNo(relatedMatPtNo);
                System.out.println("finalChatRoomNo" + finalChatRoomNo);
                
                if (finalChatRoomNo == null) {
                    throw new MemberException("채팅방 번호를 가져오는데 실패했습니다.");
                }
                
                int chatRoomMemberResult2 = chService.insertChatRoomMember2(finalChatRoomNo, cMemberNo,firstMemberNo,secondMemberNo);
                
                if(chatRoomMemberResult2 <=0) {
                    throw new MemberException("채팅멤버 추가에 실패하였습니다");
                }
            }
            //공동간병에서 참여환자수가 3일때
            else if(ptCount == 3) {
            	ChatingRoom newChatingRoom = new ChatingRoom();
                newChatingRoom.setMatNo(matNo);
                List<Integer> ptNos = chService.getMatPtNos(matNo);
                if("C".equals(loginUser.getMemberCategory())) {
                    cMemberNo = memberNo;
                } else if("P".equals(loginUser.getMemberCategory())) {
                    cMemberNo = caregiverMemberNo;
                }
                
                firstPtNo = ptNos.get(0);
                secondPtNo = ptNos.get(1);
                thirdPtNo = ptNos.get(2);
                
                List<Integer> memberNos = chService.getMatMemberNos3(firstPtNo,secondPtNo,thirdPtNo);
                
                firstMemberNo = memberNos.get(0);
                secondMemberNo = memberNos.get(1);
                thirdMemberNo = memberNos.get(2);
                
                int chatRoomResult = chService.insertChatRoom(newChatingRoom);
                
                if (chatRoomResult <= 0) {
                    throw new MemberException("채팅방 생성에 실패했습니다.");
                }
                
                relatedMatPtNo = firstPtNo;
                finalChatRoomNo = chService.getChatRoomNo(relatedMatPtNo);
                System.out.println("finalChatRoomNo" + finalChatRoomNo);
                
                if (finalChatRoomNo == null) {
                    throw new MemberException("채팅방 번호를 가져오는데 실패했습니다.");
                }
                
                int chatRoomMemberResult3 = chService.insertChatRoomMember3(finalChatRoomNo, cMemberNo,firstMemberNo,secondMemberNo,thirdMemberNo);
                
                if(chatRoomMemberResult3 <=0) {
                    throw new MemberException("채팅멤버 추가에 실패하였습니다");
                }
            }
        }

        if (finalChatRoomNo == null) {
            throw new MemberException("채팅방 번호를 가져오는데 실패했습니다.");
        }
        int firstChat = chService.getChatCount(finalChatRoomNo);
        if(firstChat == 0) {
        	ChatingRoomMessage systemMessage = new ChatingRoomMessage();
	        systemMessage.setChatRoomNo(finalChatRoomNo);
	        systemMessage.setMemberNo(memberNo);  // 시스템 메시지를 나타내는 특별한 memberNo
	        systemMessage.setChatContent("안녕하세요.");
	        chService.saveMessage(systemMessage);
        }
        
        
        model.addAttribute("chatRoomId", finalChatRoomNo);
        model.addAttribute("userId", memberNo);
        model.addAttribute("memberName", memberName);
        
        return "chatRoom";
    }
	
	
    @MessageMapping("/chat/{chatRoomId}")
    @SendTo("/room/chat/{chatRoomId}")
    public ChatingRoomMessage sendMessage(@DestinationVariable("chatRoomId") int chatRoomId, ChatingRoomMessage chatMessage) {
        chatMessage.setChatRoomNo(chatRoomId);
        int participantCount = chService.getParticipantCount(chatRoomId);
        System.out.println("보낼때 참가인원 :" + participantCount);
        chatMessage.setReadCount(participantCount);  // 발신자를 제외한 참여자 수
        
        TimeZone koreaTimeZone = TimeZone.getTimeZone("Asia/Seoul");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        sdf.setTimeZone(koreaTimeZone);
        chatMessage.setWriteDate(new Date());
        System.out.println("보내기 직전 참가인원 :" + participantCount);
        return chService.sendMessage(chatMessage);  // 서비스 메서드 사용
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
        int chatRoomNoInt = Integer.parseInt(chatRoomId);
        int memberNo = readStatusMessage.getMemberNo();

        chService.markAsRead(chatRoomNoInt, memberNo);

        List<Map<String, Object>> messageReadCounts = chService.getMessageReadCounts(chatRoomNoInt);
        int participantCount = chService.getParticipantCount(chatRoomNoInt);
        System.out.println("챗룸넘버 = " + chatRoomId);
        System.out.println("참가인원: " + participantCount);
        System.out.println("메세지 리드카운트 : " + messageReadCounts);
        System.out.println("메세지 리드카운트2 : " + (messageReadCounts));

        Map<String, Object> response = new HashMap<>();
        response.put("memberNo", memberNo);
        response.put("messageReadCounts", messageReadCounts);
        response.put("participantCount", participantCount);

        messagingTemplate.convertAndSend("/room/chat/" + chatRoomNoInt + "/read", response);
    }
    
//    @GetMapping("/api/chat/unreadCount")
//    @ResponseBody
//    public Map<String, Object> getUnreadMessageCount(@RequestParam("chatRoomNo") int chatRoomNo, @RequestParam("memberNo") int memberNo) {
//        int unreadCount = chService.getUnreadMessageCount(chatRoomNo, memberNo);
//        Map<String, Object> response = new HashMap<>();
//        response.put("unreadCount", unreadCount);
//        return response;
//    }
    
    @GetMapping("/api/chat/messageReadCounts/{chatRoomId}")
    @ResponseBody
    public List<Map<String, Object>> getMessageReadCounts(@PathVariable("chatRoomId") int chatRoomId) {
        return chService.getMessageReadCounts(chatRoomId);
    }
    
//    @MessageMapping("/chat/checkRead/{chatRoomId}")
//    @SendTo("/room/chat/{chatRoomId}/read")
//    public Map<String, Object> checkReadStatus(@DestinationVariable String chatRoomId, @Payload ReadStatusMessage readStatusMessage) {
//        int chatRoomNoInt = Integer.parseInt(chatRoomId);
//        chService.markAsRead(chatRoomNoInt, readStatusMessage.getMemberNo());
//        
//        int unreadCount = chService.getUnreadMessageCount(chatRoomNoInt, readStatusMessage.getMemberNo());
//        
//        Map<String, Object> response = new HashMap<>();
//        response.put("memberNo", readStatusMessage.getMemberNo());
//        response.put("unreadCount", unreadCount);
//        
//        return response;
//    }
    
    
    
    
    
    
    
}