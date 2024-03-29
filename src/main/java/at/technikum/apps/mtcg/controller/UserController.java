package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.controller.helpers.AuthorizationHelper;
import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.apps.mtcg.service.UserService;
import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.apps.mtcg.entity.UserData;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.apps.mtcg.entity.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Objects;

public class UserController implements Controller {

    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/users");
    }

    @Override
    public Response handle(Request request) {
        if (request.getRoute().startsWith("/users")) {
            switch (request.getMethod()) {
                case "GET" -> {
                    return getUserData(request);
                }
                case "POST" -> {
                    return registerUser(request);
                }
                case "PUT" -> {
                    return updateUser(request);
                }
            }
        }

        return ResponseHelper.generateResponse(HttpStatus.OK, "in user controller");
    }

    Response getUserData(Request request) {

        String username = AuthorizationHelper.getNameFromRoute(request);

        if(!AuthorizationHelper.isAdmin(request) && !Objects.equals(request.getToken(request), username + "-mtcgToken"))
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "not admin/ not user");
        else if(!userService.tokenExists(request))
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "user not logged in");

        try {
            UserData retrivedUserData = userService.getUserData(username);
            String userJson = objectMapper.writeValueAsString(retrivedUserData);
            return ResponseHelper.generateResponse(HttpStatus.OK, userJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    Response updateUser(Request request) {

        String username = AuthorizationHelper.getNameFromRoute(request);

        if(!AuthorizationHelper.isAdmin(request) && !Objects.equals(request.getToken(request), username + "-mtcgToken"))
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "not admin/not user");
        else if(!userService.tokenExists(request))
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "user not logged in");

        UserData userData;
        try {
            userData = objectMapper.readValue(request.getBody(), UserData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        userService.updateUserData(username, userData);
        return ResponseHelper.generateResponse(HttpStatus.OK, "User successfully updated");
    }

    Response registerUser(Request request) {
        ObjectMapper objectMapper = new ObjectMapper();
        User newUser = null;

        try {
            String requestBody = request.getBody();
            newUser = objectMapper.readValue(requestBody, User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (newUser != null) {
            return newUserResponse(newUser);
        } else {
            return ResponseHelper.generateResponse(HttpStatus.BAD_REQUEST, "Invalid user data");
        }
    }

    private Response newUserResponse(User newUser) {
        try {
            boolean userCreated = userService.createUser(newUser.getUsername(), newUser.getPassword());
            if (userCreated) {
                return ResponseHelper.generateResponse(HttpStatus.CREATED, "User sucessfully created");
            } else {
                return ResponseHelper.generateResponse(HttpStatus.CONFLICT, "User with same username already registered");
            }
        } catch (RuntimeException e) {
            return ResponseHelper.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user: " + e.getMessage());
        }
    }


}
