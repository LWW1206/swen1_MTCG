package at.technikum.apps.mtcg.controller.helpers;

import at.technikum.apps.mtcg.controller.helpers.AuthorizationHelper;
import at.technikum.server.http.Request;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorizationHelperTest {

    @Test
    void testExtractName() {
        String token = "user-mtcgToken";
        String extractedName = AuthorizationHelper.extractName(token);
        assertEquals("user", extractedName);
    }

    @Test
    void testGetNameFromToken() {
        // Mocking the Request object
        Request mockedRequest = mock(Request.class);

        // Adjusting the mocked behavior to return a token in the correct format
        when(mockedRequest.getToken(any())).thenReturn("testuser-mtcgToken");

        String nameFromToken = AuthorizationHelper.getNameFromToken(mockedRequest);
        assertEquals("testuser", nameFromToken);
    }

    @Test
    void testIsAdminWithAdminToken() {
        // Mocking the Request object
        Request mockedRequest = mock(Request.class);
        when(mockedRequest.getToken(any())).thenReturn("admin-mtcgToken");

        assertTrue(AuthorizationHelper.isAdmin(mockedRequest));
    }

    @Test
    void testIsAdminWithNonAdminToken() {
        // Mocking the Request object
        Request mockedRequest = mock(Request.class);
        when(mockedRequest.getToken(any())).thenReturn("user-mtcgToken");

        assertFalse(AuthorizationHelper.isAdmin(mockedRequest));
    }

    @Test
    void testGetNameFromRoute() {
        // Mocking the Request object
        Request mockedRequest = mock(Request.class);
        when(mockedRequest.getRoute()).thenReturn("/someroute/user");

        String nameFromRoute = AuthorizationHelper.getNameFromRoute(mockedRequest);
        assertEquals("user", nameFromRoute);
    }
}
