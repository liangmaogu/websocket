package com.lmg.websocket.model;

public final class MessageType {
	public static final int SYSTEM_USER_ONLINE_MSG = 1;		// ϵͳ��Ϣ�û�����
	public static final int SYSTEM_USER_OFFLINE_MSG = 2;	// ϵͳ��Ϣ�û�����
	public static final int GROUP_MSG = 3;					// Ⱥ��Ϣ
	public static final int P2P_MSG = 4;					// ��Ե���Ϣ
	
	public static final String CHAR_MSG = "txt";			// �ı���Ϣ
	public static final String IMG_MSG = "draw";			// ͼƬ��Ϣ
}
