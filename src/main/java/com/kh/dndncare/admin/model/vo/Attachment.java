package com.kh.dndncare.admin.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Setter
@Getter
@ToString
public class Attachment {
	private int attmNo; //ATTM_NO
	private int refBoardNo; //REF_BOARD_NO
	private String originalName; //ORIGINAL_NAME
	private String renameName; //RENAME_NAME
	private String attmPath; //ATTM_PATH
	private int attmLevel; //ATTM_LEVEL
	private int attmStatus; //ATTM_STATUS
}
