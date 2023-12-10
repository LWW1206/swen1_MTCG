package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.server.http.HttpContentType;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.apps.template.user;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.net.ssl.SSLEngineResult;
import java.io.IOException;

public class UserController implements Controller {

    private final UserRepository userRepo;

    public UserController() {
        this.userRepo = new UserRepository();
    }

    @Override
    public boolean supports(String route) {
        return route.startsWith("/users");
    }

    @Override
    public Response handle(Request request) {
        System.out.println("in usercontroller");

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

        Response response = new Response();
        response.setStatus(HttpStatus.OK);
        response.setContentType(HttpContentType.TEXT_PLAIN);
        response.setBody("user controller");

        return response;
    }

    private Response getUserData(Request request) {
        String username = request.getUsername();
        String token = request.getToken();
        String path = request.getPath();

        Response response = new Response();
        response.setStatus(HttpStatus.OK);
        response.setBody("got User Data");

        return response;
    }

    private Response updateUser(Request request) {
        Response response = new Response();
        response.setStatus(HttpStatus.OK);
        response.setBody("User successfully updated");

        return response;
    }

    private Response registerUser(Request request) {

        ObjectMapper objectMapper = new ObjectMapper();
        user newUser = null;

        try {
            String requestBody = request.getBody();
            newUser = objectMapper.readValue(requestBody, user.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (newUser != null) {
            boolean userCreated = userRepo.createUser(newUser.getUsername(), newUser.getPassword());
            if (userCreated) {
                Response response = new Response();
                response.setStatus(HttpStatus.CREATED);
                response.setBody("User created");
                return response;
            } else {
                Response response = new Response();
                response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                response.setBody("Failed to create user");
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
