package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.apps.mtcg.service.SessionService;
import at.technikum.apps.mtcg.controller.helpers.responseHelper;
import at.technikum.apps.mtcg.template.user;
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
            boolean loggedIn = sessionService.loginUser(user.getUsername(), user.getPassword());
            if (loggedIn) {
                return responseHelper.generateResponse(HttpStatus.OK, "User logged in successfully");
            } else {
                return responseHelper.generateResponse(HttpStatus.UNAUTHORIZED, "Invalid username or password");
            }
        } else {
            return responseHelper.generateResponse(HttpStatus.BAD_REQUEST, "Invalid user data");
        }
    }
}
