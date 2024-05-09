package ru.numbdev.interviewer.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.numbdev.interviewer.jpa.entity.RoomEntity;

import java.util.UUID;

public interface RoomRepository extends JpaRepository<RoomEntity, UUID> {
}
