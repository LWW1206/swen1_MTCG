package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.repository.UserRepository;

public class SessionService {

    private final UserRepository userRepository;

    public SessionService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean loginUser(String username, String password) {
        return userRepository.loginUser(username, password);
    }
}
