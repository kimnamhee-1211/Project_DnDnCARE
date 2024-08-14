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
import jakarta.servlet.http.HttpSession;

@Controller
public class BoardController {
	
	@Autowired
	private BoardService bService;
	
	// 커뮤니티클릭하면 이동
	@GetMapping("communityBoardList.bo") 
	public String CommunityList(@RequestParam(value="page", defaultValue = "1") int currentPage, Model model,
	                            @RequestParam(value="categoryNo", defaultValue="-1") int categoryNo,
	                            @RequestParam(value="area", required = false) List<Integer> areas, 
	                            HttpServletRequest request, HttpSession session) {
	    String category = ((Member)session.getAttribute("loginUser")).getMemberCategory();
	    // 회원이 간병인인지 피간병인인지구분

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
		    // 댓글수 가져와서 replyCounts에 담기
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

	
	// 글작성 클릭
	@GetMapping("writeBoard.bo")
	public String writeBoard() {
		return "writeBoard";
	}
	
	// writeBoard에서 작성하기 클릭
	@PostMapping("insertBoard.bo")
	public String inserBoard(@ModelAttribute Board b, HttpSession session) {
		int memberNo = ((Member)session.getAttribute("loginUser")).getMemberNo();
		
		b.setMemberNo(memberNo);
		b.setAreaNo(b.getAreaNo());
		b.setCategoryNo(b.getCategoryNo());
		// 작성시 필요한 회원번호, 지역번호, 카테고리번호 b에 담기
		int result = bService.insertBoard(b);
		if(result > 0) {
			return "redirect:communityBoardList.bo";			
		}else {
			throw new BoardException("게시글 작성을 실패하였습니다.");
		}
	}
	
	// 게시글 클릭하기
	@GetMapping("selectBoard.bo")
	public String selectBoard(@RequestParam("bId") int bId, @RequestParam("page") int page, HttpSession session, Model model) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		int memberNo = loginUser.getMemberNo();
		Board board = bService.selectBoard(bId, memberNo);
		// 게시글
		
		ArrayList<Reply> reply = bService.selectReply(bId);
		// 댓글
		
		int boardLikeCount = bService.boardLikeCount(bId);
		// 좋아요
		
		HashMap<Integer, Integer> replyLikeCounts = new HashMap<>();
		 for (Reply replys : reply) {
	            int rId = replys.getReplyNo();
	            int replyLikeCount = bService.replyLikeCount(rId);
	            replyLikeCounts.put(rId, replyLikeCount);
	        }
		 // 댓글 좋아요
		 
		
		if(board != null) {
			model.addAttribute("b", board); 
			model.addAttribute("page", page); 
			model.addAttribute("reply", reply);
			model.addAttribute("boardLikeCount", boardLikeCount);
			model.addAttribute("replyLikeCounts", replyLikeCounts);
			return "boardDetail";
		}else {
			throw new BoardException("게시글 상세보기를 실패했습니다");
		}
	}
	
	// boardDetail페이지에서 댓글작성 클릭시
	@GetMapping(value="insertReply.bo", produces="application/json; charset-UTF-8")
	@ResponseBody
	public String insertReply(@ModelAttribute Reply r) {
		int result = bService.insertReply(r);
		// Reply에 넘어온 정보를 통해 insert
		
		ArrayList<Reply> replyList = bService.selectReply(r.getRefBoardNo());
		// 댓글리스트
		JSONArray array = new  JSONArray();
		HashMap<Integer, Integer> replyLikeCounts = new HashMap<>();
		ArrayList<Reply> sReply = bService.selectReply(r.getRefBoardNo());
		 for (Reply replys : sReply) {
	            int rId = replys.getReplyNo();
	            int replyLikeCount = bService.replyLikeCount(rId);
	            replyLikeCounts.put(rId, replyLikeCount);
	        }
		for(Reply reply : replyList) {
			JSONObject json = new JSONObject();
			json.put("replyNo", reply.getReplyNo());
			json.put("replyContent", reply.getReplyContent());
			json.put("memberNo", reply.getMemberNo());
			json.put("memberNickName", reply.getMemberNickName());
			json.put("replyCreateDate", reply.getReplyCreateDate());
			json.put("replyUpdateDate", reply.getReplyUpdateDate());
			json.put("refBoardNo", reply.getRefBoardNo());
			json.put("replyLikeCounts", replyLikeCounts);
			
			array.put(json);
		}
		return array.toString();
	}
	
