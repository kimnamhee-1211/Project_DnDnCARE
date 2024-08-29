package com.kh.dndncare.admin.controller;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.kh.dndncare.admin.model.exception.AdminException;
import com.kh.dndncare.admin.model.service.AdminService;
import com.kh.dndncare.admin.model.vo.Attachment;
import com.kh.dndncare.board.model.exception.BoardException;
import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.board.model.vo.PageInfo;
import com.kh.dndncare.board.model.vo.Reply;
import com.kh.dndncare.common.ImageUtil;
import com.kh.dndncare.common.Pagination2;
import com.kh.dndncare.common.ThumbnailUtil;
import com.kh.dndncare.matching.model.vo.Matching;
import com.kh.dndncare.matching.model.vo.Pay;
import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.vo.Member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminController {
	
	@Autowired
	private AdminService aService;
	
	@Autowired
	private ThumbnailUtil thumbnailUtil;
	
	@Autowired
	private BCryptPasswordEncoder bCrypt;
	
	
	// 관리자 로그인 => 관리자 메인 페이지로 이동하는 메소드
	@GetMapping("adminMain.adm")
	public String adminMain() {

		return "adminMain";
	}

	// 간병정보(간병백과) 페이지로 이동하는 메소드
	@GetMapping("careInformation.adm")
	public String careInformation(@RequestParam(value="page", defaultValue="1") int currentPage, Model model,
									HttpServletRequest request) {
		// 로그 파일 : 페이지 이용량을 조회 (시작)
		File usageFolder = new File("C:/logs/careInformationUsage/");
		File[] usageFileList = usageFolder.listFiles(); // 사용량이 기록된 로그 파일들 모두에게 접근
		
		TreeMap<String, Integer> usageMap = new TreeMap<String, Integer>();
		try { 
			for(File f : usageFileList) {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String data;
				while((data=br.readLine())!=null) {
					// 24-08-17 21:25:77 [INFO] c.k.d.c.i.CheckCareInformationUsage.preHandle - test-m-p20
					String date = data.split(" ")[0];
					if(usageMap.containsKey(date)) { // map에 해당 날짜의 key가 존재하는 경우
						usageMap.put(date, usageMap.get(date) + 1);
					} else { // map에 해당 날짜의 key가 존재하지 않는 경우
						usageMap.put(date, 1);
					}
				}
				br.close();
			}
			model.addAttribute("usage", usageMap);
		} catch(Exception e) {
			e.printStackTrace();
		} 
		// 로그 파일 : 페이지 이용량을 조회 (끝)
			
		// 로그 파일 : 최근 일주일 검색어 조회 (시작)	
		File searchFolder = new File("C:/logs/careInformation/");
		File[] searchFileList = searchFolder.listFiles();
		//System.out.println(Arrays.toString(searchFileList));
		//[C:\logs\dndnCare\careInformation\careInformation.log, C:\logs\dndnCare\careInformation\careInformation.log.20240816]
		
		// 최근 일주일의 날짜에 접근
		Calendar c = GregorianCalendar.getInstance();
		int year = c.get(Calendar.YEAR); // 2024
		int month = c.get(Calendar.MONTH) + 1; // 7 + 1 == 8 (0부터 시작)
		String realMonth = month < 10 ? "0"+month : month+""; // 08
		int date = c.get(Calendar.DATE); // 18
		String now = year + realMonth + date; // 20240818
		int nowInteger = Integer.parseInt(now);
		
		// 일주일전 날짜를 20240811 로 출력하기
		c.set(year, month, date-7); 
		int agoYear = c.get(Calendar.YEAR);
		int agoMonth = c.get(Calendar.MONTH);
		String agoRealMonth = agoMonth < 10 ? "0"+agoMonth : agoMonth+"";
		int agoDate = c.get(Calendar.DATE);
		String agoRealDate = agoDate < 10 ? "0"+agoDate : agoDate+"";
		String ago = agoYear + agoRealMonth + agoRealDate; // 20240811
		int agoInteger = Integer.parseInt(ago);
		
		TreeMap<String, Integer> searchMap = new TreeMap<String, Integer>(); // key(검색어), value(횟수)
		//TreeMap<Integer, String> searchMap2 = new TreeMap<Integer, String>();
		try {
			for(File f : searchFileList) {
				String[] fileName = f.getName().split("log.");
				// log를 기준으로 자른 배열의 길이가 1인 경우 : fileName.length == 1)
				// log를 기준으로 자른 배열의 길이가 2인 경우 : Integer.parseInt(fileName[1]) >= agoInteger
				if(fileName.length == 1 ||  Integer.parseInt(fileName[1]) > agoInteger) {
					BufferedReader br = new BufferedReader(new FileReader(f));
					String data;
					while((data = br.readLine())!=null) {
						String[] arr = data.split(" ");
						String search = arr[arr.length - 1]; // 검색어
						
						if(searchMap.containsKey(search)) {
							searchMap.put(search, searchMap.get(search) + 1);
						} else {
							searchMap.put(search, 1);
						}
					}
					br.close();
				}
			}
			
			model.addAttribute("search", searchMap);
		} catch(Exception e) {
			e.printStackTrace();
		}
		// 로그 파일 : 검색어 조회 (끝)	
		
		
		// 페이징처리된 게시글 목록 조회 : BoardLimit == 7 (**가정**)
		int listCount = aService.getCareInformationListCount();
		PageInfo pi = Pagination2.getPageInfo(currentPage, listCount, 7, 5);
		ArrayList<Board> bList = aService.selectAllCareInformation(pi); // 이래도 되나?
		ArrayList<Integer> bNoList = new ArrayList<Integer>();
		ArrayList<Attachment> aList = new ArrayList<Attachment>();
		for(Board b : bList) {
			bNoList.add(b.getBoardNo());
		}
		if(!bNoList.isEmpty()) {
			aList = aService.selectAttachment(bNoList);
		}
		
		model.addAttribute("bList", bList);
		model.addAttribute("aList", aList);
		model.addAttribute("pi", pi);
		model.addAttribute("loc", request.getRequestURI());
		return "admin/careInformation";
		
	}

	// 간병백과 작성 페이지로 이동
	@GetMapping("writeCareInformationPage.adm")
	public String writeCareInformation(HttpSession session, Model model,
										@RequestParam("labels") ArrayList<String> labels,
										@RequestParam("data") ArrayList<String> data) {
		System.out.println(labels); // [이건없어, 아아, test-m-p20]
		System.out.println(data); // [19, 5, 3]
		
		
		Member loginUser = (Member) session.getAttribute("loginUser");
		if (loginUser != null) {
			if (loginUser.getMemberCategory().equals("A")) {
				model.addAttribute("labels", labels);
				model.addAttribute("data", data);
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
					
					if(type.equals("jpeg")) {
						type="jpg";
					}
					
					String copyName = copyNameCreate(); 				// 첨부파일명(확장자가 없음)을 생성한다.
					renameName = copyName + "." + type; 				// 첨부파일명 + ".확장자"를 DB에 저장할 리네임으로 지정한다.
				ImageUtil.base64ToFile(copyName, b64);  				// 첨부파일명과 암호화된 이미지src를 전달한다.
					
					if(content.contains(b64)) {
						content = content.replace(b64, renameName); 	// HTML의 암호화부분을 "첨부파일명.확장자"로 바꾸어 둔다. (View에서 출력하기 편리하게 하기 위함)
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
			BufferedImage image = ImageIO.read(new File("C:\\\\uploadFiles/careInformation/thumbnail.png"));
			image = thumbnailUtil.createThumbnail(image, b.getBoardTitle(), 50, 130); // 썸네일 기본 이미지 위에 텍스트 입력하기
			String thumbnailName = copyNameCreate() + ".png"; // 텍스트 입력한 썸네일 파일을 저장할 때 사용할 파일명
			//thumbnailUtil.saveImage(image, "\\\\192.168.40.37\\sharedFolder/dndnCare/admin/board/" + thumbnailName);
			thumbnailUtil.saveImage(image, "C:\\\\uploadFiles/careInformation/" + thumbnailName);
			
			// 3. DB에 전달할 데이터로서 가공한다. : "data:image/~~~~~"인 부분을 잘라내면됨
			int bResult = aService.insertCareInfomation(b); // 게시글 삽입 후 생성된 글 번호를 받아온다.
			int aResult = 0;
			if(bResult > 0) { // 게시글 삽입에 성공한 경우
				for(Attachment attm : aList) { // 첨부파일에 대한 정보를 주입
					attm.setRefBoardNo(b.getBoardNo()); // 첨부파일에 대한 참조글번호를 지정한다.
					attm.setOriginalName(b.getBoardTitle());
					//attm.setAttmPath("\\\\192.168.40.37\\sharedFolder/dndnCare/admin/board/" + renameName);
					attm.setAttmPath("C:\\\\uploadFiles/careInformation" + renameName);
					attm.setAttmLevel(1);
				}
				
				Attachment thumbnail = new Attachment(); // 자동생성한 썸네일에 대한 정보를 주입
				thumbnail.setRefBoardNo(b.getBoardNo());
				thumbnail.setOriginalName("auto_Thumbnail");
				thumbnail.setRenameName(thumbnailName);
				thumbnail.setAttmPath("C:\\\\uploadFiles/careInformation" + thumbnailName);
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
				BufferedImage image = ImageIO.read(new File("C:\\\\uploadFiles/careInformation/thumbnail.png"));
				image = thumbnailUtil.createThumbnail(image, b.getBoardTitle(), 25, 150); // 썸네일 기본 이미지 위에 텍스트 입력하기
				String thumbnailName = copyNameCreate() + ".png"; // 텍스트 입력한 썸네일 파일을 저장할 때 사용할 파일명
				//thumbnailUtil.saveImage(image, "\\\\192.168.40.37\\sharedFolder/dndnCare/admin/board/" + thumbnailName);
				thumbnailUtil.saveImage(image, "C:\\\\uploadFiles/careInformation/" + thumbnailName);
				
				
				// 새로운 썸네일을 DB에 저장한다.
				Attachment thumbnail = new Attachment(); // 자동생성한 썸네일에 대한 정보를 주입
				thumbnail.setRefBoardNo(b.getBoardNo());
				thumbnail.setOriginalName("auto_Thumbnail");
				thumbnail.setRenameName(thumbnailName);
				thumbnail.setAttmPath("C:\\\\uploadFiles/careInformation/" + thumbnailName);
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
		File saveFile = new File("C:\\uploadFiles\\careInformation\\" + fileName);
		if(saveFile.exists()) saveFile.delete(); // 저장소 내에 파일이 존재할 때만 삭제한다.
	}
	
	
	//결제정보 어드민
	@GetMapping("payInfoView.adm")
	public String payInfoView(Model model) {
		ArrayList<Pay> psDp = aService.selectPayDeposit("Y");
		for(Pay p : psDp) {
			System.out.println(p);
		}
		
		ArrayList<Pay> psDpN = aService.selectPayDeposit("N");
		
		model.addAttribute("psDp",psDp);
		model.addAttribute("psDpN",psDpN);
		System.out.println(psDp);
		return "payInfo";
	}
	
	// 관리자 게시판 관리
	@GetMapping("adminBoard.adm")
	public String adminBoardView(@RequestParam(value="caregiverPage", defaultValue = "1") int caregiverPage,
		    					@RequestParam(value="patientPage", defaultValue = "1") int patientPage, Model model) {
		// 간병인 커뮤니티 게시판 페이지네이션
		int caregiverListCount = aService.getCaregiverListCount();
		PageInfo cpi = Pagination2.getPageInfo(caregiverPage, caregiverListCount, 7, 5);
		
		// 환자 커뮤니티 게시판 페이지네이션
		int patientListCount = aService.getPatientListCount();
		PageInfo ppi = Pagination2.getPageInfo(patientPage, patientListCount, 7, 5);
		
		// 간병인 커뮤니티 게시판 type만들걸
		ArrayList<Board> adminCaregiverBoardList = aService.selectCaregiverBoardList(cpi);
		System.out.println(adminCaregiverBoardList);
		// 환자 커뮤니티
		ArrayList<Board> adminPatientBoardList = aService.selectPatientBoardList(ppi);
		System.out.println(adminPatientBoardList);
		
		model.addAttribute("cpi",cpi);
		model.addAttribute("ppi",ppi);
		model.addAttribute("cbList", adminCaregiverBoardList);
		model.addAttribute("pbList", adminPatientBoardList);
		return "adminBoard";
	}


	// 회원관리 페이지로 이동을 요청
	@GetMapping("members.adm")
	public String members(@RequestParam(value="page", defaultValue="1") int currentPage, Model model,
							HttpServletRequest request) {
		int listCount = aService.getMembersListCount();
		
		PageInfo pi = Pagination2.getPageInfo(currentPage, listCount, 10, 5);
		
		ArrayList<Member> mList = aService.selectWeekMembers(null, pi); // 일주일내 가입자만 조회
		//[Member(memberNo=126, memberId=comp3, memberName=환자삼, memberGender=F, memberNickName=환자삼, memberAge=1995-08-19, memberPhone=101019950819, memberEmail=comp3@naver.com, memberCreateDate=2024-08-19, memberAddress=16979//경기 용인시 기흥구 갈곡로 5//603호, memberCategory=P, memberStatus=Y, memberNational=외국인, memberPay=null, memberUpdateDate=2024-08-19, memberRealAge=0, career=null, license=null, matNo=0, groupLeader=null), Member(memberNo=125, memberId=com4, memberPwd=$2a$10$0O.0xJyjMbftmE248Q66l.RBY8HFICp/0Rw5bQbhkB5cG1IN.di/2, memberName=간병사, memberGender=M, memberNickName=간병사, memberAge=1995-08-19, memberPhone=01019950919, memberEmail=com4@naver.com, memberCreateDate=2024-08-19, memberAddress=16296//경기 수원시 장안구 수원북부순환로 188//1층, memberCategory=C, memberStatus=Y, memberNational=내국인, memberPay=null, memberUpdateDate=2024-08-19, memberRealAge=0, career=null, license=null, matNo=0, groupLeader=null), Member(memberNo=124, memberId=wododowo, memberPwd=$2a$10$47/mtsXP4MqBMivalH5ylefTVzgNGQvZzA2ALmbSfkjW3UHousVpe, memberName=vefi, memberGender=M, memberNickName=qpwl, memberAge=2018-11-29, memberPhone=231020, memberEmail=qwdqdqd@hanmail.net, memberCreateDate=2024-08-19, memberAddress=46732//부산 강서구 녹산산업북로221번가길 12//123, memberCategory=P, memberStatus=Y, memberNational=내국인, memberPay=null, memberUpdateDate=2024-08-19, memberRealAge=0, career=null, license=null, matNo=0, groupLeader=null), Member(memberNo=123, memberId=test444, memberPwd=$2a$10$AUnATtheupsBs/uXNWcZaez9xU7PtH1udvo0KzL69Uo.UiQk1AN8O, memberName=김장군, memberGender=M, memberNickName=하하슴사, memberAge=2019-05-08, memberPhone=01077651258, memberEmail=rlarlfyd1258@naver.com, memberCreateDate=2024-08-19, memberAddress=02179//서울 중랑구 망우로74가길 16//3층, memberCategory=C, memberStatus=Y, memberNational=내국인, memberPay=null, memberUpdateDate=2024-08-19, memberRealAge=0, career=null, license=null, matNo=0, groupLeader=null), Member(memberNo=121, memberId=test888, memberPwd=$2a$10$gO7yt0za5L6UQozzgO54ye9MSDl4S5ntjziMJpMBY5KXcl6.79jhm, memberName=테스트, memberGender=M, memberNickName=ㄹㅇㄷㅇ, memberAge=1996-12-30, memberPhone=02023232, memberEmail=fwefq@naver.com, memberCreateDate=2024-08-16, memberAddress=06231//서울 강남구 도곡로33길 5//23424, memberCategory=C, memberStatus=Y, memberNational=내국인, memberPay=null, memberUpdateDate=2024-08-16, memberRealAge=0, career=null, license=null, matNo=0, groupLeader=null), Member(memberNo=120, memberId=comp2, memberPwd=$2a$10$lsWB0g12D0kD94lwWgETTOmJVCxjzqVJ9WgSsS.g7bvsBfhQFFObS, memberName=환자이, memberGender=M, memberNickName=환자이, memberAge=1991-01-09, memberPhone=01019910109, memberEmail=comp222@nate.com, memberCreateDate=2024-08-14, memberAddress=30121//세종특별자치시 가름로 170-14//501호, memberCategory=P, memberStatus=N, memberNational=내국인, memberPay=null, memberUpdateDate=2024-08-14, memberRealAge=0, career=null, license=null, matNo=0, groupLeader=null), Member(memberNo=119, memberId=com3, memberPwd=$2a$10$QotyvaK.GnWajEhT8x30Ve7g4BRJ3zqGEr.mRy8sdSqOeI2q5BD12, memberName=환자이, memberGender=M, memberNickName=간병삼, memberAge=1989-09-08, memberPhone=01019890908, memberEmail=com3333@gmail.com, memberCreateDate=2024-08-14, memberAddress=22233//인천 미추홀구 경원대로 627//202호, memberCategory=C, memberStatus=Y, memberNational=내국인, memberPay=null, memberUpdateDate=2024-08-14, memberRealAge=0, career=null, license=null, matNo=0, groupLeader=null), Member(memberNo=118, memberId=test333, memberPwd=$2a$10$Ct1pdkKLUVYwnIT0FYs49eFTeGr/.9qRlYvIZnOCkxp2JPlNF/2hO, memberName=test333, memberGender=M, memberNickName=test333, memberAge=1988-12-06, memberPhone=00055559999, memberEmail=test333@gmail.com, memberCreateDate=2024-08-14, memberAddress=05571//서울 송파구 올림픽로 지하 23//야구장, memberCategory=P, memberStatus=Y, memberNational=내국인, memberPay=null, memberUpdateDate=2024-08-14, memberRealAge=0, career=null, license=null, matNo=0, groupLeader=null), Member(memberNo=117, memberId=rlfyd1235, memberPwd=$2a$10$K5qYH6czqEU6F3IXp9gMiO2cBWmk4HtnEQd78.rcYg9JwJRf3rmZy, memberName=김기룡, memberGender=M, memberNickName=하하하하, memberAge=2012-02-13, memberPhone=01077651258, memberEmail=rlarlfyd1258@naver.com, memberCreateDate=2024-08-13, memberAddress=02179//서울 중랑구 망우로74가길 16//3층, memberCategory=P, memberStatus=N, memberNational=내국인, memberPay=null, memberUpdateDate=2024-08-13, memberRealAge=0, career=null, license=null, matNo=0, groupLeader=null), Member(memberNo=116, memberId=wodudkakao, memberPwd=$2a$10$Ec0hjucM2GQWQxykPAhVq.TX2F4QrZRBrwwmCZOADhbOb218RROjG, memberName=재영님, memberGender=M, memberNickName=재영님꺼, memberAge=1990-09-02, memberPhone=01055434392, memberEmail=doddoxl@naver.com, memberCreateDate=2024-08-12, memberAddress=05259//서울 강동구 구천면로55길 5//220호, memberCategory=C, memberStatus=N, memberNational=내국인, memberPay=3661421183, memberUpdateDate=2024-08-12, memberRealAge=0, career=null, license=null, matNo=0, groupLeader=null)]
		
		// 나이 계산
		Calendar c = GregorianCalendar.getInstance();
		int year = c.get(Calendar.YEAR);
		for(Member m : mList) {
			String memberYear = m.getMemberAge().toString().split("-")[0];
			m.setMemberRealAge(year - Integer.parseInt(memberYear));
		}
		
		model.addAttribute("mList", mList);
		model.addAttribute("pi", pi);
		model.addAttribute("loc", request.getRequestURI());
		
		membersGraph(model);
		
		
		return "members";
	}
	
	
	// 그래프 작업용 메소드를 따로 만들기
	public void membersGraph(Model model) {
		// 로그 읽어오기 : C:\logs\dndnCare\loginUser
		File folder = new File("C:/logs/loginUser");
		File[] fileList = folder.listFiles();
		
		// 2주치의 로그파일을 1주일씩 각각 읽어오기
		TreeMap<String, Integer> oneWeekAgo = new TreeMap<String, Integer>();
		TreeMap<String, Integer> twoWeekAgo = new TreeMap<String, Integer>();
		
		// 일주일 전의 날짜를 yyyyMMdd로 뽑아내기
		Calendar now = GregorianCalendar.getInstance();
		int nowYear = now.get(Calendar.YEAR);
		int nowMonth = now.get(Calendar.MONTH) + 1;
		int nowDate = now.get(Calendar.DATE);
		
		Calendar oneWeek = GregorianCalendar.getInstance();
		Calendar twoWeek = GregorianCalendar.getInstance();
		oneWeek.set(nowYear, nowMonth, nowDate - 7);
		twoWeek.set(nowYear, nowMonth, nowDate - 14);
		
		int oneWeekYear = oneWeek.get(Calendar.YEAR);
		int oneWeekMonth = oneWeek.get(Calendar.MONTH);
		String oneWeekRealMonth = oneWeekMonth < 10 ? "0" + oneWeekMonth : oneWeekMonth + "";
		int oneWeekDate = oneWeek.get(Calendar.DATE);
		String oneWeekRealDate = oneWeekDate < 10 ? "0" + oneWeekDate : oneWeekDate + "";
		
		int twoWeekYear = twoWeek.get(Calendar.YEAR);
		int twoWeekMonth = twoWeek.get(Calendar.MONTH);
		String twoWeekRealMonth = twoWeekMonth < 10 ? "0" + twoWeekMonth : twoWeekMonth + "";
		int twoWeekDate = twoWeek.get(Calendar.DATE);
		String twoWeekRealDate = twoWeekDate < 10 ? "0" + twoWeekDate : twoWeekDate + "";
		
		String oneWeekFormat = oneWeekYear + oneWeekRealMonth + oneWeekRealDate;
		Integer oneWeekInteger = Integer.parseInt(oneWeekFormat);
		String twoWeekFormat = twoWeekYear + twoWeekRealMonth + twoWeekRealDate;
		Integer twoWeekInteger = Integer.parseInt(twoWeekFormat);
		
		// 파일명의 형식 => careInformation.log, careInformation.log.20240816
		try {
			for(File f : fileList) {
				String fileName = f.getName();
				String[] nameArr = fileName.split(".log");
				
				BufferedReader br = new BufferedReader(new FileReader(f));
				
				if(nameArr.length == 1 || Integer.parseInt(nameArr[1]) > oneWeekInteger) {
					// 최근 일주일치의 로그파일들
					String read;
					while((read = br.readLine()) != null) {
						//24-08-20 13:55:78 [INFO] c.k.d.c.i.CheckLoginUser.postHandle - test-m-a
						String date = read.split(" ")[0];
						if(oneWeekAgo.containsKey(date)) {
							oneWeekAgo.put(date, oneWeekAgo.get(date) + 1);
						} else {
							oneWeekAgo.put(date, 1);
						}
					} // if문 (끝)
				} else if(Integer.parseInt(nameArr[1]) > twoWeekInteger) {
					// 14일 이전 ~ 7일 이전까지의 로그파일들
					String read;
					while((read = br.readLine()) != null) {
						//24-08-20 13:55:78 [INFO] c.k.d.c.i.CheckLoginUser.postHandle - test-m-a
						String date = read.split(" ")[0];
						if(twoWeekAgo.containsKey(date)) {
							twoWeekAgo.put(date, twoWeekAgo.get(date) + 1);
						} else {
							twoWeekAgo.put(date, 1);
						}
					}
				} // else if문 (끝)
				
				br.close();
			}
			
			model.addAttribute("oneWeekAgo", oneWeekAgo);
			model.addAttribute("twoWeekAgo", twoWeekAgo);
			
		} catch(Exception e){
			e.printStackTrace();
		}
		
		// 가입자 수 조회
		HashMap<String, Integer> oneWeekOption = new HashMap<String, Integer>();
		oneWeekOption.put("begin", 6);
		oneWeekOption.put("end", 0);
		
		HashMap<String, Integer> twoWeekOption = new HashMap<String, Integer>();
		twoWeekOption.put("begin", 13);
		twoWeekOption.put("end", 7);
		
		ArrayList<HashMap<String, Object>> oneWeekEnroll = aService.getEnrollCount(oneWeekOption);
		ArrayList<HashMap<String, Object>> twoWeekEnroll = aService.getEnrollCount(twoWeekOption);
		// [{DT=0024-08-14 00:00:00.0, CNT=3}, {DT=0024-08-20 00:00:00.0, CNT=3}, {DT=0024-08-16 00:00:00.0, CNT=1}, {DT=0024-08-19 00:00:00.0, CNT=6}]
		
		for(HashMap<String, Object> m : oneWeekEnroll) {
			m.put("DT", String.valueOf(m.get("DT")).split(" ")[0].substring(2)); // [{DT=24-08-14, CNT=3}, {DT=24-08-16, CNT=1}, {DT=24-08-19, CNT=6}, {DT=24-08-20, CNT=3}]
		}
		
		for(HashMap<String, Object> m : twoWeekEnroll) {
			m.put("DT", String.valueOf(m.get("DT")).split(" ")[0].substring(2)); 
		}
		
		model.addAttribute("oneWeekEnroll", oneWeekEnroll);
		model.addAttribute("twoWeekEnroll", twoWeekEnroll);
	}
	
	// 전체 회원 목록 조회 요청
	@GetMapping("allMembers.adm")
	public String allMembers(@RequestParam(value="page", defaultValue="1") int currentPage,
								HttpServletRequest request, Model model) {
		int listCount = aService.getAllMembersListCount();
		PageInfo pi = Pagination2.getPageInfo(currentPage, listCount, 10, 5);
		
		ArrayList<Member> mList = aService.selectAllMembers(null, pi);
		
		// 나이 계산
		Calendar c = GregorianCalendar.getInstance();
		int year = c.get(Calendar.YEAR);
		for(Member m : mList) {
			String memberYear = m.getMemberAge().toString().split("-")[0];
			m.setMemberRealAge(year - Integer.parseInt(memberYear));
		}
		
		model.addAttribute("mList", mList);
		model.addAttribute("pi", pi);
		model.addAttribute("loc", request.getRequestURI());
		
		membersGraph(model);
		
		return "members";
	}
	
	// 회원검색
	@GetMapping("searchMembers.adm")
	public String searchMembers(@RequestParam("searchOption") String searchOption, @RequestParam("searchContent") String searchContent,
								@RequestParam(value="page", defaultValue="1") int currentPage, HttpServletRequest request,
								Model model) {
		HashMap<String, String> map = new HashMap<String, String>();
		if(searchOption.equals("memberId")) {
			map.put("column", "MEMBER_ID");
		} else {
			map.put("column", "MEMBER_NO");
		}
		map.put("data", searchContent);
		
		int listCount = aService.getSearchMemberListCount(map);
		PageInfo pi = Pagination2.getPageInfo(currentPage, listCount, 10, 5);
		ArrayList<Member> mList = aService.searchMembers(map, pi);
		
		// 나이 계산
		Calendar c = GregorianCalendar.getInstance();
		int year = c.get(Calendar.YEAR);
		for(Member m : mList) {
			String memberYear = m.getMemberAge().toString().split("-")[0];
			m.setMemberRealAge(year - Integer.parseInt(memberYear));
		}
			
		model.addAttribute("pi", pi);
		model.addAttribute("mList", mList);
		model.addAttribute("loc", request.getRequestURI());
		model.addAttribute("searchOption", searchOption);
		model.addAttribute("searchContent", searchContent);
		return "members";
		
	}
	
	// 회원의 상태와 카테고리를 변경 요청
	@PostMapping("updateMembers.adm")
	@ResponseBody
	public String updateMembers(@RequestParam("memberNo") int memberNo, @RequestParam("column") String column, 
								@RequestParam("data") String data) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("memberNo", memberNo);
		map.put("data", data);
		// 확장성을 고려하여 switch문으로 작성
		switch(column) {
		case "status": map.put("column", "MEMBER_STATUS"); break;
		case "category" : map.put("column", "MEMBER_CATEGORY"); break;
		case "MEMBER_AGE":  
			Calendar c = GregorianCalendar.getInstance();
			int year = c.get(Calendar.YEAR); // 현재년도
			String memberAge = aService.getMemberAge(memberNo); // yyyy-MM-dd의 포맷으로 조회한다.
			int month = Integer.parseInt(memberAge.split("-")[1]);
			int day = Integer.parseInt(memberAge.split("-")[2]);
			
			c.set(year - Integer.parseInt(data), month, day);
			
			map.put("data", new Date(c.getTimeInMillis()));
			map.put("column", column);
			
			break; // data를 덮어씀으로써 DB에 적합하게 가공해야 한다.
		default: map.put("column", column);
		}
		
		int result = aService.updateMembers(map);
		
		return result == 1 ? "success" : "fail";
	}
	
	@PostMapping("selectMemberInfo.adm")
	@ResponseBody
	public void selectMemberInfo(@RequestParam("memberNo") int memberNo) {
		// 어떤 정보를 조회해야하는가? 미쳤네
//		<간병인 회원가입시 입력사항>
//		원하는 서비스 (1~3개, 필수)				: MEMBER_INFO
//		공동간병 매칭서비스 참여여부 (1개, 필수) 	: CAREGIVER
//		경력기간 (1개, 필수) 					: MEMBER_INFO 
//		서비스경험 (0~3개, 선택)				: MEMBER_INFO 
//		돌봄경험 (0~10개, 선택)					: MEMBER_INFO 
//		자격증 (0~3개, 선택)					: MEMBER_INFO 
//		적정비용 (최소&최대, 필수)				: CAREGIVER
//
//		<환자 회원가입시 입력사항>
//		이름, 성별, 생년월일 (필수)
//		원하는 서비스 (1~3개, 필수)				: MEMBER_INFO 
//		서비스받을 주소 (필수)					: PATIENT
//		보유질환 (0~10개, 선택)					: MEMBER_INFO 
//		키	(필수)							: PATIENT
//		몸무게  (필수)							: PATIENT
	}
	
	// 게시글(공지) 작성
	@PostMapping("insertAnnouncement.adm")
	public String insertAnnouncement(@ModelAttribute Board b, HttpSession session) {
		int memberNo = ((Member)session.getAttribute("loginUser")).getMemberNo();
		b.setMemberNo(memberNo);
		b.setMemberNickName("관리자");
		
		System.out.println("------------");
		System.out.println(b);
		System.out.println("------------");
		
		int result = aService.insertAnnouncement(b);
		if(result > 0) {
			return "redirect:adminBoard.adm";
		}else {
			throw new BoardException("공지 작성에 실패하였습니다.");
		}
	}
	
	// 게시글 상태변경
	@GetMapping("updateAdminBoardStatus.adm")
	@ResponseBody
	public String updateAdminBoardStatus(@RequestParam("boardNo") int boardNo, @RequestParam("boardStatus") String boardStatus ) {
		int result = aService.updateAdminBoardStatus(boardNo, boardStatus);
		if(result > 0) {
			return result == 1 ? "success" : "fail";
		}else {
			throw new AdminException("게시글 상태변경에 실패했습니다.");
		}
	}
	
	
	// 게시물 상세조회
	@GetMapping("selectAdminBoard.adm")
	public String selectAdminBoard(@RequestParam("bNo") int bNo, @RequestParam("page") int page,  Model model) {
		// 게시글
		Board boardList = aService.adminSelectBoard(bNo);

		// 댓글
		ArrayList<Reply> replyList = aService.adminSelectReply(bNo);
		
		System.out.println("===================");
		System.out.println(boardList);
		System.out.println("+++++++++++++++++++");
		System.out.println(replyList);
		System.out.println("===================");
		
		if(boardList != null) {
			model.addAttribute("b", boardList);
			model.addAttribute("replyList", replyList);
			return "adminBoardDetail";
		}else {
			throw new AdminException("게시글 상세보기에 실패했습니다.");
		}
	}
	
	// 게시글 삭제
	@PostMapping("amdimDeleteBoard.adm")
	public String adminDeleteBoard(@RequestParam("boardNo") int boardNo) {
		int result = aService.adminDeleteBoard(boardNo);
		if(result > 0) {
			return "redirect:adminBoard.adm";
		}else {
			throw new AdminException("게시글 삭제에 실패했습니다.");
		}
	}
	
	//어드민 페이 토탈 통계 에이작스
	
	@GetMapping("weekPayTotal.adm")
	@ResponseBody
	public void weekPayTotal(@RequestParam("data1") String data1,@RequestParam("data2") String data2, HttpServletResponse response) {
		
		int year1 = Integer.parseInt(data1.split("-")[0]);
		int month1 = Integer.parseInt(data1.split("-")[1]);
		int day1 = Integer.parseInt(data1.split("-")[2]);
		
		LocalDate date1 = LocalDate.of(year1, month1, day1);
        
        
        //long daysBetween = ChronoUnit.DAYS.between(date1, date2);
        
        HashMap<String,Object> map = new HashMap<String,Object>();
        
        
        String[] labels = {date1.plusDays(0).toString(),date1.plusDays(1).toString(),date1.plusDays(2).toString()
        					,date1.plusDays(3).toString(),date1.plusDays(4).toString(),date1.plusDays(5).toString(),date1.plusDays(6).toString()};
        map.put("labels", labels);
        
        int[] datas1 = new int[7];
        int[] datas2 = new int[7];
        
        
        ArrayList<Pay> psDp = aService.selectPayDeposit("Y");		//페이 다가져와
		for(Pay p : psDp) {
			System.out.println("결제날짜 = " + p.getPayDate());
			
			if(p.getPayService().equals("가정돌봄")){
				
				if(p.getPayDate().toLocalDate().equals(date1)) {
					datas1[0] += p.getPayMoney();
				}else if(p.getPayDate().toLocalDate().equals(date1.plusDays(1))) {
					datas1[1] += p.getPayMoney();
				}else if(p.getPayDate().toLocalDate().equals(date1.plusDays(2))) {
					datas1[2] += p.getPayMoney();
				}else if(p.getPayDate().toLocalDate().equals(date1.plusDays(3))) {
					datas1[3] += p.getPayMoney();
				}else if(p.getPayDate().toLocalDate().equals(date1.plusDays(4))) {
					datas1[4] += p.getPayMoney();
				}else if(p.getPayDate().toLocalDate().equals(date1.plusDays(5))) {
					datas1[5] += p.getPayMoney();
				}else if(p.getPayDate().toLocalDate().equals(date1.plusDays(6))) {
					datas1[6] += p.getPayMoney();
				}
			}else if(p.getPayService().equals("병원돌봄")) {
				
				if(p.getPayDate().toLocalDate().equals(date1)) {
					datas2[0] += p.getPayMoney();
				}else if(p.getPayDate().toLocalDate().equals(date1.plusDays(1))) {
					datas2[1] += p.getPayMoney();
				}else if(p.getPayDate().toLocalDate().equals(date1.plusDays(2))) {
					datas2[2] += p.getPayMoney();
				}else if(p.getPayDate().toLocalDate().equals(date1.plusDays(3))) {
					datas2[3] += p.getPayMoney();
				}else if(p.getPayDate().toLocalDate().equals(date1.plusDays(4))) {
					datas2[4] += p.getPayMoney();
				}else if(p.getPayDate().toLocalDate().equals(date1.plusDays(5))) {
					datas2[5] += p.getPayMoney();
				}else if(p.getPayDate().toLocalDate().equals(date1.plusDays(6))) {
					datas2[6] += p.getPayMoney();
				}
				
			}
			
		}
        map.put("datas1",datas1);
        map.put("datas2",datas2);
        
        Gson gson = new Gson();
		response.setContentType("application/json; charset=UTF-8;");
		try {
			gson.toJson(map, response.getWriter());
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	        
			
	}//페이토탈메소드끝
	
	//어드민 페이 토탈 통계 달
	
		@GetMapping("monthPayTotal.adm")
		@ResponseBody
		public void monthPayTotal(@RequestParam("data1") String data1,@RequestParam("data2") String data2,@RequestParam("flag") int flag, HttpServletResponse response) {
			ArrayList<Pay> psDp = aService.selectPayDeposit("Y");		//페이 다가져와
			if(flag == 1) {
				int year1 = Integer.parseInt(data1.split(",")[0]);
				int month1 = Integer.parseInt(data2.split(",")[0]);
				
				//일수 계산하자
				YearMonth yearMonth = YearMonth.of(year1, month1);
				int days = yearMonth.lengthOfMonth();
				System.out.println("내년월수 뭐야?" + yearMonth);
		        HashMap<String,Object> map = new HashMap<String,Object>();
		        
		        
		        int[] labels = new int[days];
		        for(int i =1 ; i <=labels.length ; i++) {
		        	labels[i-1] = i;
		        }
		        map.put("labels", labels);
		        
		        int[] datas1 = new int[days];
		        int[] datas2 = new int[days];
		        
		        
		        
		        for(Pay p : psDp) {
		        	if(p.getPayService().equals("가정돌봄")){
						for(int i = 1; i <= labels.length ; i++) {
							if(p.getPayDate().toLocalDate().equals(LocalDate.of(year1, month1, i))) {
								datas1[i-1] += p.getPayMoney();
							}
							
						}
		        		
					}else {
						for(int i = 1; i <= labels.length ; i++) {
							if(p.getPayDate().toLocalDate().equals(LocalDate.of(year1, month1, i))) {
								datas2[i-1] += p.getPayMoney();
							}
							
						}
						
					}
		        	
		        }
		        
		        map.put("datas1",datas1);
		        map.put("datas2",datas2);
		        
		        Gson gson = new Gson();
				response.setContentType("application/json; charset=UTF-8;");
				try {
					gson.toJson(map, response.getWriter());
				} catch (JsonIOException | IOException e) {
					e.printStackTrace();
				}
			}else {	//여러 달 검색하는것
				int year1 = Integer.parseInt(data1.split(",")[0]);
				int year2 = Integer.parseInt(data1.split(",")[1]);
				int month1 = Integer.parseInt(data2.split(",")[0]);
				int month2 = Integer.parseInt(data2.split(",")[1]);
				
				
				HashMap<String,Object> map = new HashMap<String,Object>();
				int size = month2-month1+1;
				if(year1 != year2 ) {
					size += 12 * ( year2 - year1);
				}
				
				String[] labels = new String[size];
				int j = 0;
				int k = 0;
				for(int i = 1 ; i <= size ; i++) {
					String labelMonth = "0"+((month1+i-1) % 12 == 0 ? "12" : (month1+i-1) % 12);
		        	labels[j] = year1+ k + "-" +labelMonth.substring(labelMonth.length() - 2);
		        	
		        	j += 1;
		        	if(i % 12 == 0) {
		        		k +=1;
		        	}
				}
				
		        System.out.println("확인해보자" + labels.length);
		        System.out.println("확인해보자" + labels[0]);
		        map.put("labels", labels);
		        
		        int[] datas1 = new int[labels.length];
		        int[] datas2 = new int[labels.length];
		        
		        
		        System.out.println("확인해보자" + labels.length);
		        System.out.println("확인해보자" + datas1.length);
		        System.out.println("확인해보자" + datas2.length);
		        for(Pay p : psDp) {
		        	if(p.getPayService().equals("가정돌봄")){
		        		for(int i = 0; i < labels.length ; i++) {
		        			if(labels[i].equals(YearMonth.from(p.getPayDate().toLocalDate()).toString())){
		        				datas1[i] += p.getPayMoney();
		        			}
		        		}
		        	}else {
		        		for(int i = 0; i < labels.length ; i++) {
		        			if(labels[i].equals(YearMonth.from(p.getPayDate().toLocalDate()).toString())){
		        				datas2[i] += p.getPayMoney();
		        			}
		        		}
		        	}
		        };
		        
		        map.put("datas1",datas1);
		        map.put("datas2",datas2);
		        
		        Gson gson = new Gson();
				response.setContentType("application/json; charset=UTF-8;");
				try {
					gson.toJson(map, response.getWriter());
				} catch (JsonIOException | IOException e) {
					e.printStackTrace();
				}
			}
	        
		}//페이토탈메소드끝 월
	
		//어드민 페이 토탈 통계 년
		
			@GetMapping("yearPayTotal.adm")
			@ResponseBody
			public void yearPayTotal(@RequestParam("data1") String data1,@RequestParam("data2") String data2,@RequestParam("flag") int flag, HttpServletResponse response) {
			
				ArrayList<Pay> psDp = aService.selectPayDeposit("Y");		//페이 다가져와
				if(flag ==1) {
					int year = Integer.parseInt(data1.split(",")[0]);
					
					HashMap<String,Object> map = new HashMap<String,Object>();
					
					String[] labels = new String[12];
					for(int i = 1 ; i <= 12 ; i++) {
						String labelMonth = "0"+i;
						labels[i-1] = year +"-"+ labelMonth.substring(labelMonth.length()-2);
					}
					map.put("labels", labels);
					
					int[] datas1 = new int[12];
			        int[] datas2 = new int[12];
			        
			        for(Pay p : psDp) {
			        	if(p.getPayService().equals("가정돌봄")){
			        		for(int i = 0; i < 12 ; i++) {
			        			if(labels[i].equals(YearMonth.from(p.getPayDate().toLocalDate()).toString())){
			        				datas1[i] += p.getPayMoney();
			        			}
			        		}
			        	}else {
			        		for(int i = 0; i < labels.length ; i++) {
			        			if(labels[i].equals(YearMonth.from(p.getPayDate().toLocalDate()).toString())){
			        				datas2[i] += p.getPayMoney();
			        			}
			        		}
			        	}
			        };
			        
			        map.put("datas1",datas1);
			        map.put("datas2",datas2);
			        
			        Gson gson = new Gson();
					response.setContentType("application/json; charset=UTF-8;");
					try {
						gson.toJson(map, response.getWriter());
					} catch (JsonIOException | IOException e) {
						e.printStackTrace();
					}
					
				}else {
					int year1 = Integer.parseInt(data1.split(",")[0]);
					int year2 = Integer.parseInt(data1.split(",")[1]);
					
					HashMap<String,Object> map = new HashMap<String,Object>();
					
					String[] labels = new String[year2 - year1 +1];
					int j =0;
					for(int i = year1 ; i <= year2 ; i++) {
						labels[j] = year1+ j +"년" ;
						j += 1;
					}
					
					map.put("labels", labels);
										
					int[] datas1 = new int[year2 - year1 +1];
			        int[] datas2 = new int[year2 - year1 +1];
					
			        for(Pay p : psDp) {
			        	if(p.getPayService().equals("가정돌봄")){
			        		for(int i = 0; i < labels.length ; i++) {
			        			if(labels[i].equals(p.getPayDate().toLocalDate().getYear()+"년")){
			        				datas1[i] += p.getPayMoney();
			        			}
			        		}
			        	}else {
			        		for(int i = 0; i < labels.length ; i++) {
			        			if(labels[i].equals(p.getPayDate().toLocalDate().getYear()+"년")){
			        				datas2[i] += p.getPayMoney();
			        			}
			        		}
			        	}
			        };
			        
			        map.put("datas1",datas1);
			        map.put("datas2",datas2);
			        
			        Gson gson = new Gson();
					response.setContentType("application/json; charset=UTF-8;");
					try {
						gson.toJson(map, response.getWriter());
					} catch (JsonIOException | IOException e) {
						e.printStackTrace();
					}
			        
				}
			
			}// 년 메소드
	
	// 댓글 삭제
	@GetMapping("adminDeleteReply.adm")
	@ResponseBody
	public String adminDeleteReply(@RequestParam("rNo") int rNo) {
		int result = aService.adminDeleteReply(rNo);
		if(result > 0) {
			return result == 1 ? "success" : "fail";
		}else {
			throw new AdminException("댓글작성에 실패했습니다.");
		}
		
	}
	
	
	//결제정보 어드민
	@GetMapping("matching.adm")
	public String matchingView(Model model) {
		
		
		
		ArrayList<Matching> mat = aService.selectMatchings();
		
		
		model.addAttribute("mat",mat);
		return "matchingInfo";
	}
	
	
	//어드민 페이 토탈 통계 에이작스 (매칭 일주일)
	
	@GetMapping("weekMatTotal.adm")
	@ResponseBody
	public void weekMatTotal(@RequestParam("data1") String data1,@RequestParam("data2") String data2, HttpServletResponse response) {
		
		int year1 = Integer.parseInt(data1.split("-")[0]);
		int month1 = Integer.parseInt(data1.split("-")[1]);
		int day1 = Integer.parseInt(data1.split("-")[2]);
		
		LocalDate date1 = LocalDate.of(year1, month1, day1);
        
        
        //long daysBetween = ChronoUnit.DAYS.between(date1, date2);
        
        HashMap<String,Object> map = new HashMap<String,Object>();
        
        
        String[] labels = {date1.plusDays(0).toString(),date1.plusDays(1).toString(),date1.plusDays(2).toString()
        					,date1.plusDays(3).toString(),date1.plusDays(4).toString(),date1.plusDays(5).toString(),date1.plusDays(6).toString()};
        map.put("labels", labels);
        
        int[] datas1 = new int[7];
        int[] datas2 = new int[7];
        
        
        ArrayList<Matching> mat = aService.selectMatchings();
        
        
     // 로그 파일 : 페이지 이용량을 조회 (시작)
		File usageFolder = new File("C:/logs/matching/");
		File[] usageFileList = usageFolder.listFiles(); // 사용량이 기록된 로그 파일들 모두에게 접근
		
		//TreeMap<String, Integer> usageMap = new TreeMap<String, Integer>();
		try { 
			for(File f : usageFileList) {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String data;
				String dataMatNo;
				String dataService;
				String date;
				System.out.println(br.readLine());
				while((data=br.readLine())!=null) {
					// 24-08-17 21:25:77 [INFO] c.k.d.c.i.CheckCareInformationUsage.preHandle - test-m-p20
					
					System.out.println(data);
					date = data.substring(0,10);
					dataMatNo = data.split("//")[1];
					dataService = data.split("//")[2];
					
					if(dataMatNo != null && dataService != null) {
						
						for(int i = 0; i < labels.length ; i++) {
							
							
							if(labels[i].trim().equals(date.trim())) {
								if(dataService.trim().equals("개인간병")) {
									System.out.println("djelemfdjrksl?");
									datas1[i] += 1;
								}else {
									System.out.println("djelemfdjrksl?222");
									datas2[i] += 1;
								}
							}
						}
					}
					
				}
				
				
				br.close();
			}
			//model.addAttribute("usage", usageMap);
		} catch(Exception e) {
			e.printStackTrace();
		} 
        
		
		map.put("datas1",datas1);
        map.put("datas2",datas2);
        
        Gson gson = new Gson();
		response.setContentType("application/json; charset=UTF-8;");
		try {
			gson.toJson(map, response.getWriter());
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
		
        
        
	        
	}//페이토탈메소드끝 ( 매칭 일주일)
		
		
	//어드민 페이 토탈 통계 에이작스 (매칭 달)
	
	@GetMapping("monthMatTotal.adm")
	@ResponseBody
	public void monthMatTotal(@RequestParam("data1") String data1,@RequestParam("data2") String data2,@RequestParam("flag") int flag, HttpServletResponse response) {
		

		if(flag == 1) {
			int year1 = Integer.parseInt(data1.split(",")[0]);
			int month1 = Integer.parseInt(data2.split(",")[0]);
			
			//일수 계산하자
			YearMonth yearMonth = YearMonth.of(year1, month1);
			int days = yearMonth.lengthOfMonth();
			System.out.println("내년월수 뭐야?" + yearMonth);
	        HashMap<String,Object> map = new HashMap<String,Object>();
	        
	        
	        String[] labels = new String[days];
	        for(int i =1 ; i <=labels.length ; i++) {
	        	labels[i-1] = i+"";
	        }
	        map.put("labels", labels);
	        
	        int[] datas1 = new int[days];
	        int[] datas2 = new int[days];
	        
	        
	        
	     // 로그 파일 : 페이지 이용량을 조회 (시작)
			File usageFolder = new File("C:/logs/matching/");
			File[] usageFileList = usageFolder.listFiles(); // 사용량이 기록된 로그 파일들 모두에게 접근
			
			//TreeMap<String, Integer> usageMap = new TreeMap<String, Integer>();
			try { 
				for(File f : usageFileList) {
					BufferedReader br = new BufferedReader(new FileReader(f));
					String data;
					String dataMatNo;
					String dataService;
					String date;
					System.out.println(br.readLine());
					while((data=br.readLine())!=null) {
						// 24-08-17 21:25:77 [INFO] c.k.d.c.i.CheckCareInformationUsage.preHandle - test-m-p20
						
						System.out.println(data);
						date = data.substring(0,10);
						dataMatNo = data.split("//")[1];
						dataService = data.split("//")[2];
						
						if(dataMatNo != null && dataService != null) {
							
							for(int i = 0; i < labels.length ; i++) {
								System.out.println(date.substring(7,10));
								System.out.println("체킇ㄱ");
								if(labels[i].trim().equals(date.substring(8,10))) {
									if(dataService.trim().equals("개인간병")) {
										datas1[i] += 1;
									}else {
										datas2[i] += 1;
									}
								}
							}
						}
						
					}
					
					
					br.close();
				}
				//model.addAttribute("usage", usageMap);
			} catch(Exception e) {
				e.printStackTrace();
			} 
	        
			
			map.put("datas1",datas1);
	        map.put("datas2",datas2);
	        
	        Gson gson = new Gson();
			response.setContentType("application/json; charset=UTF-8;");
			try {
				gson.toJson(map, response.getWriter());
			} catch (JsonIOException | IOException e) {
				e.printStackTrace();
			}
		}else {	//여러 달 검색하는것
			int year1 = Integer.parseInt(data1.split(",")[0]);
			int year2 = Integer.parseInt(data1.split(",")[1]);
			int month1 = Integer.parseInt(data2.split(",")[0]);
			int month2 = Integer.parseInt(data2.split(",")[1]);
			
			
			HashMap<String,Object> map = new HashMap<String,Object>();
			int size = month2-month1+1;
			if(year1 != year2 ) {
				size += 12 * ( year2 - year1);
			}
			
			String[] labels = new String[size];
			int j = 0;
			int k = 0;
			for(int i = 1 ; i <= size ; i++) {
				String labelMonth = "0"+((month1+i-1) % 12 == 0 ? "12" : (month1+i-1) % 12);
	        	labels[j] = year1+ k + "-" +labelMonth.substring(labelMonth.length() - 2);
	        	
	        	j += 1;
	        	if(i % 12 == 0) {
	        		k +=1;
	        	}
			}
			
	        System.out.println("확인해보자" + labels.length);
	        System.out.println("확인해보자" + labels[0]);
	        map.put("labels", labels);
	        
	        int[] datas1 = new int[labels.length];
	        int[] datas2 = new int[labels.length];
	        
	        
	     // 로그 파일 : 페이지 이용량을 조회 (시작)
			File usageFolder = new File("C:/logs/matching/");
			File[] usageFileList = usageFolder.listFiles(); // 사용량이 기록된 로그 파일들 모두에게 접근
			
			//TreeMap<String, Integer> usageMap = new TreeMap<String, Integer>();
			try { 
				for(File f : usageFileList) {
					BufferedReader br = new BufferedReader(new FileReader(f));
					String data;
					String dataMatNo;
					String dataService;
					String date;
					while((data=br.readLine())!=null) {
						// 24-08-17 21:25:77 [INFO] c.k.d.c.i.CheckCareInformationUsage.preHandle - test-m-p20
						
						System.out.println(data);
						date = data.substring(0,10);
						dataMatNo = data.split("//")[1];
						dataService = data.split("//")[2];
						
						if(dataMatNo != null && dataService != null) {
							
							for(int i = 0; i < labels.length ; i++) {
								System.out.println(date.substring(0,7));
								System.out.println("체킇ㄱ");
								if(labels[i].trim().equals(date.substring(0,7))) {
									if(dataService.trim().equals("개인간병")) {
										datas1[i] += 1;
									}else {
										datas2[i] += 1;
									}
								}
							}
						}
						
					}
					
					
					br.close();
				}
				//model.addAttribute("usage", usageMap);
			} catch(Exception e) {
				e.printStackTrace();
			} 
	        
			
			map.put("datas1",datas1);
	        map.put("datas2",datas2);
	        
	        Gson gson = new Gson();
			response.setContentType("application/json; charset=UTF-8;");
			try {
				gson.toJson(map, response.getWriter());
			} catch (JsonIOException | IOException e) {
				e.printStackTrace();
			}
		}
		
		
	        
	}//페이토탈메소드끝 ( 매칭 달)
	
	
	//어드민 페이 토탈 통계 년
	
	@GetMapping("yearMatTotal.adm")
	@ResponseBody
	public void yearMatTotal(@RequestParam("data1") String data1,@RequestParam("data2") String data2,@RequestParam("flag") int flag, HttpServletResponse response) {
	
		if(flag ==1) {
			int year = Integer.parseInt(data1.split(",")[0]);
			
			HashMap<String,Object> map = new HashMap<String,Object>();
			
			String[] labels = new String[12];
			for(int i = 1 ; i <= 12 ; i++) {
				String labelMonth = "0"+i;
				labels[i-1] = year +"-"+ labelMonth.substring(labelMonth.length()-2);
			}
			map.put("labels", labels);
			
			int[] datas1 = new int[12];
	        int[] datas2 = new int[12];
	        
	     // 로그 파일 : 페이지 이용량을 조회 (시작)
	     			File usageFolder = new File("C:/logs/matching/");
	     			File[] usageFileList = usageFolder.listFiles(); // 사용량이 기록된 로그 파일들 모두에게 접근
	     			
	     			//TreeMap<String, Integer> usageMap = new TreeMap<String, Integer>();
	     			try { 
	     				for(File f : usageFileList) {
	     					BufferedReader br = new BufferedReader(new FileReader(f));
	     					String data;
	     					String dataMatNo;
	     					String dataService;
	     					String date;
	     					while((data=br.readLine())!=null) {
	     						// 24-08-17 21:25:77 [INFO] c.k.d.c.i.CheckCareInformationUsage.preHandle - test-m-p20
	     						
	     						System.out.println(data);
	     						date = data.substring(0,10);
	     						dataMatNo = data.split("//")[1];
	     						dataService = data.split("//")[2];
	     						
	     						if(dataMatNo != null && dataService != null) {
	     							
	     							for(int i = 0; i < labels.length ; i++) {
	     								System.out.println(date.substring(0,7));
	     								System.out.println("체킇ㄱ");
	     								if(labels[i].trim().equals(date.substring(0,7))) {
	     									if(dataService.trim().equals("개인간병")) {
	     										datas1[i] += 1;
	     									}else {
	     										datas2[i] += 1;
	     									}
	     								}
	     							}
	     						}
	     						
	     					}
	     					
	     					
	     					br.close();
	     				}
	     				//model.addAttribute("usage", usageMap);
	     			} catch(Exception e) {
	     				e.printStackTrace();
	     			} 
	     	        
	     			
	     			map.put("datas1",datas1);
	     	        map.put("datas2",datas2);
	     	        
	     	        Gson gson = new Gson();
	     			response.setContentType("application/json; charset=UTF-8;");
	     			try {
	     				gson.toJson(map, response.getWriter());
	     			} catch (JsonIOException | IOException e) {
	     				e.printStackTrace();
	     			}
			
		}else {
			int year1 = Integer.parseInt(data1.split(",")[0]);
			int year2 = Integer.parseInt(data1.split(",")[1]);
			
			HashMap<String,Object> map = new HashMap<String,Object>();
			
			String[] labels = new String[year2 - year1 +1];
			int j =0;
			for(int i = year1 ; i <= year2 ; i++) {
				labels[j] = year1+ j +"년" ;
				j += 1;
			}
			
			map.put("labels", labels);
								
			int[] datas1 = new int[year2 - year1 +1];
	        int[] datas2 = new int[year2 - year1 +1];
			
	     // 로그 파일 : 페이지 이용량을 조회 (시작)
 			File usageFolder = new File("C:/logs/matching/");
 			File[] usageFileList = usageFolder.listFiles(); // 사용량이 기록된 로그 파일들 모두에게 접근
 			
 			//TreeMap<String, Integer> usageMap = new TreeMap<String, Integer>();
 			try { 
 				for(File f : usageFileList) {
 					BufferedReader br = new BufferedReader(new FileReader(f));
 					String data;
 					String dataMatNo;
 					String dataService;
 					String date;
 					while((data=br.readLine())!=null) {
 						// 24-08-17 21:25:77 [INFO] c.k.d.c.i.CheckCareInformationUsage.preHandle - test-m-p20
 						
 						System.out.println(data);
 						date = data.substring(0,10);
 						dataMatNo = data.split("//")[1];
 						dataService = data.split("//")[2];
 						
 						if(dataMatNo != null && dataService != null) {
 							
 							for(int i = 0; i < labels.length ; i++) {
 								System.out.println(date.substring(0,4));
 								if(labels[i].trim().substring(0,4).equals(date.substring(0,4))) {
 									if(dataService.trim().equals("개인간병")) {
 										datas1[i] += 1;
 									}else {
 										datas2[i] += 1;
 									}
 								}
 							}
 						}
 						
 					}
 					
 					
 					br.close();
 				}
 				//model.addAttribute("usage", usageMap);
 			} catch(Exception e) {
 				e.printStackTrace();
 			} 
 	        
 			
 			map.put("datas1",datas1);
 	        map.put("datas2",datas2);
 	        
 	        Gson gson = new Gson();
 			response.setContentType("application/json; charset=UTF-8;");
 			try {
 				gson.toJson(map, response.getWriter());
 			} catch (JsonIOException | IOException e) {
 				e.printStackTrace();
 			}
	        
		}
	
	}// 년 메소드
			
			
			
	// 공지글 수정
	@PostMapping("adminUpdateBoard.adm")
	public String adminUpdateBoard(@ModelAttribute Board b, @RequestParam(value="page", defaultValue = "1") int page, RedirectAttributes ra) {
		System.out.println("**************");
		System.out.println(b);
		int result = aService.adminUpdateBoard(b);
		
		if(result > 0) {
			ra.addAttribute("bNo", b.getBoardNo());
			ra.addAttribute("page", page);
			return "redirect:selectAdminBoard.adm";
		}else {
			throw new AdminException("게시글 수정에 실패했습니다.");
		}
	}
	
	// 문의내역 조회
	@GetMapping("adminQnABoard.adm")
	public String adminQnABoard(@RequestParam(value="page", defaultValue = "1") int currentPage, Model model) {
		int listCount = aService.getAdminQnABoardListCount();
		
		PageInfo pi = Pagination2.getPageInfo(currentPage, listCount, 7, 5);
		ArrayList<Board> adminQnABoardList = aService.adminQnABoardList(pi);
		
		System.out.println(pi);
		System.out.println("%%%%%%%%%%%%%%");
		System.out.println(adminQnABoardList);
		System.out.println("%%%%%%%%%%%%%%");
		if(adminQnABoardList != null) {
			model.addAttribute("pi",pi);
			model.addAttribute("adminQnABoardList", adminQnABoardList);
		}
		
		return "adminQnABoard";
	}
	// 관리자 아이디 중복확인
	@PostMapping("checkAdminId.adm")
	@ResponseBody
	public String checkAdminId(@RequestParam("memberId") String memberId) {
		int result = aService.checkAdminId(memberId);
		return result == 0 ? "yes" : "no";
	}
	
	// 관리자 추가하기
	@PostMapping("insertMember.adm")
	@ResponseBody
	public String insertMember(@RequestParam("memberId") String memberId, @RequestParam("memberPwd") String pwd,
								@RequestParam("memberPhone") String memberPhone) {
		Member m = new Member();
		m.setMemberId(memberId);
		m.setMemberPwd(bCrypt.encode(pwd));
		m.setMemberCategory("A");
		m.setMemberPhone(memberPhone);
		m.setMemberEmail("idmyungja@naver.com");
		m.setMemberAddress("04540//서울 중구 남대문로 120//3층 D강의실");
		m.setMemberNational("내국인");
		m.setMemberName("관리자");
		m.setMemberGender("M");
		m.setMemberNickName(memberId);
		int result = aService.insertMember(m);
		
		return result == 1 ? "success" : "fail";
	}
	
	// 문의내역 답변
	@PostMapping("adminInsertAnswer.adm")
	@ResponseBody
	public String adminInsertAnswer(@RequestParam("boardNo") int boardNo, @RequestParam("answerContent") String answerContent, @ModelAttribute Reply r, HttpSession session) {
		r.setRefBoardNo(boardNo);
		r.setReplyContent(answerContent);
		int memberNo = ((Member)session.getAttribute("loginUser")).getMemberNo();
		r.setMemberNo(memberNo);
		int result = aService.adminInsertAnswer(r);

		ArrayList<Reply> replyList = aService.adminSelectReply(r.getRefBoardNo());
		
		System.out.println(replyList);
		
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
	
	
	
}//클래스 끝
