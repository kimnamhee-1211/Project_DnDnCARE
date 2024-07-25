package com.kh.dndncare.board.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.dndncare.board.model.exception.BoardException;
import com.kh.dndncare.board.model.service.BoardService;
import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.common.Pagination;
import com.kh.dndncare.member.model.vo.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class BoardController {
	
	@Autowired
	private BoardService bService;
	
	@GetMapping("communityBoardList.bo")
	public String CommunityList(@RequestParam(value="page", defaultValue = "1") int currentPage, Model model, 
			HttpServletRequest request, HttpSession session) {
		int listCount = bService.getListCountAll();
		
		String status = ((Member)session.getAttribute("loginUser")).getMemberStatus();
		
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 5);
		
		ArrayList<Board> list = bService.selectBoardAllList(pi);
		
		if(list != null) {
			model.addAttribute("list", list);
			model.addAttribute("pi", pi);
			model.addAttribute("loc", request.getRequestURI());
			switch(status) {			
				// 	로그인 멤버가 간병인일때
				case "C":
					return "caregiverBoard";
				// 로그인 멤버가 환자일때
				case "P":
					return "patientBoard";
				// 그외
				default :
					return "adminBoard";
			}
		}else {
			throw new BoardException("게시글 조회를 실패하였습니다.");
		}
		
	}
	
	@GetMapping("writeBoard.bo")
	public String writeBoard() {
		return "writeBoard";
	}
	
	@GetMapping("selectBoard.bo")
	public String selectBoard() {
		return "boardDetail";
	}
}
