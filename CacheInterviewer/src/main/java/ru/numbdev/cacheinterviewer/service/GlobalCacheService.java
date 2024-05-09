package ru.numbdev.cacheinterviewer.service;

import java.util.UUID;

import com.hazelcast.core.HazelcastInstance;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.numbDev.common.dto.Message;

@Service
@RequiredArgsConstructor
public class GlobalCacheService {
    private final HazelcastInstance hazelcastInstance;

    @KafkaListener(topics = "${spring.kafka.topic-write}")
    public void listen(ConsumerRecord<UUID, Message> record) {
        var interviewId = record.key();
        var message = record.value();
    }
}
