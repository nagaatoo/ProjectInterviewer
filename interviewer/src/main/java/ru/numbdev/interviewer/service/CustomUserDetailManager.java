package ru.numbdev.interviewer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Component;
import ru.numbdev.interviewer.jpa.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class CustomUserDetailManager implements UserDetailsManager {

    private final UserRepository userRepository;

    @Override
    public void createUser(UserDetails user) {

    }

    @Override
    public void updateUser(UserDetails user) {

    }

    @Override
    public void deleteUser(String username) {

    }

    @Override
    public void changePassword(String oldPassword, String newPassword) {

    }

    @Override
    public boolean userExists(String username) {
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByLogin(username)
                .stream()
                .map(u -> User
                        .builder()
                        .username(u.getLogin())
                        .password(u.getPass())
                        .roles(u.getRole() != null ? u.getRole().name() : null)
                        .build()
                )
                .findAny()
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
