package at.technikum.apps.mtcg.controller;

import at.technikum.apps.mtcg.controller.SessionController;
import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.server.http.Request;
import at.technikum.server.http.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class SessionControllerTest {

    @Mock
    private UserRepository userRepository;

    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        sessionController = new SessionController();
    }

    @Test
    void testLoginUser_ValidCredentials() throws IOException {
        String validUserJson = "{\"Username\": \"testUser\", \"Password\": \"testPassword\"}";
        when(userRepository.loginUser("testUser", "testPassword")).thenReturn(true);

        Request request = mock(Request.class);
        when(request.getRoute()).thenReturn("/sessions");
        when(request.getMethod()).thenReturn("POST");
        when(request.getBody()).thenReturn(validUserJson);

        Response response = sessionController.handle(request);

        assertEquals("User logged in successfully", response.getBody());
        verify(userRepository, times(1)).loginUser("testUser", "testPassword");
    }

    @Test
    void testLoginUser_InvalidCredentials() throws IOException {

        String invalidUserJson = "{\"Username\": \"testUser\", \"Password\": \"testPassword\"}";
        when(userRepository.loginUser("testUser", "testPassword")).thenReturn(false);

        Request request = mock(Request.class);
        when(request.getRoute()).thenReturn("/sessions");
        when(request.getMethod()).thenReturn("POST");
        when(request.getBody()).thenReturn(invalidUserJson);

        Response response = sessionController.handle(request);

        assertEquals("Invalid username or password", response.getBody());
        verify(userRepository, times(1)).loginUser("testUser", "testPassword");
    }
}
