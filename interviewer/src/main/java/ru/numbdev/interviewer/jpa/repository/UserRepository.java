package ru.numbdev.interviewer.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.numbdev.interviewer.jpa.entity.UserEntity;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByLogin(String login);
}
