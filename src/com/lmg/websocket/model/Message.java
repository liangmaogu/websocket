package com.lmg.websocket.model;

import java.util.Set;

public class Message {
	private int msgType;
	private String subMsgType;
	private String fromUserId;
	private String toUserId;
	private String msg;
	private Set<User> users;

	public int getMsgType() {
		return msgType;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public String getSubMsgType() {
		return subMsgType;
	}

	public void setSubMsgType(String subMsgType) {
		this.subMsgType = subMsgType;
	}

	public String getFromUserId() {
		return fromUserId;
	}

	public void setFromUserId(String fromUserId) {
		this.fromUserId = fromUserId;
	}

	public String getToUserId() {
		return toUserId;
	}

	public void setToUserId(String toUserId) {
		this.toUserId = toUserId;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}
}
