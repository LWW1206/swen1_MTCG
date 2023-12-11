package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.apps.template.user;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class SessionController implements Controller {
    private final UserRepository userRepo;

    public SessionController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/sessions");
    }

    @Override
    public Response handle(Request request) {

        Response response = new Response();
        if (request.getRoute().equals("/sessions")) {
            switch (request.getMethod()) {
                case "POST":
                    return loginUser(request);
            }

        }
        return response;
    }

    private Response loginUser(Request request) {
        ObjectMapper objectMapper = new ObjectMapper();
        user user = null;

        try {
            String requestBody = request.getBody();
            user = objectMapper.readValue(requestBody, user.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (user != null) {
            boolean loggedIn = userRepo.loginUser(user.getUsername(), user.getPassword());
            if (loggedIn) {
                Response response = new Response();
                response.setStatus(HttpStatus.OK);
                response.setBody("User logged in successfully");
                return response;
            } else {
                Response response = new Response();
                response.setStatus(HttpStatus.UNAUTHORIZED);
                response.setBody("Invalid username or password");
                return response;
            }
        } else {
            Response response = new Response();
            response.setStatus(HttpStatus.BAD_REQUEST);
            response.setBody("Invalid user data");
            return response;
        }
    }
}
