package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.apps.mtcg.service.SessionService;
import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.apps.mtcg.template.User;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class SessionController implements Controller {
    private final SessionService sessionService;

    public SessionController() {
        this.sessionService = new SessionService(new UserRepository());
    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/sessions");
    }

    @Override
    public Response handle(Request request) {

        if (!request.getMethod().equals("POST")) {
            return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "route package only takes post requests");
        }
        return loginUser(request);
    }

    private Response loginUser(Request request) {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = null;

        try {
            String requestBody = request.getBody();
            user = objectMapper.readValue(requestBody, User.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (user == null) {
            return ResponseHelper.generateResponse(HttpStatus.BAD_REQUEST, "Invalid user data");
        }
        boolean loggedIn = sessionService.loginUser(user.getUsername(), user.getPassword());
        if (loggedIn) {
            return ResponseHelper.generateResponse(HttpStatus.OK, "User logged in successfully");
        }

        return ResponseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
}


