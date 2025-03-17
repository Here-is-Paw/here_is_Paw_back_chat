package com.ll.hereispaw.global.webSocket;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@Slf4j
public class SseEmitters {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
//    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // 새로운 SSE 연결을 추가하고 관련 콜백을 설정하는 메서드
    public SseEmitter add(Long userId, SseEmitter emitter) {
        String connectionKey = String.valueOf(userId);
        this.emitters.put(connectionKey, emitter);

        // 클라이언트와의 연결이 완료되면 컬렉션에서 제거하는 콜백
        emitter.onCompletion(() -> {
            this.emitters.remove(connectionKey);
        });

        // 연결이 타임아웃되면 완료 처리하는 콜백
        emitter.onTimeout(() -> {
            emitter.complete();
        });

        return emitter;
    }

    // 데이터 없이 이벤트 이름만으로 알림을 보내는 간편 메서드
    public void noti(String eventName) {
        noti(eventName, Ut.mapOf()); // 빈 Map으로 알림 전송
    }

    // 모든 연결된 클라이언트들에게 이벤트를 전송하는 메서드
    public void noti(String eventName, Map<String, Object> data) {

        List<Map.Entry<String, SseEmitter>> userEmitters = emitters.entrySet().stream()
            .filter(entry -> entry.getKey().startsWith("userId"))
            .collect(Collectors.toList());

        for (Map.Entry<String, SseEmitter> entry : userEmitters) {
            try {
                entry.getValue().send(SseEmitter.event()
                    .name(eventName)
                    .data(data));
            } catch (IOException e) {
                log.debug("Error sending notification to connection: {}", entry.getKey(), e);
                emitters.remove(entry.getKey());
                entry.getValue().complete();
                log.info("Client connection closed, notification stored in repository for later delivery");
            }
        }
    }
}

