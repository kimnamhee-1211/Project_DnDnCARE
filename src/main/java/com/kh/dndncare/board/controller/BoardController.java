package com.kh.dndncare.board.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.dndncare.board.model.exception.BoardException;
import com.kh.dndncare.board.model.service.BoardService;
import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.board.model.vo.Reply;
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
		
		String category = ((Member)session.getAttribute("loginUser")).getMemberCategory();
		PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 20);
		
		ArrayList<Board> list = bService.selectBoardAllList(pi, category);
		System.err.println(list);
		if(list != null) {
			model.addAttribute("list", list);
			model.addAttribute("pi", pi);
			model.addAttribute("loc", request.getRequestURI());
			if(category.equals("P")) {
				return "patientBoard";
			}else {
				return "caregiverBoard";
			}
		}else {
			throw new BoardException("게시글 조회를 실패하였습니다.");
		}
	}
	
	@GetMapping("writeBoard.bo")
	public String writeBoard() {
		return "writeBoard";
	}
	
	@PostMapping("insertBoard.bo")
	public String inserBoard(@ModelAttribute Board b, HttpSession session) {
		int memberNo = ((Member)session.getAttribute("loginUser")).getMemberNo();
		
		b.setMemberNo(memberNo);
		b.setAreaNo(b.getAreaNo());
		b.setCategoryNo(b.getCategoryNo());
		int result = bService.insertBoard(b);
		if(result > 0) {
			return "redirect:communityBoardList.bo";			
		}else {
			throw new BoardException("게시글 작성을 실패하였습니다.");
		}
	}
	@GetMapping("selectBoard.bo")
	public String selectBoard(@RequestParam("bId") int bId, @RequestParam("page") int page, HttpSession session, Model model) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		int memberNo = loginUser.getMemberNo();
		
		Board board = bService.selectBoard(bId, memberNo);
		
		ArrayList<Reply> reply = bService.selectReply(bId);
		if(board != null) {
			model.addAttribute("b", board); 
			model.addAttribute("page", page); 
			return "boardDetail";
		}else {
			throw new BoardException("게시글 상세보기를 실패했습니다");
		}
	}
}
