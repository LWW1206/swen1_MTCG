package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.controller.helpers.authorizationHelper;
import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.server.http.Request;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean createUser(String username, String password) {
        return userRepository.createUser(username, password);
    }
    public boolean tokenExists(Request request) {
        String token = request.getToken(request);
        String username = authorizationHelper.extractName(token);
        return userRepository.checkToken(username);
    }

    public boolean enoughCoins(String name) {
        return userRepository.atleastFiveCoins(name);
    }

    public boolean buyPackage(String name) {
        return userRepository.minusFiveCoins(name);
    }
}
