package com.lmg.websocket.model;

public class User {
	private String userId;
	private String username;
	private String headImg;

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getHeadImg() {
		return headImg;
	}

	public void setHeadImg(String headImg) {
		this.headImg = headImg;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			User user = (User)obj;
			if (!user.userId.equals(this.userId)) {
				return false;
			}
			if (!user.username.equals(this.username)) {
				return false;
			}
			if (!user.headImg.equals(this.headImg)) {
				return false;
			}
		} else {
			return false;
		}
		
		return true;
	}
}
