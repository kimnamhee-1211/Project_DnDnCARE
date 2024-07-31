package com.kh.dndncare.board.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.dndncare.board.model.exception.BoardException;
import com.kh.dndncare.board.model.service.BoardService;
import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.board.model.vo.Reply;
import com.kh.dndncare.common.Pagination;
import com.kh.dndncare.member.model.vo.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class BoardController {
	
	@Autowired
	private BoardService bService;
	
	@GetMapping("communityBoardList.bo")
	public String CommunityList(@RequestParam(value="page", defaultValue = "1") int currentPage, Model model,
	                            @RequestParam(value="categoryNo", defaultValue="-1") int categoryNo,
	                            @RequestParam(value="area", required = false) List<Integer> areas, 
	                            HttpServletRequest request, HttpSession session) {

	    String category = ((Member)session.getAttribute("loginUser")).getMemberCategory();

	   
	    HashMap<String, Object> map = new HashMap<>();
	    map.put("category", category);
	    map.put("categoryNo", categoryNo);
	    map.put("areas", areas);
	    
	    int listCount = bService.getListCountAll(map);
	    PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 20);
	    
	    if (areas == null || areas.isEmpty()) {
	        areas = new ArrayList<>(); 
	    }

	    ArrayList<Board> list = bService.selectBoardAllList(pi, map);
	    if(list != null) {
	    	HashMap<Integer, Integer> replyCounts = new HashMap<>();
		    for (Board board : list) {
		    	int replyCount = bService.getReplyCount(board.getBoardNo());
		    	replyCounts.put(board.getBoardNo(), replyCount);
		    	
		    }
	        model.addAttribute("list", list);
	        model.addAttribute("pi", pi);
	        model.addAttribute("categoryNo", categoryNo);
	        model.addAttribute("areas", areas);
	        model.addAttribute("loc", request.getRequestURI());
	        model.addAttribute("replyCounts", replyCounts);
	        if(category.equals("P")) {
	            return "patientBoard";
	        } else {
	            return "caregiverBoard";
	        }
	    } else {
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
		System.out.println(reply);
		int boardLikeCount = bService.boardLikeCount(bId);
		if(board != null) {
			model.addAttribute("b", board); 
			model.addAttribute("page", page); 
			model.addAttribute("reply", reply);
			model.addAttribute("boardLikeCount", boardLikeCount);
			return "boardDetail";
		}else {
			throw new BoardException("게시글 상세보기를 실패했습니다");
		}
	}
	
	@GetMapping(value="insertReply.bo", produces="application/json; charset-UTF-8")
	@ResponseBody
	public String insertReply(@ModelAttribute Reply r) {
		int result = bService.insertReply(r);
		ArrayList<Reply> replyList = bService.selectReply(r.getRefBoardNo());
		JSONArray array = new  JSONArray();
		for(Reply reply : replyList) {
			JSONObject json = new JSONObject();
			json.put("replyNo", reply.getReplyNo());
			json.put("replyContent", reply.getReplyContent());
			json.put("memberNo", reply.getMemberNo());
			json.put("memberNickName", reply.getMemberNickName());
			json.put("replyCreateDate", reply.getReplyCreateDate());
			json.put("replyUpdateDate", reply.getReplyUpdateDate());
			json.put("refBoardNo", reply.getRefBoardNo());
			
			array.put(json);
		}
		return array.toString();
	}
	
	@PostMapping("editBoard.bo")
	public String editBoard(@RequestParam("boardId") int bId, @RequestParam("page") int page, Model model, HttpSession session) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		int memberNo = loginUser.getMemberNo();
		Board b = bService.selectBoard(bId, memberNo);
		model.addAttribute("b", b);
		model.addAttribute("page", page);
		return "editBoard";

	}
	
	@PostMapping("updateBoard.bo")
	public String updateBoard(@ModelAttribute Board b, @RequestParam("page") int page, RedirectAttributes ra) {
		int result = bService.updateBoard(b);
		if(result>0) {
			
			ra.addAttribute("bId", b.getBoardNo());
			ra.addAttribute("page", page);
			return "redirect:selectBoard.bo";
		}else {
			throw new BoardException("게시판 수정에 실패했습니다.");
		}
	}
	
	@PostMapping("deleteBoard.bo")
	public String deleteBoard(@RequestParam("boardId") int bId) {
		int result = bService.deleteBoard(bId);
		if(result>0) {
			return "redirect:list.bo";			
		}else {
			throw new BoardException("게시글 삭제에 실패했습니다.");
		}
	}
	
	@GetMapping("searchBoard.bo")
	public String searchBoard(@RequestParam("searchType") String searchType, @RequestParam("searchText") String searchText,
							@RequestParam(value="page", defaultValue = "1") int currentPage, Model model,
				            @RequestParam(value="categoryNo", defaultValue="-1") int categoryNo,
				            @RequestParam(value="area", required = false) List<Integer> areas, 
				            HttpServletRequest request, HttpSession session) {
		String category = ((Member)session.getAttribute("loginUser")).getMemberCategory();
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("searchType", searchType);
		map.put("searchText", searchText);
		map.put("category", category);
	    map.put("categoryNo", categoryNo);
	    map.put("areas", areas);
	    
	    int listCount = bService.getListCountAll(map);
	    PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 20);
	    
	    if (areas == null || areas.isEmpty()) {
	        areas = new ArrayList<>(); 
	    }

	    ArrayList<Board> list = bService.searchBoard(pi, map);
	    if(list != null) {
	    	HashMap<Integer, Integer> replyCounts = new HashMap<>();
		    for (Board board : list) {
		    	int replyCount = bService.getReplyCount(board.getBoardNo());
		    	replyCounts.put(board.getBoardNo(), replyCount);
		    	
		    }
	        model.addAttribute("list", list);
	        model.addAttribute("pi", pi);
	        model.addAttribute("categoryNo", categoryNo);
	        model.addAttribute("areas", areas);
	        model.addAttribute("loc", request.getRequestURI());
	        model.addAttribute("replyCounts", replyCounts);
	        if(category.equals("P")) {
	            return "patientBoard";
	        } else {
	            return "caregiverBoard";
	        }
	    } else {
	        throw new BoardException("게시글 조회를 실패하였습니다.");
	    }
	}
	
	@GetMapping("updateReply.bo")
	@ResponseBody
	public String updateReply(@ModelAttribute Reply r) {
		int result = bService.updateReply(r);
		return result == 1 ? "success" : "fail";
	}
	
	@GetMapping("deleteReply.bo")
	@ResponseBody
	public String deleteReply(@RequestParam("rId") int rId) {
		int result = bService.deleteReply(rId);
		return result == 1 ? "success" : "fail";
	}
	
	@GetMapping("boardLike.bo")
	@ResponseBody
	public String boardLike(@RequestParam("boardNo") int boardNo, @RequestParam("memberNo") int memberNo) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put("boardNo", boardNo);
		map.put("memberNo", memberNo);
		int result = bService.insertBoardLike(map);
		int boardLikeCount = bService.boardLikeCount(boardNo);
		System.out.println(map);
		JSONObject json = new JSONObject();
		json.put("boardLikeCount", boardLikeCount);
		
		return result == 1 ? "success" : "fail";
	}


	
	
}
