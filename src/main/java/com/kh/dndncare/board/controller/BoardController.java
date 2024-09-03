package com.kh.dndncare.board.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.kh.dndncare.admin.model.vo.Attachment;
import com.kh.dndncare.board.model.exception.BoardException;
import com.kh.dndncare.board.model.service.BoardService;
import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.board.model.vo.Reply;
import com.kh.dndncare.common.Pagination;
import com.kh.dndncare.common.Pagination2;
import com.kh.dndncare.member.controller.CustomBotController;
import com.kh.dndncare.member.model.vo.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class BoardController {
	
	@Autowired
	private BoardService bService;
	
	@Autowired
	private CustomBotController bot;
	
	// 커뮤니티클릭하면 이동
	@GetMapping("communityBoardList.bo")
	public String CommunityList(
						        @RequestParam(value = "searchType", required = false) String searchType,
						        @RequestParam(value = "searchText", required = false) String searchText,
						        @RequestParam(value = "page", defaultValue = "1") int currentPage,
						        Model model,
						        @RequestParam(value = "categoryNo", defaultValue = "-1") int categoryNo,
						        @RequestParam(value = "area", required = false) List<Integer> areas,
						        HttpServletRequest request,
						        HttpSession session) {

	    String category = ((Member) session.getAttribute("loginUser")).getMemberCategory();
	    // 회원의 타입 확인

	    HashMap<String, Object> map = new HashMap<>();
	    map.put("category", category);
	    map.put("categoryNo", categoryNo);
	    map.put("areas", areas);

	    // 검색 조건이 있을 경우 추가
	    if (searchType != null && !searchType.isEmpty()) {
	        map.put("searchType", searchType);
	    }
	    if (searchText != null && !searchText.isEmpty()) {
	        map.put("searchText", searchText);
	    }

	    int listCount = bService.getListCountAll(map);
	    PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 10);

	    if (areas == null || areas.isEmpty()) {
	        areas = new ArrayList<>(); 
	    }

	    ArrayList<Board> list;

	    // 검색 조건이 있는 경우와 없는 경우에 따라 다른 서비스 메서드 호출
	    if (searchType != null && searchText != null && !searchText.isEmpty()) {
	        list = bService.searchBoard(pi, map);  // 검색 조건에 따른 게시글 목록 조회
	    } else {
	        list = bService.selectBoardAllList(pi, map);  // 일반 게시글 목록 조회
	    }

	    if (list != null) {
	        HashMap<Integer, Integer> replyCounts = new HashMap<>();
	        for (Board board : list) {
	            int replyCount = bService.getReplyCount(board.getBoardNo());
	            replyCounts.put(board.getBoardNo(), replyCount);
	        }
	        model.addAttribute("list", list);
	        model.addAttribute("pi", pi);
	        model.addAttribute("categoryNo", categoryNo);
	        model.addAttribute("area", areas);
	        model.addAttribute("loc", request.getRequestURI());
	        model.addAttribute("replyCounts", replyCounts);
	        model.addAttribute("searchType", searchType);
	        model.addAttribute("searchText", searchText);

	        if (category.equals("P")) {
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
		
		System.out.println("####################");
		System.out.println(b);
		
		b.setMemberNo(memberNo);
		b.setAreaNo(b.getAreaNo());
		b.setCategoryNo(b.getCategoryNo());
		// 작성시 필요한 회원번호, 지역번호, 카테고리번호 b에 담기
		int result = bService.insertBoard(b);
		if(result > 0) {
			if(b.getCategoryNo()!=99) {
				return "redirect:communityBoardList.bo";			
			}else {
				return "redirect:qnaBoardList.bo";
			}
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
	public String editBoard(@RequestParam("boardNo") int bId, @RequestParam("page") int page, Model model, HttpSession session) {
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
		System.out.println(b);
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
	public String deleteBoard(@RequestParam("boardNo") int bId, @RequestParam("categoryNo") int categoryNo) {
		int result = bService.deleteBoard(bId);
		System.out.println(categoryNo);
		if(result>0) {
			if(categoryNo != 99) {
				return "redirect:communityBoardList.bo";			
			}else {
				return "redirect:qnaBoardList.bo";
			}
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
	    PageInfo spi = Pagination.getPageInfo(currentPage, listCount, 10);
	    // 페이지네이션
	    
	    if (areas == null || areas.isEmpty()) {
	        areas = new ArrayList<>(); 
	    }

	    ArrayList<Board> list = bService.searchBoard(spi, map);
	    if(list != null) {
	    	HashMap<Integer, Integer> replyCounts = new HashMap<>();
		    for (Board board : list) {
		    	int replyCount = bService.getReplyCount(board.getBoardNo());
		    	replyCounts.put(board.getBoardNo(), replyCount);
		    	
		    }
	        model.addAttribute("list", list);
	        model.addAttribute("spi", spi);
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
	
	
	// 간병백과 페이지로의 이동요청을 처리
	@GetMapping("careInformation.bo")
	public String careInformation(HttpSession session, Model model) {
		// ai 검색 횟수를 파악해서 넘어가야 한다.
		Member loginUser = (Member)session.getAttribute("loginUser");
		if(loginUser != null) {
			String id = loginUser.getMemberId();
			// 오늘 작성된 로그파일들에게만 접근한다.
			File file = new File("C:/logs/dndnCare/careInformationAi/careInformationAi.log"); // 로그 파일이 저장된 폴더 '내'까지 접근
			int aiCount = 0;
			
			try(BufferedReader br = new BufferedReader(new FileReader(file));) {
				String data;
				
				while((data = br.readLine()) != null) {
					// 24-08-17 20:24:44 [INFO] c.k.d.c.i.CheckCareInfomationAiSearch.preHandle - test-m-p20
					String[] arr = data.split(" ");
					if(arr[arr.length-1].equals(id)) aiCount++; // 검색기록에 사용자 아이디가 있는 경우 검색횟수를 1증가 시킨다.
				}
				
				model.addAttribute("aiCount", aiCount);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		
		return "board/careInformation";
		
	}
	
	// 간병백과 컨텐츠의 목록을 조회 : 검색조건X
	@PostMapping("selectCareInformationList.bo")
	@ResponseBody
	public void selectCareInformationList(@RequestParam(value="page", defaultValue="1") int currentPage, 
												HttpServletResponse response) {
		// 페이징 처리된 컨텐츠 목록 조회
		int listCount = bService.getCareInfomationListCount(null);
		PageInfo pi = Pagination2.getPageInfo(currentPage, listCount, 10, 5);
		
		response.setContentType("application/json; charset=UTF-8"); // MIME타입, 인코딩타입 지정
		GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd");
		Gson gson = gb.create(); 
		
		try {
			if(currentPage > pi.getMaxPage()) {
				gson.toJson(null, response.getWriter());
			} else {
				HashMap<String, ArrayList<?>> result = new HashMap<String, ArrayList<?>>();
				ArrayList<Board> bList = bService.selectCareInformation(null, pi);
				ArrayList<Attachment> aList = bService.selectAttachment(bList);
				ArrayList<Integer> pList = new ArrayList<Integer>();
				pList.add(pi.getMaxPage());
				
				result.put("bList", bList);
				result.put("aList", aList);
				result.put("pList", pList);
				gson.toJson(result, response.getWriter());
			}
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	// 간병백과 글 상세조회 시 조회수 증가 요청
	@GetMapping("updateCareInformationCount.bo")
	@ResponseBody
	public String updateCareInformationCount(@RequestParam("boardNo") int boardNo) {
		int result = bService.updateCareInformationCount(boardNo);
		return result == 1 ? "success" : "fail";
	}
	
	// 간병백과 글 검색 요청
	@GetMapping("searchCareInformation.bo")
	@ResponseBody
	public void searchCareInformation(@RequestParam(value="page", defaultValue="1") int currentPage,
										@RequestParam("searchOption") String searchOption,
										@RequestParam("searchContent") String searchContent,
										HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd");
		Gson gson = gb.create();
		
		try {
			if(searchOption.equals("none")) { // 검색조건이 올바르지 않을 때 : 백에서도 안전하게 fail로 반환
				gson.toJson(null, response.getWriter());
			} else { // 올바른 검색조건을 선택했을 때
				HashMap<String, String> map = new HashMap<String, String>();
				if(searchOption.equals("title")) {
					map.put("column", "BOARD_TITLE");
				} else if(searchOption.equals("content")) {
					map.put("column", "BOARD_CONTENT");
				}
				map.put("searchContent", searchContent);
				
				int listCount = bService.getCareInfomationListCount(map);
				if(listCount == 0) {
					log.info("{} : {}", searchOption, searchContent); // 검색결과가 없을 때 로그를 발생시킨다.
					gson.toJson("empty", response.getWriter());
				} else {
					PageInfo pi = Pagination2.getPageInfo(currentPage, listCount, 10, 5);
					
					if(currentPage > pi.getMaxPage()) {
						gson.toJson(null, response.getWriter());
					} else {
						HashMap<String, ArrayList<?>> result = new HashMap<String, ArrayList<?>>();
						ArrayList<Board> bList = bService.searchCareInformation(map, pi);
						
						ArrayList<Attachment> aList = bService.selectAttachment(bList);
						ArrayList<Integer> pList = new ArrayList<Integer>();
						pList.add(pi.getMaxPage());
						
						result.put("bList", bList);
						result.put("aList", aList);
						result.put("pList", pList);
						
						gson.toJson(result, response.getWriter());
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// 간병백과 ai 검색 요청
	@GetMapping("searchOpenAi.bo")
	@ResponseBody
	public String searchOpenAi(@RequestParam("condition") String condition, HttpSession session) {
		return bot.chat(condition + "에 대한 간병정보를 300자로 요약해줘.");
	}
	
	// 삭제하지 말아주세요 ㅠ_ㅠ
	// 간병백과 앨범형 페이지로 이동 요청 (**임시 징검다리**)
	@GetMapping("albumCareInformation.bo")
	public String albumCareInformation(@RequestParam(value="page", defaultValue="1") int currentPage) {
		return "albumCareInformation";
	}// 삭제하지 말아주세요 ㅠ_ㅠ
	
	
	// 간병백과 앨범형 컨텐츠 목록을 조회
	@PostMapping("selectCareInformationAlbumList.bo")
	@ResponseBody
	public void selectCareInformationAlbumList(@RequestParam(value="page", defaultValue="1") int currentPage,
												HttpServletResponse response) {
		// 페이징 처리된 컨텐츠 목록 조회
		int listCount = bService.getCareInfomationListCount(null);
		PageInfo pi = Pagination2.getPageInfo(currentPage, listCount, 9, 5);
		
		response.setContentType("application/json; charset=UTF-8"); // MIME타입, 인코딩타입 지정
		GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd");
		Gson gson = gb.create(); 
		
		try {
			if(currentPage > pi.getMaxPage()) {
				gson.toJson(null, response.getWriter());
			} else {
				HashMap<String, ArrayList<?>> result = new HashMap<String, ArrayList<?>>();
				ArrayList<Board> bList = bService.selectCareInformation(null, pi);
				ArrayList<Attachment> aList = bService.selectAttachment(bList);
				
				ArrayList<Integer> pList = new ArrayList<Integer>();
				pList.add(pi.getMaxPage());
				
				result.put("bList", bList);
				result.put("aList", aList);
				result.put("pList", pList);
				gson.toJson(result, response.getWriter());
			}
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}
	
	
	// 간병백과 앨범형 글 검색 요청
	@GetMapping("searchCareInformationAlbum.bo")
	@ResponseBody
	public void searchCareInformationAlbum(@RequestParam(value="page", defaultValue="1") int currentPage,
										@RequestParam("searchOption") String searchOption,
										@RequestParam("searchContent") String searchContent,
										HttpServletResponse response) {
		response.setContentType("application/json; charset=UTF-8");
		GsonBuilder gb = new GsonBuilder().setDateFormat("yyyy-MM-dd");
		Gson gson = gb.create();
		
		try {
			if(searchOption.equals("none")) { // 검색조건이 올바르지 않을 때 : 백에서도 안전하게 fail로 반환
				gson.toJson(null, response.getWriter());
			} else { // 올바른 검색조건을 선택했을 때
				HashMap<String, String> map = new HashMap<String, String>();
				if(searchOption.equals("title")) {
					map.put("column", "BOARD_TITLE");
				} else if(searchOption.equals("content")) {
					map.put("column", "BOARD_CONTENT");
				}
				map.put("searchContent", searchContent);
				
				int listCount = bService.getCareInfomationListCount(map);
				if(listCount == 0) {
					log.info("{} : {}", searchOption, searchContent); // 검색결과가 없을 때 로그를 발생시킨다.
					gson.toJson("empty", response.getWriter());
				} else {
					PageInfo pi = Pagination2.getPageInfo(currentPage, listCount, 9, 5);
					
					if(currentPage > pi.getMaxPage()) {
						gson.toJson(null, response.getWriter());
					} else {
						HashMap<String, ArrayList<?>> result = new HashMap<String, ArrayList<?>>();
						ArrayList<Board> bList = bService.searchCareInformation(map, pi);
						
						ArrayList<Attachment> aList = bService.selectAttachment(bList);
						ArrayList<Integer> pList = new ArrayList<Integer>();
						pList.add(pi.getMaxPage());
						
						result.put("bList", bList);
						result.put("aList", aList);
						result.put("pList", pList);
						
						gson.toJson(result, response.getWriter());
					}
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	// 문의게시판
	@GetMapping("qnaBoardList.bo")
	public String qnaBoardList(@RequestParam(value="page", defaultValue = "1") int currentPage, 
							   @RequestParam(value="myPage", defaultValue = "1") int myCurrentPage,
								Model model,
					            @RequestParam(value="categoryNo", defaultValue="-1") int categoryNo,
					            @RequestParam(value="area", required = false) List<Integer> areas, 
					            HttpServletRequest request, HttpSession session) {
	    // 페이지네이션
		int listCount = bService.getListCountQnA();   
	    PageInfo pi = Pagination.getPageInfo(currentPage, listCount, 10);
	    
	    // 모든 문의 내역
		ArrayList<Board> qnaList = bService.qnaBoardList(pi);
		
		if(qnaList != null) {
			model.addAttribute("qnaList", qnaList);
			model.addAttribute("pi",pi);
		}
		// 나의 문의내역 페이지
		int memberNo = ((Member)session.getAttribute("loginUser")).getMemberNo();
		int myListCount = bService.getMyListCountQnA(memberNo);
		PageInfo mpi = Pagination.getPageInfo(myCurrentPage, myListCount, 10);
		// 나의 문의 내역
		ArrayList<Board> myQnAList = bService.myQnAList(memberNo,mpi);
		if(myQnAList != null) {
			model.addAttribute("myQnAList", myQnAList);
			model.addAttribute("mpi",mpi);
			return "qnaBoard";
		}else {
			throw new BoardException("문의내역조회에 실패했습니다.");
		}
		
		
	}
	
	// 문의글 작성페이지
	@GetMapping("writeQnA.bo")
	public String writeQnABoard() {
		return "writeQnABoard";
	}
	
	// faq게시판
	@GetMapping("faqBoard.bo")
	public String faqBoard() {
		return "faqBoard";
	}
	
	// 이용가이드
	@GetMapping("userGuide.bo")
	public String userGuide() {
		return "userGuide";
	}
	
	
	
	
}
