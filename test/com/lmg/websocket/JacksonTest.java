package com.lmg.websocket;

import java.io.IOException;
import java.io.StringWriter;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.lmg.websocket.model.Message;
import com.lmg.websocket.model.MessageType;

public class JacksonTest {
	private ObjectMapper mapper = null;
	private JsonGenerator generator = null;
	
	@Before
	public void setUp() throws IOException {
		mapper = new ObjectMapper();
		generator = mapper.getJsonFactory().createJsonGenerator(new StringWriter());
	}

	@Test
	public void testBeanToJson() throws JsonProcessingException, IOException {
		Message msg = new Message();
		msg.setMsgType(MessageType.SYSTEM_USER_OFFLINE_MSG);
		msg.setMsg("this is system msg");

		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, msg);
		String json = writer.toString();
		System.out.println(json);
	}
	
	@Test
	public void testJsonToBean() throws JsonParseException, JsonMappingException, IOException {
		String json = "{\"msg\":\"this is system msg\",\"toUserId\":null,\"fromUserId\":null,\"msgType\":1}";
		Message msg = mapper.readValue(json, Message.class);
		
		System.out.println(msg.getMsgType());
	}
	
	@After
	public void destory() {
		try {
			if (generator != null) {
				generator.flush();
			}
			if (!generator.isClosed()) {
				generator.close();
			}
			generator = null;
			mapper = null;
			System.gc();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
