package ru.numbdev.interviewer.service.crud;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.numbdev.interviewer.jpa.entity.RoomEntity;
import ru.numbdev.interviewer.jpa.repository.RoomRepository;

import java.text.MessageFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomCrudService {

    private final RoomRepository roomRepository;

    public RoomEntity save(RoomEntity entity) {
        return roomRepository.save(entity);
    }

    public RoomEntity getById(UUID id) {
        return roomRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException(MessageFormat.format("Комната с id {0} не найдена", id)));
    }

}
