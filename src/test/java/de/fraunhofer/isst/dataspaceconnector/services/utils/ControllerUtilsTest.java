package de.fraunhofer.isst.dataspaceconnector.services.utils;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;

class ControllerUtilsTest {

    @Test
    void respondRejectUnauthorized_Null_UnauthorizedWithMessageResponse() {
        // ARRANGE
        final String emptyString = null;

        // ACT
        final var result = ControllerUtils.respondRejectUnauthorized(emptyString);

        // ASSERT
        assertNotNull(result);
        assertEquals(result.getStatusCode(), HttpStatus.UNAUTHORIZED);
        assertEquals(result.getHeaders().size(), 0);
        assertNotNull(result.getBody());
        assertTrue(result.getBody().length() > 0);
    }

    @Test
    void respondRejectUnauthorized_NotNullString_UnauthorizedWithMessageResponse() {
        // ARRANGE
        final String emptyString = "TEST";

        // ACT
        final var result = ControllerUtils.respondRejectUnauthorized(emptyString);

        // ASSERT
        assertNotNull(result);
        assertEquals(result.getStatusCode(), HttpStatus.UNAUTHORIZED);
        assertEquals(result.getHeaders().size(), 0);
        assertNotNull(result.getBody());
        assertTrue(result.getBody().length() > 0);
    }

    @Test
    void respondBrokerCommunicationFailed() {
    }
}
