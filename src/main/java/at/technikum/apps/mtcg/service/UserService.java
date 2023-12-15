package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.repository.UserRepository;

import java.util.Optional;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean createUser(String username, String password) {
        return userRepository.createUser(username, password);
    }


}
