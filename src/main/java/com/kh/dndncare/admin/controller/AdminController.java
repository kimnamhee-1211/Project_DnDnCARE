package com.kh.dndncare.admin.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.kh.dndncare.admin.model.service.AdminService;
import com.kh.dndncare.admin.model.vo.Attachment;
import com.kh.dndncare.board.model.vo.Board;
import com.kh.dndncare.common.ImageUtil;
import com.kh.dndncare.common.ThumbnailUtil;
import com.kh.dndncare.member.model.Exception.MemberException;
import com.kh.dndncare.member.model.vo.Member;

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
	public String careInformation() {

		return "careInformation";
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
	
	
	
	
	
}
