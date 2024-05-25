package ru.numbdev.cacheinterviewer.service;

import java.util.Map;
import java.util.UUID;

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
import ru.numbDev.common.enums.ElementType;
import ru.numbDev.common.utils.ElementUtils;

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
            case FINISH_INTERVIEW -> finishInterview(interviewId, message);
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
                    var rows = ElementUtils.buildRowsElement(e.getValue().value());
                    saveResult(rows, message.diffs(), e.getValue().type());
                    var newValue = buildValue(rows);
                    map.put(e.getKey(), e.getValue().copyWithNewValue(newValue));
                    sendMessage(interviewId, message);
                });
    }

    private String buildValue(Map<Integer, String> rows) {
        return String.join(ValueConstants.SPLIT, rows.values());
    }

    private void saveResult(Map<Integer, String> rows, Map<Integer, String> diff, ElementType type) {
        switch (type) {
            case QUESTION -> saveQuestionResult(rows, diff);
            case CODE -> saveCodeResult(rows, diff);
        }
    }

    private void saveQuestionResult(Map<Integer, String> rows, Map<Integer, String> diff) {
        var result = ElementUtils.parseValueFromRadioButton(diff.get(1), rows);
        rows.put(1, result);
    }

    private void saveCodeResult(Map<Integer, String> rows, Map<Integer, String> diff) {
        diff.forEach((rowIdx, value) -> {
            if (ValueConstants.NULL_ROW_TAG.equals(value)) {
                rows.remove(rowIdx);
            } else {
                rows.put(rowIdx, value);
            }
        });
    }

    private void finishInterview(UUID interviewId, Message message) {
        hazelcastInstance.getMap(interviewId.toString()).destroy();
        sendMessage(interviewId, message);
    }
}
