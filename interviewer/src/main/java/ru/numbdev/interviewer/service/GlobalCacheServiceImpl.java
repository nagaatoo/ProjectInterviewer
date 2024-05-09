package ru.numbdev.interviewer.service;

import com.hazelcast.core.HazelcastInstance;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinSession;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.numbdev.interviewer.component.RoomObserver;
import ru.numbdev.interviewer.dto.ElementValues;
import ru.numbdev.interviewer.dto.Message;
import ru.numbdev.interviewer.enums.EventType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class GlobalCacheServiceImpl implements GlobalCacheService {

    private static final Map<UUID, Map<VaadinSession, RoomObserver>> sessions = new ConcurrentHashMap<>();

    private static final Lock offerInterviewLock = new ReentrantLock();
    private static final Lock offerDiffLock = new ReentrantLock();

    @Value("${spring.kafka.topic}")
    private String topic;

    private final KafkaTemplate<UUID, Message> kafkaTemplate;
    private final HazelcastInstance hazelcastInstance;

    @Override
    public Map<Integer, ElementValues> offerInterview(UUID interviewId, RoomObserver room) {
        try {
            offerInterviewLock.lock();
            // Видимо, срабатывает для фоновых push - не регистрируем такие комнаты
            if (UI.getCurrent() == null) {
                return Map.copyOf(hazelcastInstance.getMap(interviewId.toString()));
            }

            if (!sessions.containsKey(interviewId)) {
                sessions.put(interviewId, new ConcurrentHashMap<>());
            }
            var activeSessions = sessions.get(interviewId);
            activeSessions.put(UI.getCurrent().getSession(), room);

            return Map.copyOf(hazelcastInstance.getMap(interviewId.toString()));
        } finally {
            offerInterviewLock.unlock();
        }
    }

    @Override
    public void endInterview(UUID interviewId, UUID roomId) {
        var rooms = sessions.get(interviewId);
        if (rooms.isEmpty()) {
            sessions.remove(interviewId);
            return;
        }

        rooms
                .entrySet()
                .stream()
                .filter(es -> es.getValue().getIdAsUUID().equals(roomId))
                .findFirst()
                .ifPresent(es -> rooms.remove(es.getKey()));
    }

    @Override
    public void offerEvent(UUID interviewId, UUID roomId, EventType type) {
        kafkaTemplate.send(topic, interviewId, new Message(
                roomId,
                type,
                null,
                null
        ));
    }

    @Override
    public void offerComponent(UUID interviewId, UUID roomId, ElementValues value, boolean isChange) {
        var elements = hazelcastInstance.getMap(interviewId.toString());
        elements.put(isChange ? elements.size() - 1 : elements.size(), value);

        kafkaTemplate.send(topic, interviewId, new Message(
                roomId,
                isChange ? EventType.CHANGE_LAST_COMPONENT : EventType.ADD_COMPONENT,
                value,
                null
        ));
    }

    @Override
    public void offerDiff(UUID interviewId, UUID roomId, UUID elementId, Map<Integer, String> diffs) {
        try {
            offerDiffLock.lock();
            var elements = hazelcastInstance.getMap(interviewId.toString());
            var targetOptional = elements
                    .entrySet()
                    .stream()
                    .filter(es -> ((ElementValues) es.getValue()).id().equals(elementId.toString()))
                    .map(es -> (ElementValues) es.getValue())
                    .findFirst();
            targetOptional.ifPresent(elementValues -> kafkaTemplate.send(topic, interviewId, new Message(
                    roomId,
                    EventType.DO_DIFF,
                    elementValues,
                    diffs
            )));
        } finally {
            offerDiffLock.unlock();
        }
    }

    // TODO может быть лаг между инициализации страницы, сборки кеша и получением diff
    @KafkaListener(topics = "${spring.kafka.topic}")
    private void listen(ConsumerRecord<UUID, Message> record) {
        var interviewId = record.key();
        var message = record.value();

        var sessionRooms = sessions.get(interviewId);
        if (sessionRooms != null) {
            sessionRooms
                    .entrySet()
                    .stream()
                    .filter(es -> !es.getValue().getIdAsUUID().equals(message.roomId()))
                    .forEach(es -> es.getKey().access(() -> es.getValue().doAction(message)));
        }
    }

}
