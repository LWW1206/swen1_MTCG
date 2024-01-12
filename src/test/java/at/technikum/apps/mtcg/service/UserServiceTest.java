package at.technikum.apps.mtcg.service;

import at.technikum.apps.mtcg.controller.helpers.AuthorizationHelper;
import at.technikum.apps.mtcg.entity.UserData;
import at.technikum.apps.mtcg.repository.UserRepository;
import at.technikum.server.http.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepositoryMock;

    @Mock
    private Request requestMock;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser() {
        // Arrange
        when(userRepositoryMock.createUser("testUser", "testPassword")).thenReturn(true);

        // Act
        boolean result = userService.createUser("testUser", "testPassword");

        // Assert
        assertTrue(result);
        verify(userRepositoryMock, times(1)).createUser("testUser", "testPassword");
    }

    @Test
    public void testTokenExistsWithInvalidToken() {
        // Arrange
        when(requestMock.getToken(requestMock)).thenReturn(null);

        // Act
        boolean result = userService.tokenExists(requestMock);

        // Assert
        assertFalse(result);
        verify(userRepositoryMock, never()).checkToken(anyString());
    }

    @Test
    public void testEnoughCoins() {
        // Arrange
        when(userRepositoryMock.atleastFiveCoins("testUser")).thenReturn(true);

        // Act
        boolean result = userService.enoughCoins("testUser");

        // Assert
        assertTrue(result);
        verify(userRepositoryMock, times(1)).atleastFiveCoins("testUser");
    }

    @Test
    public void testDeductFiveCoins() {
        // Arrange
        when(userRepositoryMock.minusFiveCoins("testUser")).thenReturn(true);

        // Act
        boolean result = userService.deductFiveCoins("testUser");

        // Assert
        assertTrue(result);
        verify(userRepositoryMock, times(1)).minusFiveCoins("testUser");
    }

    @Test
    public void testGetUserData() {
        // Arrange
        UserData userData = new UserData(/* Set relevant user data */);
        when(userRepositoryMock.getUserData("testUser")).thenReturn(userData);

        // Act
        UserData result = userService.getUserData("testUser");

        // Assert
        assertNotNull(result);
        assertEquals(userData, result);
        verify(userRepositoryMock, times(1)).getUserData("testUser");
    }

    @Test
    public void testUpdateUserData() {
        // Arrange
        UserData userData = new UserData();

        // Act
        userService.updateUserData("testUser", userData);

        // Assert
        verify(userRepositoryMock, times(1)).updateUserData("testUser", userData);
    }
}
