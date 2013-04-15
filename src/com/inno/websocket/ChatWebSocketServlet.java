package com.inno.websocket;

import java.io.IOException;
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

public class ChatWebSocketServlet extends WebSocketServlet {
	private static final long serialVersionUID = 1L;
	private static final String GUEST_PREFIX = "Guest";
	
	private final AtomicInteger connectionIds = new AtomicInteger(0);
	
	private final Set<ChatMessageInbound> connections = new CopyOnWriteArraySet<ChatMessageInbound>();

	@Override
	protected StreamInbound createWebSocketInbound(String arg0,
			HttpServletRequest arg1) {
		return new ChatMessageInbound(connectionIds.incrementAndGet());
	}

	private final class ChatMessageInbound extends MessageInbound {
		private final String nickname;

		private ChatMessageInbound(int id) {
			this.nickname = GUEST_PREFIX + id;
		}

		@Override
		protected void onClose(int status) {
			connections.remove(this);
			String message = String.format("* %s %s", nickname, "has disconnected.");
			broadcast(message);
		}



		@Override
		protected void onOpen(WsOutbound outbound) {
			connections.add(this);
			String message = String.format("* %s %s", nickname, "has joined.");
			broadcast(message);
		}

		@Override
		protected void onBinaryMessage(ByteBuffer arg0) throws IOException {
			throw new UnsupportedOperationException("Binary message not supported.");
		}

		@Override
		protected void onTextMessage(CharBuffer message) throws IOException {
			String[] strs = message.toString().split("&-&");
			String msgType = strs[0].split(":")[1];
			
			String filteredMessage = null;
			if (msgType.equals("txt")) {
				filteredMessage = HTMLFilter.filter(strs[1].split(":")[1]);
			} else {
				filteredMessage = strs[1].substring(4);
			}
			
			
			String broadcastMsg = strs[0] + ", msg:'" + filteredMessage + "', from:'" + nickname + "'";
			broadcast(broadcastMsg);
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
