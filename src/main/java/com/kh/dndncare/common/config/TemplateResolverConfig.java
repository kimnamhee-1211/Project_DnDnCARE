package com.kh.dndncare.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

@Configuration	//설정파일의 역할을 하는 클래스다. 라고 빈(객체) 등록
public class TemplateResolverConfig {
	
	
	@Bean	//내가 반환값을 빈으로(객체)로 등록하겟다 겟네
	public ClassLoaderTemplateResolver dotDoResolver() {
		ClassLoaderTemplateResolver dotDo = new ClassLoaderTemplateResolver();
		dotDo.setPrefix("templates/views/"); //앞첨자 접두사
		dotDo.setSuffix(".html");			 //뒷첨자 접미사
		dotDo.setTemplateMode(TemplateMode.HTML); //이형식으로진행할게??
		dotDo.setCharacterEncoding("UTF-8");	//우리한글
		dotDo.setCacheable(false);				//캐시저장풀어서 서버껐다켯다x
		dotDo.setOrder(1);						//시작점만정해주는거라고한다.(me,do,at이런것중 뭐먼저정할래? 같은) 없어도됨
		dotDo.setCheckExistence(true);			//여러개에대한 설정을 만들면
												//그걸 다 읽을 수있게 설정한것(필수네?)
												//Resolver가 여러개 작동할수잇도록!
		
		return dotDo;
	}
	
	
	//아직 멤버 me는 하지 않았습니다
//	@Bean
//	public ClassLoaderTemplateResolver dotMeResolver() {
//		ClassLoaderTemplateResolver dotMe = new ClassLoaderTemplateResolver();
//		dotMe.setPrefix("templates/views/member/");
//		dotMe.setSuffix(".html");
//		dotMe.setTemplateMode(TemplateMode.HTML);
//		dotMe.setCharacterEncoding("UTF-8");
//		dotMe.setCacheable(false);
//		dotMe.setOrder(2);
//		dotMe.setCheckExistence(true);
//		
//		
//		return dotMe;
//		
//	}
	
}
