package ru.numbdev.interviewer.service.crud;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.numbdev.interviewer.jpa.entity.Role;
import ru.numbdev.interviewer.jpa.entity.UserEntity;
import ru.numbdev.interviewer.jpa.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserCrudService {

    private final UserRepository userRepository;

    public UserEntity getByLogin(String login) {
        return userRepository.findByLogin(login).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public List<UserEntity> getByRole(Role role) {
        return userRepository.findByRole(role);
    }
}
