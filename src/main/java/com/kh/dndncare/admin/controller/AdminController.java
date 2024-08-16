package com.kh.dndncare.admin.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
		PageInfo pi = Pagination2.getPageInfo(currentPage, listCount, 7, 5);
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
			return "admin/careInformation";
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
			//BufferedImage image = ImageIO.read(new File("\\\\192.168.40.37\\sharedFolder/dndnCare/thumbnail.png")); // 바탕이 될 썸네일 기본 이미지를 불러온다.
			BufferedImage image = ImageIO.read(new File("C:\\\\uploadFinalFiles/thumbnail.png"));
			image = thumbnailUtil.createThumbnail(image, b.getBoardTitle(), 25, 150); // 썸네일 기본 이미지 위에 텍스트 입력하기
			String thumbnailName = copyNameCreate() + ".png"; // 텍스트 입력한 썸네일 파일을 저장할 때 사용할 파일명
			//thumbnailUtil.saveImage(image, "\\\\192.168.40.37\\sharedFolder/dndnCare/admin/board/" + thumbnailName);
			thumbnailUtil.saveImage(image, "C:\\\\uploadFinalFiles/" + thumbnailName);
			
			// 3. DB에 전달할 데이터로서 가공한다. : "data:image/~~~~~"인 부분을 잘라내면됨
			int bResult = aService.insertCareInfomation(b); // 게시글 삽입 후 생성된 글 번호를 받아온다.
			int aResult = 0;
			if(bResult > 0) { // 게시글 삽입에 성공한 경우
				for(Attachment attm : aList) { // 첨부파일에 대한 정보를 주입
					attm.setRefBoardNo(b.getBoardNo()); // 첨부파일에 대한 참조글번호를 지정한다.
					attm.setOriginalName(b.getBoardTitle());
					//attm.setAttmPath("\\\\192.168.40.37\\sharedFolder/dndnCare/admin/board/" + renameName);
					attm.setAttmPath("C:\\\\uploadFinalFiles/" + renameName);
					attm.setAttmLevel(1);
				}
				
				Attachment thumbnail = new Attachment(); // 자동생성한 썸네일에 대한 정보를 주입
				thumbnail.setRefBoardNo(b.getBoardNo());
				thumbnail.setOriginalName("auto_Thumbnail");
				thumbnail.setRenameName(thumbnailName);
				thumbnail.setAttmPath("C:\\\\uploadFinalFiles/" + thumbnailName);
				//thumbnail.setAttmPath("\\\\192.168.40.37\\sharedFolder/dndnCare/admin/board/" + thumbnailName);
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
	@PostMapping("changeStatusCareInformation.adm")
	@ResponseBody
	public String changeStatusCareInformation(@RequestParam("boardNo") int boardNo,
												@RequestParam("status") String status) {
		System.out.println("삭제할 글 번호 : " + boardNo); // 삭제할 글 번호 : 181
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("boardNo", boardNo);
		map.put("status", status);
		
		int bResult = aService.changeStatusCareInformation(map);
		int aResult = aService.changeStatusAttachment(map);
		
		if(bResult == 1) {
			return "success";
		} else {
			return "fail";
		}
	}
	
	// 간병백과 게시글 수정
	@PostMapping("modifyCareInformation.adm")
	@ResponseBody
	public String modifyCareInformation(@ModelAttribute Board b, HttpSession session) {
//		테스트
//			원본에 이미지파일이 없었던 경우
//				수정할 때 이미지파일을 첨부한 경우1 				: 완료
//				수정할 때 이미지파일을 첨부하지 않은 경우2 		: 완료
//			원본에 이미지파일이 있었던 경우
//				원본의 이미지파일을 일부만 지우는 경우
//					새로운 이미지 파일을 삽입한 경우3			: 완료
//					새로운 이미지 파일을 삽입하지 않은 경우4		: 완료
//				원본의 이미지파일을 모두 지우는 경우
//					새로운 이미지 파일을 삽입한 경우5			: 완료
//					새로운 이미지 파일을 삽입하지 않은 경우6		: 완료
		
	//	Board(boardNo=222, memberNo=0, boardTitle=수정 테스트, boardContent=<p>원본 사진은 별입니다.</p><p><img src="2024081420473902143.png" contenteditable="false"><br></p><p><br></p><p>사진을 하나 더 추가합니다</p><p><img src="data:image/png;base64,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" contenteditable="false"><br></p>, boardStatus=null, boardCreateDate=null, boardUpdateDate=null, boardCount=0, categoryNo=0, areaNo=0, categoryName=null, areaName=null, memberNickName=null)
	//	기존의 이미지가 남아있다면 새롭게 암호화X, 새로운 이미지만 암호화O	
		try {
			// (1) 수정한 이미지가 있다면, 암호화를 해제하여 로컬 파일로 저장한다.
			String content = b.getBoardContent(); // 전달받은 HTML 문자열 전체
			int boardNo = b.getBoardNo();
			ArrayList<Attachment> aList = new ArrayList<Attachment>(); // 첨부 이미지들을 저장할 List
			ArrayList<String> base64List = new ArrayList<String>(); // content 중에서 이미지의 복호화만 추출
			String[] arr = content.split("<img src="); // 이미지 태그와 src속성을 기준으로 HTML을 자른다.
			String renameName = ""; // 첨부파일에 대한 리네임을 저장할 변수를 선언
			
			if(arr.length > 0) { // img 태그가 존재할 때 => 이미지가 변경되었는가?를 확인
				for (String s : arr) {
					if (s.contains("data:image")) { // 새롭게 첨부된 이미지가 존재하는 경우
						String temp = s.split(" ")[0]; // temp == "data:image/png:암호화"
						base64List.add(temp.substring(1, temp.length() - 1)); // 마지막 인덱스 전까지 잘라와야하므로 endIndex는 마지막 인덱스로 지정해야 한다.
					}
				}
				
				if(!base64List.isEmpty()) { // 새롭게 첨부된 이미지가 존재하는 경우 : 복호화하여 저장
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
			}
			
			Member loginUser = (Member)session.getAttribute("loginUser"); // 또 다른 관리자가 수정할 수 있으므로 작성자를 최신화해야한다.
			if(loginUser != null) { // 로그인이 되어있을 때만 작성자를 최신화할 수 있다.
				b.setMemberNo(loginUser.getMemberNo()); 
			}
			b.setBoardContent(content);
			b.setCategoryNo(7);
			b.setAreaNo(1); // 지역번호는 '서울'로 지정한다.
			
			// (2) 기존의 이미지에 대한 처리
			ArrayList<Attachment> oldAttmList = aService.selectOneAttachment(boardNo); // 원래의 첨부파일들을 조회(단, 썸네일은 제외함)
			Attachment oldThumbnailAttm = new Attachment();
			ArrayList<Integer> removeAttmNoList = new ArrayList<Integer>(); // 기존의 첨부파일 중 삭제해야할 첨부파일 번호를 담을 그릇
			ArrayList<String> removeFileNameList = new ArrayList<String>();
			
			if(oldAttmList != null && !oldAttmList.isEmpty()) { // 원래의 첨부파일이 존재하는 경우를 가르킨다.
				for(Attachment attm : oldAttmList) {
					if(attm.getAttmLevel() == 1) { // 썸네일이 아닌 첨부파일들만을 대상으로 한다.
						if(!content.contains(attm.getRenameName())) { // 수정한 boardContent에서 기존의 이미지가 제거된 경우를 가르킴
							removeAttmNoList.add(attm.getAttmNo());
							removeFileNameList.add(attm.getRenameName());
						}
					} else {
						oldThumbnailAttm = attm; // 썸네일인 첨부파일을 변수에 저장한다.
					}
				}
				
				if(removeAttmNoList.size() > 0) { // 기존 첨부파일 중 삭제해야할 파일이 존재하는 경우
					int removeResult = aService.deleteAttachment(removeAttmNoList); // DB에서 삭제한다.
					for(String fileName : removeFileNameList){ // 저장소에서 삭제한다.
						deleteFile(fileName);
					}
				}
			}
			
			
			// (3) 제목이 달라졌다면, 썸네일을 재생성한다.
			Board oldBoard = aService.selectOneBoard(boardNo);
			if(!oldBoard.getBoardTitle().equals(b.getBoardTitle())) { // 제목이 달라졌음
				int removeThumbnailResult = aService.deleteThumbnail(boardNo);// 기존의 썸네일을 DB에서 삭제한다.
				deleteFile(oldThumbnailAttm.getRenameName()); // 기존의 썸네일을 저장소에서 삭제한다.
				
				// 썸네일을 새롭게 생성한다.
				//BufferedImage image = ImageIO.read(new File("\\\\192.168.40.37\\sharedFolder/dndnCare/thumbnail.png")); // 바탕이 될 썸네일 기본 이미지를 불러온다.
				BufferedImage image = ImageIO.read(new File("C:\\\\uploadFinalFiles/thumbnail.png"));
				image = thumbnailUtil.createThumbnail(image, b.getBoardTitle(), 25, 150); // 썸네일 기본 이미지 위에 텍스트 입력하기
				String thumbnailName = copyNameCreate() + ".png"; // 텍스트 입력한 썸네일 파일을 저장할 때 사용할 파일명
				//thumbnailUtil.saveImage(image, "\\\\192.168.40.37\\sharedFolder/dndnCare/admin/board/" + thumbnailName);
				thumbnailUtil.saveImage(image, "C:\\\\uploadFinalFiles/" + thumbnailName);
				
				
				// 새로운 썸네일을 DB에 저장한다.
				Attachment thumbnail = new Attachment(); // 자동생성한 썸네일에 대한 정보를 주입
				thumbnail.setRefBoardNo(b.getBoardNo());
				thumbnail.setOriginalName("auto_Thumbnail");
				thumbnail.setRenameName(thumbnailName);
				thumbnail.setAttmPath("C:\\\\uploadFinalFiles/" + thumbnailName);
				//thumbnail.setAttmPath("\\\\192.168.40.37\\sharedFolder/dndnCare/admin/board/" + thumbnailName);
				thumbnail.setAttmLevel(0);
				int insertThumbnailResult = aService.insertThumbnail(thumbnail);
			}
			
			
			
			// (4) 수정한 이미지 이름을 이용하여 글 내용을 업데이트한다. : MEMBER_NO, BOARD_TITLE, BOARD_UPDATE_DATE, BOARD_CONTENT
			int updateBoardResut = aService.updateCareInformation(b);
			
			
			// 성공 기준을 게시글 업데이트만을 고려하기엔 너무 대충하는 것 같은데? **추후에 보완하자**
			return updateBoardResut == 1 ? "success" : "fail";
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void deleteFile(String fileName) {
		File saveFile = new File("C:\\uploadFinalFiles\\" + fileName);
		if(saveFile.exists()) saveFile.delete(); // 저장소 내에 파일이 존재할 때만 삭제한다.
	}
	
}
