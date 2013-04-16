package com.lmg.websocket.model;

public final class MessageType {
	public static final int SYSTEM_USER_ONLINE_MSG = 1;		// 系统消息用户上线
	public static final int SYSTEM_USER_OFFLINE_MSG = 2;	// 系统消息用户下线
	public static final int GROUP_MSG = 3;					// 群消息
	public static final int P2P_MSG = 4;					// 点对点消息
	
	public static final String CHAR_MSG = "txt";			// 文本消息
	public static final String IMG_MSG = "draw";			// 图片消息
}
