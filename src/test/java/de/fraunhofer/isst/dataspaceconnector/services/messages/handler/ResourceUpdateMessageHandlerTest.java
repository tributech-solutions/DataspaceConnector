package de.fraunhofer.isst.dataspaceconnector.services.messages.handler;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.GregorianCalendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.ArtifactBuilder;
import de.fraunhofer.iais.eis.DynamicAttributeTokenBuilder;
import de.fraunhofer.iais.eis.MessageProcessedNotificationMessage;
import de.fraunhofer.iais.eis.RejectionReason;
import de.fraunhofer.iais.eis.RepresentationBuilder;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.ResourceUpdateMessageBuilder;
import de.fraunhofer.iais.eis.ResourceUpdateMessageImpl;
import de.fraunhofer.iais.eis.TokenFormat;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.exceptions.ResourceNotFoundException;
import de.fraunhofer.isst.dataspaceconnector.services.EntityUpdateService;
import de.fraunhofer.isst.ids.framework.messaging.model.messages.MessagePayloadImpl;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.model.responses.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ResourceUpdateMessageHandlerTest {

    @SpyBean
    EntityUpdateService updateService;

    @Autowired
    ResourceUpdateMessageHandler handler;

    @Test
    public void handleMessage_nullMessage_returnBadRequest() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage(null, null);

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_nullMessage_returnVersionNotSupported() throws DatatypeConfigurationException {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new ResourceUpdateMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("tetris")
                ._issued_(xmlCalendar)
                ._affectedResource_(URI.create("https://localhost:8080/someResource"))
                .build();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage((ResourceUpdateMessageImpl) message, null);

        /* ASSERT */
        assertEquals(RejectionReason.VERSION_NOT_SUPPORTED, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_missingAffectedResource_returnBadRequestResponseMessage()
            throws DatatypeConfigurationException {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new ResourceUpdateMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                ._affectedResource_(URI.create(""))
                .build();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage((ResourceUpdateMessageImpl) message, null);

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_nullPayload_returnBadRequestResponseMessage()
            throws DatatypeConfigurationException {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new ResourceUpdateMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                ._affectedResource_(URI.create("https://localhost:8080/artifacts/someId"))
                .build();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage((ResourceUpdateMessageImpl) message, null);

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_illPayload_returnBadRequestResponseMessage()
            throws DatatypeConfigurationException {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new ResourceUpdateMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                ._affectedResource_(URI.create("https://localhost:8080/artifacts/someId"))
                .build();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage((ResourceUpdateMessageImpl) message, new MessagePayloadImpl(null, new ObjectMapper()));

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_emptyPayload_returnBadRequestResponseMessage()
            throws DatatypeConfigurationException {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new ResourceUpdateMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                ._affectedResource_(URI.create("https://localhost:8080/artifacts/someId"))
                .build();

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage((ResourceUpdateMessageImpl) message, new MessagePayloadImpl(
                InputStream.nullInputStream(), new ObjectMapper()));

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_notIdsInPayload_returnInternalRecipientErrorResponseError()
            throws DatatypeConfigurationException {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new ResourceUpdateMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                ._affectedResource_(URI.create("https://localhost:8080/artifacts/someId"))
                .build();

        final var invalidInput = "some stuff inside here";
        final InputStream stream = new ByteArrayInputStream(invalidInput.getBytes(StandardCharsets.UTF_8));

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage((ResourceUpdateMessageImpl) message,
                                                                 new MessagePayloadImpl(stream, new ObjectMapper()));

        /* ASSERT */
        assertEquals(RejectionReason.INTERNAL_RECIPIENT_ERROR, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_affectedResourceNotInPayload_returnBadRequestErrorResponseError()
            throws DatatypeConfigurationException, IOException {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new ResourceUpdateMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                ._affectedResource_(URI.create("https://localhost:8080/artifacts/someId"))
                .build();

        final var validInput = new Serializer().serialize(new ResourceBuilder(URI.create("https://localhost:8080/artifacts/someOtherId"))
                                                                    .build());
        final InputStream stream = new ByteArrayInputStream(validInput.getBytes(StandardCharsets.UTF_8));

        /* ACT */
        final var result = (ErrorResponse) handler.handleMessage((ResourceUpdateMessageImpl) message,
                                                                 new MessagePayloadImpl(stream, new ObjectMapper()));

        /* ASSERT */
        assertEquals(RejectionReason.BAD_PARAMETERS, result.getRejectionMessage().getRejectionReason());
    }

    @Test
    public void handleMessage_failToUpdateResource_returnMessageProcessNotification()
            throws DatatypeConfigurationException, IOException {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new ResourceUpdateMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                ._affectedResource_(URI.create("https://localhost:8080/artifacts/someId"))
                .build();

        final var validInput = new Serializer().serialize(new ResourceBuilder(URI.create("https://localhost:8080/artifacts/someId"))
                                                                    .build());
        final InputStream stream = new ByteArrayInputStream(validInput.getBytes(StandardCharsets.UTF_8));

        Mockito.doThrow(ResourceNotFoundException.class).when(updateService).updateResource(Mockito.any());;

        /* ACT */
        final var result = (BodyResponse) handler.handleMessage((ResourceUpdateMessageImpl) message,
                                                                new MessagePayloadImpl(stream, new ObjectMapper()));

        /* ASSERT */
        assertTrue(result.getHeader() instanceof MessageProcessedNotificationMessage);
    }


    @Test
    public void handleMessage_validUpdate_returnMessageProcessNotification()
            throws DatatypeConfigurationException, IOException {
        /* ARRANGE */
        final var calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        final var xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);

        final var message = new ResourceUpdateMessageBuilder()
                ._senderAgent_(URI.create("https://localhost:8080"))
                ._issuerConnector_(URI.create("https://localhost:8080"))
                ._securityToken_(new DynamicAttributeTokenBuilder()._tokenFormat_(TokenFormat.OTHER)._tokenValue_("").build())
                ._modelVersion_("4.0.0")
                ._issued_(xmlCalendar)
                ._affectedResource_(URI.create("https://localhost:8080/resources/someId"))
                .build();

        final var artifact =new ArtifactBuilder(URI.create("https://localhost:8080/artifacts/someId")).build();
        final var representation = new RepresentationBuilder(URI.create("https://localhost:8080/representations/someId"))
                ._instance_(Util.asList(artifact))
                .build();
        final var resource = new ResourceBuilder(URI.create("https://localhost:8080/resources/someId"))
                ._representation_(Util.asList(representation)).build();

        final var validInput = new Serializer().serialize(resource);
        final InputStream stream = new ByteArrayInputStream(validInput.getBytes(StandardCharsets.UTF_8));


        /* ACT */
        final var result = (BodyResponse) handler.handleMessage((ResourceUpdateMessageImpl) message,
                                                                new MessagePayloadImpl(stream, new ObjectMapper()));

        /* ASSERT */
        Mockito.verify(updateService).updateResource(Mockito.argThat(x -> x.getId().equals(resource.getId())));
        Mockito.verify(updateService).updateRepresentation(Mockito.argThat(x -> x.getId().equals(representation.getId())));
        Mockito.verify(updateService).updateArtifact(Mockito.argThat(x -> x.getId().equals(artifact.getId())));
        assertTrue(result.getHeader() instanceof MessageProcessedNotificationMessage);
    }
}
