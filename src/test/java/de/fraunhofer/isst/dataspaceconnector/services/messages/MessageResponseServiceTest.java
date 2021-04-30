package de.fraunhofer.isst.dataspaceconnector.services.messages;

import java.net.URI;

import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.isst.dataspaceconnector.exceptions.MessageEmptyException;
import de.fraunhofer.isst.dataspaceconnector.exceptions.VersionNotSupportedException;
import de.fraunhofer.isst.dataspaceconnector.services.ids.ConnectorService;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {MessageResponseService.class})
class MessageResponseServiceTest {

    @MockBean
    ConnectorService connectorService;

    @Autowired
    MessageResponseService service;

    private final URI connectorId = URI.create("https://someConnectorId");
    private final String outboundVersion = "4.0.0";

    @BeforeEach
    public void init() {
        Mockito.when(connectorService.getConnectorId()).thenReturn(connectorId);
        Mockito.when(connectorService.getOutboundModelVersion()).thenReturn(outboundVersion);
    }

    /**
     *  handleMessageEmptyException
     */

    @Test
    public void handleMessageEmptyException_null_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.handleMessageEmptyException(null));
    }

    @Test
    public void handleMessageEmptyException_validException_BadParametersResponse() {
        /* ARRANGE */
        final var exception = new MessageEmptyException("Some problem");

        /* ACT */
        final var result = service.handleMessageEmptyException(exception);

        /* ASSERT */
        assertTrue(result instanceof ErrorResponse);

        final var error = (ErrorResponse) result;
        assertEquals(RejectionReason.BAD_PARAMETERS, error.getRejectionMessage().getRejectionReason());
        assertEquals(exception.getMessage(), error.getErrorMessage());
        assertEquals(connectorId, error.getRejectionMessage().getIssuerConnector());
        assertEquals(outboundVersion, error.getRejectionMessage().getModelVersion());
    }

    /**
     *  handleInfoModelNotSupportedException
     */

    @Test
    public void handleInfoModelNotSupportedException_nullException_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.handleInfoModelNotSupportedException(null, "4.0.0"));
    }

    @Test
    public void handleInfoModelNotSupportedException_nullVersion_noException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> service.handleInfoModelNotSupportedException(new VersionNotSupportedException(""), null));
    }

    @Test
    public void handleInfoModelNotSupportedException_validInput_VersionNotSupportedResponse()
            throws IllegalAccessException, NoSuchFieldException {
        /* ARRANGE */
        final var exception = new VersionNotSupportedException("Some problem");
        final var version = "3.0.0";

        /* ACT */
        final var result = service.handleInfoModelNotSupportedException(exception, version);

        /* ASSERT */
        assertTrue(result instanceof ErrorResponse);

        final var error = (ErrorResponse) result;
        assertEquals(RejectionReason.VERSION_NOT_SUPPORTED, error.getRejectionMessage().getRejectionReason());
        assertEquals(exception.getMessage(), error.getErrorMessage());
        assertEquals(connectorId, error.getRejectionMessage().getIssuerConnector());
        assertEquals(outboundVersion, error.getRejectionMessage().getModelVersion());
    }

    /**
     *  handleResponseMessageBuilderException
     */

    @Test
    public void handleResponseMessageBuilderException_nullException_throwIllegalArgumentException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertThrows(IllegalArgumentException.class, () -> service.handleResponseMessageBuilderException(null, URI.create("https://someUri"), URI.create("https://someUri")));
    }


    @Test
    public void handleResponseMessageBuilderExceptionException_nullIssuer_noException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> service.handleResponseMessageBuilderException(new Exception(""), null, URI.create("https://someUri")));
    }

    @Test
    public void handleResponseMessageBuilderExceptionException_nullMessageId_noException() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT && ASSERT */
        assertDoesNotThrow(() -> service.handleResponseMessageBuilderException(new Exception(""), URI.create("https://someUri"), null));
    }

    @Test
    public void handleResponseMessageBuilderExceptionException_validInput_RecipientResponse() {
        /* ARRANGE */
        final var exception = new Exception("Some problem");
        final var issuer = URI.create("https://someUri");
        final var messageId = URI.create("https://someUri2");

        /* ACT */
        final var result = service.handleResponseMessageBuilderException(exception, issuer, messageId);

        /* ASSERT */
        assertTrue(result instanceof ErrorResponse);

        final var error = (ErrorResponse) result;
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR, error.getRejectionMessage().getRejectionReason());
        assertEquals("Response could not be constructed.", error.getErrorMessage());
        assertEquals(connectorId, error.getRejectionMessage().getIssuerConnector());
        assertEquals(outboundVersion, error.getRejectionMessage().getModelVersion());
    }
}
