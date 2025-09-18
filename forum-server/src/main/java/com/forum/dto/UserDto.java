package com.forum.dto;

import java.time.LocalDateTime;

import com.forum.model.User;

/**
 */
public class UserDto {

    private int uid;
    private String username, status, role;
    private LocalDateTime createdAt;

    public UserDto(User user) {
        uid = user.getId();
        username = user.getUsername();
        status = user.getStatus().toString();
        role = user.getRole().toString();
        createdAt = user.getCreatedAt();
    }

    public UserDto(int uid, String username, String status, String role, LocalDateTime createdAt) {
        this.uid = uid;
        this.username = username;
        this.status = status;
        this.role = role;
        this.createdAt = createdAt;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public int getUid() {
        return uid;
    }

    public String getStatus() {
        return status;
    }

    public String getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
