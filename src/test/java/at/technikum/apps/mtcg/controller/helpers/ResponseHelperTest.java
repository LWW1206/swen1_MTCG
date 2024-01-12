package at.technikum.apps.mtcg.controller.helpers;

import at.technikum.apps.mtcg.controller.helpers.ResponseHelper;
import at.technikum.server.http.HttpStatus;
import at.technikum.server.http.Response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ResponseHelperTest {
    @Test
    void testGenerateResponseWithMock() {
        // Create a mocked Response
        Response mockedResponse = mock(Response.class);

        // Define expected values
        HttpStatus expectedStatus = HttpStatus.OK;
        String expectedBody = "Test Body";

        // Mock the behavior of the getStatus and getBody methods
        when(mockedResponse.getStatusCode()).thenReturn(expectedStatus.getCode());
        when(mockedResponse.getStatusMessage()).thenReturn(expectedStatus.getMessage());
        when(mockedResponse.getBody()).thenReturn(expectedBody);

        // Call the generateResponse method with the mocked Response
        Response response = ResponseHelper.generateResponse(expectedStatus, expectedBody);

        // Verify that the generated response has the expected status and body
        assertEquals(expectedStatus.getCode(), response.getStatusCode());
        assertEquals(expectedStatus.getMessage(), response.getStatusMessage());
        assertEquals(expectedBody, response.getBody());
    }
}
