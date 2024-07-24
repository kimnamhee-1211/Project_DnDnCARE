package com.kh.dndncare.board.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BoardController {
	
	@GetMapping("communityBoardList.bo")
	public String CommunityList() {
		return "caregiverBoard";
	}
	
	@GetMapping("writeBoard.bo")
	public String writeBoard() {
		return "writeBoard";
	}
}
