package com.forum.dto;

import java.time.LocalDateTime;

import com.forum.model.Board;

/**
 */
public class BoardDto {
	private int id;
	private String name, description;
	private UserDto user;
	private LocalDateTime createdAt;

	public BoardDto(Board board) {
		id = board.getId();
		name = board.getName();
		description = board.getDescription();
		user = new UserDto(board.getModerator());
		createdAt = board.getCreatedAt();
	}

	public BoardDto(int id, String name, String description, UserDto user, LocalDateTime createdAt) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.user = user;
		this.createdAt = createdAt;
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public UserDto getUser() {
		return user;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}
}
