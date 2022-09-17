package com.springboot.project.test.controller.UserMessageWebSocketController;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.websocket.CloseReason.CloseCodes;
import org.apache.http.client.utils.URIBuilder;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jinq.orm.stream.JinqStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.alibaba.fastjson.JSON;
import com.springboot.project.model.UserMessageModel;
import com.springboot.project.test.BaseTest;
import io.reactivex.subjects.ReplaySubject;

public class UserMessageWebSocketControllerTest extends BaseTest {

    private String webSocketServer;
    private String accessToken;

    @Test
    public void test() throws URISyntaxException, InterruptedException, ExecutionException, TimeoutException {
        URI url = new URIBuilder(webSocketServer).setPath("/message").setParameter("accessToken", accessToken)
                .build();
        ReplaySubject<List<UserMessageModel>> subject = ReplaySubject.create();
        WebSocketClient webSocketClient = new WebSocketClient(url) {

            @Override
            public void onOpen(ServerHandshake handshakeData) {

            }

            @Override
            public void onMessage(String message) {
                subject.onNext(JSON.parseArray(message, UserMessageModel.class));
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
        var result = subject.take(1).toList().toFuture().get(5, TimeUnit.SECONDS);
        webSocketClient.closeBlocking();
        assertEquals(1, result.size());
        assertTrue(JinqStream.from(result).getOnlyValue().size() > 0);
    }

    @BeforeEach
    public void beforeEach() throws URISyntaxException {
        this.webSocketServer = new URIBuilder("ws" + this.testRestTemplate.getRootUri().substring(4)).build()
                .toString();
        var tokenModel = this.createAccount("zdu.strong@gmail.com");
        this.accessToken = tokenModel.getAccess_token();
        var user = tokenModel.getUserModel();
        var userMessage = new UserMessageModel().setUser(user).setContent("Hello, World!");
        this.userMessageService.sendMessage(userMessage);
    }
}
