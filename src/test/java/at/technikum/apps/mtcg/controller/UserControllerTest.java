package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.controller.UserController;
import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    @Mock
    private UserRepository mockUserRepo;

    private UserController userController;
    private Request mockRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController();
        mockRequest = Mockito.mock(Request.class);
    }

    @Test
    public void testHandleGetUserData() {
        when(mockRequest.getRoute()).thenReturn("/users");
        when(mockRequest.getMethod()).thenReturn("GET");

        Response response = userController.handle(mockRequest);
        assertEquals("got User Data", response.getBody());
    }

    @Test
    public void testHandleUpdateUser() {
        when(mockRequest.getRoute()).thenReturn("/users");
        when(mockRequest.getMethod()).thenReturn("PUT");

        Response response = userController.handle(mockRequest);
        assertEquals("User successfully updated", response.getBody());
    }

    @Test
    public void testHandleRegisterUser() {
        when(mockUserRepo.createUser(anyString(), anyString())).thenReturn(true);
        when(mockRequest.getRoute()).thenReturn("/users");
        when(mockRequest.getMethod()).thenReturn("POST");

        String requestBody = "{\"Username\":\"kienboec\", \"Password\":\"daniel\"}";
        when(mockRequest.getBody()).thenReturn(requestBody);

        Response response = userController.handle(mockRequest);
        assertEquals("User created", response.getBody());
    }

    @Test
    public void testHandleRegisterUserWrongFormat() {
        when(mockUserRepo.createUser(anyString(), anyString())).thenReturn(true);
        when(mockRequest.getRoute()).thenReturn("/users");
        when(mockRequest.getMethod()).thenReturn("POST");

        String requestBody = "{\"username\":\"kienboec\", \"Password\":\"daniel\"}";
        when(mockRequest.getBody()).thenReturn(requestBody);

        Response response = userController.handle(mockRequest);
        assertEquals("Invalid user data", response.getBody());
    }
}
