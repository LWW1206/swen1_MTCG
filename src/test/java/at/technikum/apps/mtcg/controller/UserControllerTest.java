package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.controller.UserController;
import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import at.technikum.apps.template.user;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @Test
    public void testHandleGetUserData() {
        UserRepository mockUserRepo = Mockito.mock(UserRepository.class);
        UserController userController = new UserController(mockUserRepo);

        Request mockRequest = Mockito.mock(Request.class);
        when(mockRequest.getRoute()).thenReturn("/users");
        when(mockRequest.getMethod()).thenReturn("GET");

        Response response = userController.handle(mockRequest);
        assertEquals("got User Data", response.getBody());
    }

    @Test
    public void testHandleUpdateUser() {
        UserRepository mockUserRepo = Mockito.mock(UserRepository.class);
        UserController userController = new UserController(mockUserRepo);

        Request mockRequest = Mockito.mock(Request.class);
        when(mockRequest.getRoute()).thenReturn("/users");
        when(mockRequest.getMethod()).thenReturn("PUT");

        Response response = userController.handle(mockRequest);
        assertEquals("User successfully updated", response.getBody());
    }

    @Test
    public void testHandleRegisterUser() {
        UserRepository mockUserRepo = Mockito.mock(UserRepository.class);
        UserController userController = new UserController(mockUserRepo);

        Request mockRequest = Mockito.mock(Request.class);
        when(mockRequest.getRoute()).thenReturn("/users");
        when(mockRequest.getMethod()).thenReturn("POST");

        String requestBody = "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}";
        when(mockRequest.getBody()).thenReturn(requestBody);

        Response response = userController.handle(mockRequest);
        assertEquals("User created", response.getBody());
    }

}