package ru.numbdev.cacheinterviewer.service;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.numbDev.common.constant.ValueConstants;
import ru.numbDev.common.dto.ElementValues;
import ru.numbDev.common.dto.Message;

@Service
@RequiredArgsConstructor
public class DiffCommitCacheService {

    @Value("${spring.kafka.topic-write}")
    private String topicForWrite;

    private final HazelcastInstance hazelcastInstance;
    private final KafkaTemplate<UUID, Message> kafkaTemplate;

    @KafkaListener(topics = "${spring.kafka.topic-read}")
    public void listen(ConsumerRecord<UUID, Message> record) {
        var interviewId = record.key();
        var message = record.value();

        switch (message.event()) {
            case DO_DIFF -> saveDiff(interviewId, message);
            case FINISH_INTERVIEW -> finishInterview(interviewId);
            case null -> System.out.println("Null event");
            default -> sendMessage(interviewId, message);
        }
    }

    private void sendMessage(UUID interviewId, Message message) {
        kafkaTemplate.send(topicForWrite, interviewId, message);
    }

    private void saveDiff(UUID interviewId, Message message) {
        Map<Integer, ElementValues> map = hazelcastInstance.getMap(interviewId.toString());
        map
                .entrySet()
                .stream()
                .filter(e -> e.getValue().id().equals(message.value().id()))
                .findFirst()
                .ifPresent(e -> {
                    var rows = buildRows(e.getValue().value());
                    saveResult(rows, message.diffs());
                    var newValue = buildValue(rows);
                    map.put(e.getKey(), e.getValue().copyWithNewValue(newValue));
                    sendMessage(interviewId, message);
                });
    }

    private Map<Integer, String> buildRows(String value) {
        var seq = new AtomicInteger();
        return Arrays
                .stream(value.split("\n"))
                .collect(
                        Collectors.toConcurrentMap(
                                e -> seq.incrementAndGet(),
                                e -> e
                        )
                );
    }

    private String buildValue(Map<Integer, String> rows) {
        return String.join(ValueConstants.SPLIT, rows.values());
    }

    private void saveResult(Map<Integer, String> rows, Map<Integer, String> diff) {
        diff.forEach((rowIdx, value) -> {
            if (ValueConstants.NULL_ROW_TAG.equals(value)) {
                rows.remove(rowIdx);
            } else {
                rows.put(rowIdx, value);
            }
        });
    }

    private void finishInterview(UUID interviewId) {
        hazelcastInstance.getMap(interviewId.toString()).destroy();
    }
}
