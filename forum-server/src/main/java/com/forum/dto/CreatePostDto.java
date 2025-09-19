package com.forum.dto;

/**
 */
public class CreatePostDto {
	private String content, title;
	private int boardId;

	public CreatePostDto(String content, String title, int boardId) {
		this.content = content;
		this.title = title;
		this.boardId = boardId;
	}

	public int getBoardId() {
		return boardId;
	}

	public String getContent() {
		return content;
	}

	public String getTitle() {
		return title;
	}

	public void setBoardId(int boardId) {
		this.boardId = boardId;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
