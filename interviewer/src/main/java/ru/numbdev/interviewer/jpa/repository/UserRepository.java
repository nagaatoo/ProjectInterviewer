package ru.numbdev.interviewer.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.numbdev.interviewer.jpa.entity.Role;
import ru.numbdev.interviewer.jpa.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByLogin(String login);
    List<UserEntity> findByRole(Role role);
}