	// 게시글 수정하기 클릭
	@PostMapping("editBoard.bo")
	public String editBoard(@RequestParam("boardId") int bId, @RequestParam("page") int page, Model model, HttpSession session) {
		Member loginUser = (Member)session.getAttribute("loginUser");
		
		int memberNo = loginUser.getMemberNo();
		Board b = bService.selectBoard(bId, memberNo);
		// 선택한 게시글 정보를 가지고 수정페이지로 이동
		model.addAttribute("b", b);
		model.addAttribute("page", page);
		return "editBoard";

	}
	
	// editBoard에서 수정하기 클릭
	@PostMapping("updateBoard.bo")
	public String updateBoard(@ModelAttribute Board b, @RequestParam("page") int page, RedirectAttributes ra) {
		int result = bService.updateBoard(b);
		// 가져온 Board로 update
		
		if(result>0) {
			ra.addAttribute("bId", b.getBoardNo());
			ra.addAttribute("page", page);
			return "redirect:selectBoard.bo";
		}else {
			throw new BoardException("게시판 수정에 실패했습니다.");
		}
	}
	
	// boardDetail에서 삭제하기 클릭
	@PostMapping("deleteBoard.bo")
	public String deleteBoard(@RequestParam("boardId") int bId) {
		int result = bService.deleteBoard(bId);
		if(result>0) {
			return "redirect:list.bo";			
		}else {
			throw new BoardException("게시글 삭제에 실패했습니다.");
		}
	}
	
	// 검색 클릭
	@GetMapping("searchBoard.bo")
	public String searchBoard(@RequestParam("searchType") String searchType, @RequestParam("searchText") String searchText,
							@RequestParam(value="page", defaultValue = "1") int currentPage, Model model,
				            @RequestParam(value="categoryNo", defaultValue="-1") int categoryNo,
				            @RequestParam(value="area", required = false) List<Integer> areas, 
				            HttpServletRequest request, HttpSession session) {
		String category = ((Member)session.getAttribute("loginUser")).getMemberCategory();
		//회원의 타입
		
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("searchType", searchType);
		// 검색할 카테고리
		map.put("searchText", searchText);
		// 검색할 내용
		map.put("category", category);
	    map.put("categoryNo", categoryNo);
	    // 게시글의 카테고리 번호
	    map.put("areas", areas);
	    // 지역
	    
	    int listCount = bService.getListCountAll(map);
	    PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 20);
	    // 페이지네이션
	    
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
	
	// 댓글 수정
	@GetMapping("updateReply.bo")
	@ResponseBody
	public String updateReply(@ModelAttribute Reply r) {
		int result = bService.updateReply(r);
		return result == 1 ? "success" : "fail";
	}
	
	// 댓글 삭제
	@GetMapping("deleteReply.bo")
	@ResponseBody
	public String deleteReply(@RequestParam("rId") int rId) {
		int result = bService.deleteReply(rId);
		return result == 1 ? "success" : "fail";
	}
	
	// 게시글 좋아요
	@PostMapping("boardLike.bo")
	@ResponseBody
	public String boardLike(@RequestParam("boardNo") int boardNo, @RequestParam("memberNo") int memberNo) {
	    HashMap<String, Integer> map = new HashMap<>();
	    map.put("boardNo", boardNo);
	    map.put("memberNo", memberNo);
	    // 게시글번호와 접속한회원정보
	    JSONObject json = new JSONObject();
	    try {
	        int result = bService.insertBoardLike(map);
	        String resultString = result == 1 ? "success" : "fail";
	        int boardUpdateLikeCount = bService.boardLikeCount(boardNo);
	        
	        json.put("boardUpdateLikeCount", boardUpdateLikeCount);
	        json.put("resultString", resultString);
	    } catch (Exception e) {
	        json.put("resultString", "error");
	    }
	    return json.toString();
	}
	
	// 댓글 좋아요
	@PostMapping("replyLike.bo")
	@ResponseBody
	public String reply(@RequestParam("rId") int rId, @RequestParam("memberNo") int memberNo) {
		HashMap<String, Integer> map = new HashMap<>();
		map.put("rId", rId);
		map.put("memberNo", memberNo);
		// 댓글번호와 접속한회원정보
		
		JSONObject json = new JSONObject();
		try {
			int result = bService.insertReplyLike(map);
			String resultString = result == 1 ? "success" : "fail";
			int replyLikeCount = bService.replyLikeCount(rId);
			json.put("resultString", resultString);
			json.put("replyLikeCount", replyLikeCount);
		} catch(Exception e) {
			json.put("resultString", "error");
		}
		return json.toString();
	}
	

	
	
}
