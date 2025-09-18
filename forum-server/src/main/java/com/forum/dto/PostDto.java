package com.forum.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PostDto {
	private int pid;
	private String title, content;
	private UserDto user;
	private BoardDto board;
	private LocalDateTime createdAt;
	private boolean isPinned;
	private int likes, dislikes;
	private List<CommentDto> comments;

	public PostDto(int pid, String title, String content, UserDto user, BoardDto board, LocalDateTime createdAt,
			boolean isPinned, int likes, int dislikes, List<CommentDto> comments) {
		this.pid = pid;
		this.title = title;
		this.content = content;
		this.user = user;
		this.board = board;
		this.createdAt = createdAt;
		this.isPinned = isPinned;
		this.likes = likes;
		this.dislikes = dislikes;
		this.comments = comments;
	}

	public void setBoard(BoardDto board) {
		this.board = board;
	}

	public void setComments(List<CommentDto> comments) {
		this.comments = comments;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setDislikes(int dislikes) {
		this.dislikes = dislikes;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public void setPinned(boolean isPinned) {
		this.isPinned = isPinned;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public BoardDto getBoard() {
		return board;
	}

	public List<CommentDto> getComments() {
		return comments;
	}

	public String getContent() {
		return content;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public int getDislikes() {
		return dislikes;
	}

	public int getLikes() {
		return likes;
	}

	public int getPid() {
		return pid;
	}

	public String getTitle() {
		return title;
	}

	public UserDto getUser() {
		return user;
	}

	public boolean isPinned() {
		return isPinned;
	}
}