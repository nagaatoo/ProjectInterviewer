package ru.numbdev.conference.handler;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.concurrent.CopyOnWriteArrayList;

public class VideoWebSocketHandler extends TextWebSocketHandler {
    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
//    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
//        Map<String, Object> msgMap = objectMapper.readValue(message.getPayload(), Map.class);
        for (WebSocketSession sess : sessions) {
            if (sess.isOpen() && !sess.getId().equals(session.getId())) {
                sess.sendMessage(message);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        System.out.println("handleBinaryMessage: " + message.getPayload());
        super.handleBinaryMessage(session, message);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        System.out.println("handleMessage: " + message.getPayload());
        super.handleMessage(session, message);
    }

    @Override
    protected void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        System.out.println("handlePongMessage: " + message.getPayload());
        super.handlePongMessage(session, message);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        System.out.println("handleTransportError: ");
        super.handleTransportError(session, exception);
    }

    @Override
    public boolean supportsPartialMessages() {
        return super.supportsPartialMessages();
    }
}
