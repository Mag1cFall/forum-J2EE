package com.forum.dto;

/**
 */
public class CreateBoardDto {
	private String name, desc;
	private int modId;

	public CreateBoardDto(String name, String desc, int modId) {
		this.name = name;
		this.desc = desc;
		this.modId = modId;
	}

	public String getName() {
		return name;
	}

	public String getDesc() {
		return desc;
	}

	public int getModId() {
		return modId;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public void setModId(int modId) {
		this.modId = modId;
	}

	public void setName(String name) {
		this.name = name;
	}
}
