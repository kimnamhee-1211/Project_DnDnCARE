package com.kh.dndncare.admin.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kh.dndncare.admin.model.exception.AdminException;
import com.kh.dndncare.admin.model.service.AdminService;
import com.kh.dndncare.admin.model.vo.Attachment;
import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.common.ImageUtil;
import com.kh.dndncare.common.Pagination2;
import com.kh.dndncare.common.ThumbnailUtil;
import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.vo.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
	
	@Autowired
	private AdminService aService;
	
	@Autowired
	private ThumbnailUtil thumbnailUtil;
	
	
	
	
	// 관리자 로그인 => 관리자 메인 페이지로 이동하는 메소드
	@GetMapping("adminMain.adm")
	public String adminMain() {

		return "adminMain";
	}

	// 간병정보(간병백과) 페이지로 이동하는 메소드
	@GetMapping("careInformation.adm")
	public String careInformation(@RequestParam(value="page", defaultValue="1") int currentPage, Model model,
									HttpServletRequest request) {
		// 페이징처리된 게시글 목록 조회 : BoardLimit == 7 (**가정**)
		int listCount = aService.getCareInformationListCount();
		PageInfo pi = Pagination2.getPageInfo(currentPage, listCount, 7);
		ArrayList<Board> bList = aService.selectAllCareInformation(pi); // 이래도 되나?
		ArrayList<Integer> bNoList = new ArrayList<Integer>();
		for(Board b : bList) {
			bNoList.add(b.getBoardNo());
		}
		ArrayList<Attachment> aList = aService.selectAttachment(bNoList);
		
		
		
		if(!bList.isEmpty()) {
			model.addAttribute("bList", bList);
			model.addAttribute("aList", aList);
			model.addAttribute("pi", pi);
			System.out.println(pi);
			model.addAttribute("loc", request.getRequestURI());
			return "careInformation";
		} else {
			throw new AdminException("서비스 요청에 실패하였습니다.");
		}
	}

	// 간병백과 작성 페이지로 이동
	@GetMapping("writeCareInformationPage.adm")
	public String writeCareInformation(HttpSession session) {
		Member loginUser = (Member) session.getAttribute("loginUser");
		if (loginUser != null) {
			if (loginUser.getMemberCategory().equals("A")) {
				return "writeCareInformation";
			} else {
				throw new MemberException("관리자로 로그인 후 이용해주세요.");
			}
		} else {
			throw new MemberException("로그인 후 이용해주세요");
		}
	}

	// 간병백과 작성 수행
	@PostMapping("writeCareInformation.adm")
	public String writeCareInformation(@ModelAttribute Board b) {
		try {
			// (1) HTML 문자열 중 암호화된 이미지를 복호화 + 이름을 재지정하여 공유폴더에 저장 
			String content = b.getBoardContent(); // 전달받은 HTML 문자열 전체
			ArrayList<Attachment> aList = new ArrayList<Attachment>(); // 첨부 이미지들을 저장할 List
			ArrayList<String> base64List = new ArrayList<String>(); // content 중에서 이미지의 복호화만 추출
			String[] arr = content.split("<img src="); // 이미지 태그와 src속성을 기준으로 HTML을 자른다.
			String renameName = ""; // 첨부파일에 대한 리네임을 저장할 변수를 선언
			
			for (String s : arr) {
				if (s.contains("data:image")) { // s == data:image/png:암호화 다른속성="어쩌고저쩌고" contenteditable="false"><br></p>
					String temp = s.split(" ")[0]; // temp == "data:image/png:암호화"
					base64List.add(temp.substring(1, temp.length() - 1)); // 마지막 인덱스 전까지 잘라와야하므로 endIndex는 마지막 인덱스로 지정해야 한다.
				}
			}
			
			if(!base64List.isEmpty()) {
				for(String b64 : base64List) {
					StringTokenizer st = new StringTokenizer(b64 ,"/;");
					int count = 0;
					String type = ""; // 첨부파일의 형식(jpg, png)을 저장할 변수
					
					while(st.hasMoreTokens()) {
						if(count < 1) {
							st.nextToken();
							count++;
							continue;
						}
						if(count == 1) {
							type=st.nextToken();
							break;
						}
					}
					
					String copyName = copyNameCreate(); // 첨부파일명(확장자가 없음)을 생성한다.
					renameName = copyName + "." + type; // 첨부파일명 + ".확장자"를 DB에 저장할 리네임으로 지정한다.
					ImageUtil.base64ToFile(copyName, b64);  // 첨부파일명과 암호화된 이미지src를 전달한다.
					
					if(content.contains(b64)) {
						content = content.replace(b64, renameName); // HTML의 암호화부분을 "첨부파일명.확장자"로 바꾸어 둔다. (View에서 출력하기 편리하게 하기 위함)
					}
					Attachment a = new Attachment();
					a.setRenameName(renameName); // 첨부파일에 대한 리네임을 명시한다.
					aList.add(a); // 첨부파일 리스트에 추가한다.
				}
			}
			
			b.setBoardContent(content);
			b.setCategoryNo(7);
			b.setAreaNo(1); // 지역번호는 '서울'로 지정한다.
			
			// 2. 썸네일 생성하기
			BufferedImage image = ImageIO.read(new File("\\\\192.168.40.37\\sharedFolder/dndnCare/thumbnail.png")); // 바탕이 될 썸네일 기본 이미지를 불러온다.
			image = thumbnailUtil.createThumbnail(image, b.getBoardTitle(), 25, 150); // 썸네일 기본 이미지 위에 텍스트 입력하기
			String thumbnailName = copyNameCreate() + ".png"; // 텍스트 입력한 썸네일 파일을 저장할 때 사용할 파일명
			thumbnailUtil.saveImage(image, "\\\\192.168.40.37\\sharedFolder/dndnCare/admin/board/" + thumbnailName);
			
			// 3. DB에 전달할 데이터로서 가공한다. : "data:image/~~~~~"인 부분을 잘라내면됨
			int bResult = aService.insertCareInfomation(b); // 게시글 삽입 후 생성된 글 번호를 받아온다.
			int aResult = 0;
			if(bResult > 0) { // 게시글 삽입에 성공한 경우
				for(Attachment attm : aList) { // 첨부파일에 대한 정보를 주입
					attm.setRefBoardNo(b.getBoardNo()); // 첨부파일에 대한 참조글번호를 지정한다.
					attm.setOriginalName(b.getBoardTitle());
					attm.setAttmPath("\\\\192.168.40.37\\sharedFolder/dndnCare/admin/board/" + renameName);
					attm.setAttmLevel(1);
				}
				
				Attachment thumbnail = new Attachment(); // 자동생성한 썸네일에 대한 정보를 주입
				thumbnail.setRefBoardNo(b.getBoardNo());
				thumbnail.setOriginalName("auto_Thumbnail");
				thumbnail.setRenameName(thumbnailName);
				thumbnail.setAttmPath("\\\\192.168.40.37\\sharedFolder/dndnCare/admin/board/" + thumbnailName);
				thumbnail.setAttmLevel(0);
				aList.add(thumbnail); // aList의 마지막 index에 썸네일을 add한다.
				
				if(!aList.isEmpty()) { // 첨부파일이 있을 때만 저장해야한다.
					aResult = aService.insertAttachment(aList);
				}
				
				if(aResult == aList.size()) { // 첨부파일 삽입 성공
					return "redirect:careInformation.adm";
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	// 이미지 파일명 제작해주는 메소드
	public String copyNameCreate() {// type : 이미지의 형식
		// 현재 시간을 yyyyMMddHHssSSS로 만들기
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHssSSS");
		String now = sdf.format(new java.util.Date());
		
		// 랜덤숫자 생성하기
		int random = (int)(Math.random()*10000);
		
		// 랜덤 파일명 만들기
		return now + random;
	}
	
	// 간병백과 게시글 삭제
	@PostMapping("deleteCareInformation.adm")
	@ResponseBody
	public String deleteCareInformation(@RequestParam("boardNo") int boardNo) {
		System.out.println("삭제할 글 번호 : " + boardNo); // 삭제할 글 번호 : 181
		
		int bResult = aService.hideCareInformation(boardNo);
		int aResult = aService.hideAttachment(boardNo);
		
		if(bResult == 1) {
			return "success";
		} else {
			return "fail";
		}
	}
	
	// 간병백과 게시글 수정
	@PostMapping("modifyCareInformation.adm")
	public String modifyCareInformation(@ModelAttribute Board b) {
		//Board(boardNo=181, memberNo=0, boardTitle=썸네일을만들어보아요, boardContent=<p>수정한 사진</p><p><img src="data:image/png;base64,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" contenteditable="false"><br></p>, boardStatus=null, boardCreateDate=null, boardUpdateDate=null, boardCount=0, categoryNo=0, areaNo=0, categoryName=null, areaName=null, memberNickName=null)
		try {
			// (1) 수정한 이미지가 있다면, 암호화를 해제하여 로컬 파일로 저장한다.
			
			
			
			// (2) 제목이 달라졌다면, 썸네일을 재생성한다.
			
			
			
			
			// (3) 수정한 이미지 이름을 이용하여 글 내용을 가공한다.
			
			
			
			
			// (4) DB에서 수정 : 글에 대한 정보, 첨부파일에 대한 정보
			
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
		
		return null;
	}
	
	
	
}
