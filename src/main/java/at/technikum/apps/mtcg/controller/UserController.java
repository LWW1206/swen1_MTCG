package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.apps.mtcg.service.UserService;
import at.technikum.apps.mtcg.controller.helpers.responseHelper;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.apps.mtcg.template.user;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class UserController implements Controller {

    private final UserService userService;

    public UserController() {
        this.userService = new UserService(new UserRepository());
    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/users");
    }

    @Override
    public Response handle(Request request) {
        if (request.getRoute().equals("/users")) {
            switch (request.getMethod()) {
                case "GET":
                    return getUserData(request);
                case "POST":
                    return registerUser(request);
                case "PUT":
                    return updateUser(request);
            }
        }

        return responseHelper.generateResponse(HttpStatus.OK, "in user controller");
    }

    private Response getUserData(Request request) {
        String[] path = request.getRoute().split("/");

        if(path.length < 3) {
            return responseHelper.generateResponse(HttpStatus.BAD_REQUEST, "Invalid Request");
        }

        String username = path[2];
        System.out.println(username);
        // implement communication with db

        return responseHelper.generateResponse(HttpStatus.OK, "got User Data");
    }

    private Response updateUser(Request request) {
        //  update user

        return responseHelper.generateResponse(HttpStatus.OK, "User successfully updated");
    }

    Response registerUser(Request request) {
        ObjectMapper objectMapper = new ObjectMapper();
        user newUser = null;

        try {
            String requestBody = request.getBody();
            newUser = objectMapper.readValue(requestBody, user.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (newUser != null) {
            return newUserResponse(newUser);
        } else {
            return responseHelper.generateResponse(HttpStatus.BAD_REQUEST, "Invalid user data");
        }
    }

    private Response newUserResponse(user newUser) {
        try {
            boolean userCreated = userService.createUser(newUser.getUsername(), newUser.getPassword());
            if (userCreated) {
                return responseHelper.generateResponse(HttpStatus.CREATED, "User created");
            } else {
                return responseHelper.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, "User already exists");
            }
        } catch (RuntimeException e) {
            return responseHelper.generateResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to create user: " + e.getMessage());
        }
    }


}
