package com.lmg.websocket;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.apache.catalina.websocket.WsOutbound;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.lmg.websocket.model.Message;
import com.lmg.websocket.model.MessageType;
import com.lmg.websocket.model.User;

public class ChatWebSocketServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
	private static final String GUEST_PREFIX = "Guest";
	
	private final AtomicInteger connectionIds = new AtomicInteger(0);
	
	private final Set<ChatMessageInbound> connections = new CopyOnWriteArraySet<ChatMessageInbound>();
	private final Set<User> onlineUsers = new CopyOnWriteArraySet<User>();

	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	protected StreamInbound createWebSocketInbound(String str,
			HttpServletRequest request) {
		return new ChatMessageInbound(connectionIds.incrementAndGet());
	}

	private final class ChatMessageInbound extends MessageInbound {
		private final String nickname;
		private int id;

		private ChatMessageInbound(int id) {
			this.id = id;
			this.nickname = GUEST_PREFIX + id;
		}

		@Override
		protected void onClose(int status) {
			connections.remove(this);
			User user = new User();
			user.setUserId("user" + id);
			user.setUsername(this.nickname);
			user.setHeadImg("images/head.png");
			onlineUsers.remove(user);
			
			broadcast(systemMsg(MessageType.SYSTEM_USER_OFFLINE_MSG, " has disconnected"));
		}

		@Override
		protected void onOpen(WsOutbound outbound) {
			connections.add(this);
			User user = new User();
			user.setUserId("user" + id);
			user.setUsername(this.nickname);
			user.setHeadImg("images/head.png");
			onlineUsers.add(user);
			
			broadcast(systemMsg(MessageType.SYSTEM_USER_ONLINE_MSG, " has joined"));
		}

		/**
		 * 系统消息
		 * @param msgType
		 * @return
		 */
		private String systemMsg(int msgType, String info) {
			Message msg = new Message();
			msg.setMsgType(msgType);
			msg.setSubMsgType("txt");
			msg.setMsg(this.nickname + info);
			msg.setUsers(onlineUsers);
			
			StringWriter writer = new StringWriter();
			try {
				mapper.writeValue(writer, msg);
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return writer.toString();
		}

		@Override
		protected void onBinaryMessage(ByteBuffer arg0) throws IOException {
			throw new UnsupportedOperationException("Binary message not supported.");
		}

		@Override
		protected void onTextMessage(CharBuffer message) {
			try {
				Message msg = mapper.readValue(message.toString(), Message.class);
				msg.setFromUserId(this.nickname);

				if (MessageType.CHAR_MSG.equals(msg.getSubMsgType())) {
					msg.setMsg(HTMLFilter.filter(msg.getMsg()));
				}
				
				StringWriter writer = new StringWriter();
				mapper.writeValue(writer , msg);
				broadcast(writer.toString());
			} catch (JsonGenerationException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void broadcast(String message) {
			for (ChatMessageInbound connection : connections) {
				try {
					CharBuffer buffer = CharBuffer.wrap(message);
					connection.getWsOutbound().writeTextMessage(buffer);
				} catch (IOException ignore) {
					ignore.printStackTrace();
				}
			}
		}
	}
}
