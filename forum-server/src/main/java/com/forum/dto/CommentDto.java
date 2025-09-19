package com.forum.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.forum.model.Comment;

public class CommentDto {
	private int id;
	private String content;
	private UserDto author;
	private PostDto post;
	private LocalDateTime createdAt;
	private int likes, dislikes, parentId;
	private List<CommentDto> replies;

	public CommentDto(int id, String content, UserDto author, PostDto post, LocalDateTime createdAt, int likes,
			int dislikes, int parentId, List<CommentDto> replies) {
		this.id = id;
		this.content = content;
		this.author = author;
		this.post = post;
		this.createdAt = createdAt;
		this.likes = likes;
		this.dislikes = dislikes;
		this.parentId = parentId;
		this.replies = replies;
	}

	public CommentDto(Comment comm) {
		this(comm.getId(), comm.getContent(), new UserDto(comm.getAuthor()), new PostDto(comm.getPost()),
				comm.getCreatedAt(), comm.getLikes(), comm.getDislikes(), comm.getParentId(),
				comm.getReplies().stream().map(x -> new CommentDto(x)).toList());
	}

	public UserDto getAuthor() {
		return author;
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

	public int getId() {
		return id;
	}

	public int getLikes() {
		return likes;
	}

	public int getParentId() {
		return parentId;
	}

	public PostDto getPost() {
		return post;
	}

	public List<CommentDto> getReplies() {
		return replies;
	}

	public void setAuthor(UserDto author) {
		this.author = author;
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

	public void setId(int id) {
		this.id = id;
	}

	public void setLikes(int likes) {
		this.likes = likes;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public void setPost(PostDto post) {
		this.post = post;
	}

	public void setReplies(List<CommentDto> replies) {
		this.replies = replies;
	}
}