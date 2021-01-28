package de.fraunhofer.isst.dataspaceconnector.services.utils;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ControllerUtilsTest {

    @Test
    void respondRejectUnauthorized_Null_UnauthorizedWithMessageResponse() {
        // ARRANGE
        final String emptyString = null;

        // ACT
        final var response =
                ControllerUtils.respondRejectUnauthorized(emptyString);

        // ASSERT
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
        assertEquals(response.getHeaders().size(), 0);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length() > 0);
    }

    @Test
    void respondRejectUnauthorized_NotNullString_UnauthorizedWithMessageResponse() {
        // ARRANGE
        final var testString = "TEST";

        // ACT
        final var response =
                ControllerUtils.respondRejectUnauthorized(testString);

        // ASSERT
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.UNAUTHORIZED);
        assertEquals(response.getHeaders().size(), 0);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length() > 0);
    }

    @Test
    void respondBrokerCommunicationFailed_Null_InternalServerErrorWithMessageResponse() {
        // ARRANGE
        final Exception nullException = null;

        // ACT
        final var response =
                ControllerUtils.respondBrokerCommunicationFailed(nullException);

        // ASSERT
        assertNotNull(response);
        assertEquals(response.getStatusCode(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(response.getHeaders().size(), 0);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length() > 0);
    }

    @Test
    void respondBrokerCommunicationFailed_NotNull_InternalServerErrorWithMessageResponse() {
        // ARRANGE
        final var exception = new Exception();

        // ACT
        final var response =
                ControllerUtils.respondBrokerCommunicationFailed(exception);

        // ASSERT
        assertNotNull(response);
        assertEquals(response.getStatusCode(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(response.getHeaders().size(), 0);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length() > 0);
    }

    @Test
    void respondUpdateError_Null_InternalServerErrorWithMessageResponse() {
        // ARRANGE
        final String emptyString = null;

        // ACT
        final var response = ControllerUtils.respondUpdateError(emptyString);

        // ASSERT
        assertNotNull(response);
        assertEquals(response.getStatusCode(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(response.getHeaders().size(), 0);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length() > 0);
    }

    @Test
    void respondUpdateError_NotNullString_InternalServerErrorWithMessageResponse() {
        // ARRANGE
        final var testString = "TEST";

        // ACT
        final var response = ControllerUtils.respondUpdateError(testString);

        // ASSERT
        assertNotNull(response);
        assertEquals(response.getStatusCode(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(response.getHeaders().size(), 0);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length() > 0);
    }

    @Test
    void respondConfigurationNotFound_NotFoundWithMessageResponse() {
        // ARRANGE
        // Nothing

        // ACT
        final var response = ControllerUtils.respondConfigurationNotFound();

        // ASSERT
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(response.getHeaders().size(), 0);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length() > 0);
    }

    @Test
    void respondResourceNotFound_NullId_NotFoundWithMessageResponse() {
        // ARRANGE
        final UUID resourceId = null;

        // ACT
        final var response =
                ControllerUtils.respondResourceNotFound(resourceId);

        // ASSERT
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(response.getHeaders().size(), 0);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length() > 0);
    }

    @Test
    void respondResourceNotFound_NotNullId_NotFoundWithMessageResponse() {
        // ARRANGE
        final var resourceId = UUID.randomUUID();

        // ACT
        final var response =
                ControllerUtils.respondResourceNotFound(resourceId);

        // ASSERT
        assertNotNull(response);
        assertEquals(response.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(response.getHeaders().size(), 0);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length() > 0);
    }

    @Test
    void respondResourceCouldNotBeLoaded_NullId_InternalServerErrorWithMessageResponse() {
        // ARRANGE
        final UUID resourceId = null;

        // ACT
        final var response =
                ControllerUtils.respondResourceCouldNotBeLoaded(resourceId);

        // ASSERT
        assertNotNull(response);
        assertEquals(response.getStatusCode(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(response.getHeaders().size(), 0);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length() > 0);
    }

    @Test
    void respondResourceCouldNotBeLoaded_NotNullId_InternalServerErrorWithMessageResponse() {
        // ARRANGE
        final var resourceId = UUID.randomUUID();

        // ACT
        final var response =
                ControllerUtils.respondResourceCouldNotBeLoaded(resourceId);

        // ASSERT
        assertNotNull(response);
        assertEquals(response.getStatusCode(),
                HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(response.getHeaders().size(), 0);
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length() > 0);
    }
}
