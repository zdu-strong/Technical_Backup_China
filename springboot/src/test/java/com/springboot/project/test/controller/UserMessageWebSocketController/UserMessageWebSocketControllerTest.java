package com.springboot.project.test.controller.UserMessageWebSocketController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import jakarta.websocket.CloseReason.CloseCodes;
import org.apache.http.client.utils.URIBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.model.UserMessageWebSocketSendModel;
import com.springboot.project.model.UserModel;
import com.springboot.project.test.BaseTest;
import io.reactivex.rxjava3.subjects.ReplaySubject;

public class UserMessageWebSocketControllerTest extends BaseTest {

    private String webSocketServer;
    private String accessToken;
    private UserModel user;

    @Test
    public void test() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException,
            JsonProcessingException {
        URI url = new URIBuilder(webSocketServer).setPath("/message").setParameter("accessToken", accessToken)
                .build();
        ReplaySubject<UserMessageWebSocketSendModel> subject = ReplaySubject.create();
        WebSocketClient webSocketClient = new WebSocketClient(url) {

            @Override
            public void onOpen(ServerHandshake handshakeData) {

            }

            @Override
            public void onMessage(String message) {
                try {
                    subject.onNext(objectMapper.readValue(message, UserMessageWebSocketSendModel.class));
                } catch (JsonMappingException e) {
                    throw new RuntimeException(e.getMessage(), e);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (code == CloseCodes.NORMAL_CLOSURE.getCode()) {
                    subject.onComplete();
                } else {
                    subject.onError(new RuntimeException(reason));
                }
            }

            @Override
            public void onError(Exception ex) {
                subject.onError(ex);
            }
        };
        webSocketClient.connectBlocking();
        var result = subject.take(1).toList().toFuture().get(10, TimeUnit.SECONDS);
        webSocketClient.closeBlocking();
        assertEquals(1, result.size());
        assertTrue(JinqStream.from(result).selectAllList(s -> s.getList()).count() > 0);
        assertTrue(JinqStream.from(result).select(s -> s.getTotalPage()).findFirst().get() > 0);
        assertEquals("Hello, World!", JinqStream.from(result).selectAllList(s -> s.getList())
                .where(s -> s.getUser().getId().equals(this.user.getId())).select(s -> s.getContent())
                .limit(1)
                .getOnlyValue());
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        this.webSocketServer = new URIBuilder("ws" + this.testRestTemplate.getRootUri().substring(4)).build()
                .toString();
        this.user = this.createAccount("zdu.strong@gmail.com");
        this.accessToken = this.user.getAccess_token();
        var userMessage = new UserMessageModel().setUser(this.user).setContent("Hello, World!");
        this.userMessageService.sendMessage(userMessage);
    }
}
