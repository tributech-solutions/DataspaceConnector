package de.fraunhofer.isst.dataspaceconnector.services.messages;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.ContractAgreementBuilder;
import de.fraunhofer.iais.eis.ContractRequestBuilder;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResourceDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ArtifactRequestMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ContractAgreementMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.ContractRequestMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.model.messages.DescriptionRequestMessageDesc;
import de.fraunhofer.isst.dataspaceconnector.services.EntityResolver;
import de.fraunhofer.isst.dataspaceconnector.services.ids.ConnectorService;
import de.fraunhofer.isst.dataspaceconnector.services.ids.DeserializationService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ArtifactRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ContractAgreementService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.ContractRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.messages.types.DescriptionRequestService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.ArtifactService;
import de.fraunhofer.isst.dataspaceconnector.services.resources.TemplateBuilder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = {MessageService.class})
public class MessageServiceTest {

    @MockBean
    DescriptionRequestService descService;

    @MockBean
    ContractRequestService contractRequestService;

    @MockBean
    ContractAgreementService contractAgreementService;

    @MockBean
    ArtifactRequestService artifactRequestService;

    @MockBean
    DeserializationService deserializationService;

    @MockBean
    TemplateBuilder<RequestedResource, RequestedResourceDesc> templateBuilder;

    @MockBean
    ConnectorService connectorService;

    @MockBean
    EntityResolver entityResolver;

    @MockBean
    ObjectMapper objectMapper;

    @MockBean
    ArtifactService artifactService;

    @Autowired
    MessageService service;

    private final ZonedDateTime date = ZonedDateTime.ofInstant(Instant.ofEpochMilli(1616772571804L), ZoneOffset.UTC);

    @Test
    public void sendDescriptionRequestMessage_withoutRequestedElement_returnValidResponse() {
        /* ARRANGE */
        final var recipient = URI.create("https://localhost:8080/api/ids/data");
        final var desc = new DescriptionRequestMessageDesc(recipient, null);

        final var response = new HashMap<String, String>();
        response.put("header", "some header values");
        response.put("body", "some body values");

        Mockito.when(descService.sendMessage(Mockito.eq(desc), Mockito.eq(""))).thenReturn(response);

        /* ACT */
        final var result = service.sendDescriptionRequestMessage(recipient, null);

        /* ARRANGE */
        assertEquals(response, result);
    }

    @Test
    public void sendDescriptionRequestMessage_validRequestedElement_returnValidResponse() {
        /* ARRANGE */
        final var recipient = URI.create("https://localhost:8080/api/ids/data");
        final var element = URI.create("https://requestedElement");
        final var desc = new DescriptionRequestMessageDesc(recipient, element);

        final var response = new HashMap<String, String>();
        response.put("header", "some header values");
        response.put("body", "some body values");

        Mockito.when(descService.sendMessage(Mockito.eq(desc), Mockito.eq(""))).thenReturn(response);

        /* ACT */
        final var result = service.sendDescriptionRequestMessage(recipient, element);

        /* ARRANGE */
        assertEquals(response, result);
    }

    @Test
    public void validateDescriptionResponseMessage_validResponse_returnTrue() {
        /* ARRANGE */
        final var response = new HashMap<String, String>();
        response.put("header", "some valid header");
        response.put("body", "some valid body");

        Mockito.when(descService.isValidResponseType(Mockito.eq(response))).thenReturn(true);

        /* ACT */
        final var result = service.validateDescriptionResponseMessage(response);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void validateDescriptionResponseMessage_invalidResponse_returnFalse() {
        /* ARRANGE */
        final var response = new HashMap<String, String>();
        response.put("header", "some invalid header");
        response.put("body", "some invalid body");

        Mockito.when(descService.isValidResponseType(Mockito.eq(response))).thenReturn(false);

        /* ACT */
        final var result = service.validateDescriptionResponseMessage(response);

        /* ASSERT */
        assertFalse(result);
    }

    @Test
    public void sendContractRequestMessage_withValidContractRequest_returnValidResponse() {
        /* ARRANGE */
        final var recipient = URI.create("https://localhost:8080/api/ids/data");
        final var contractId = URI.create("https://contractRequest");
        final var desc = new ContractRequestMessageDesc(recipient, contractId);
        final var request = new ContractRequestBuilder(contractId).build();
        final var requestAsRdf = request.toRdf();

        final var response = new HashMap<String, String>();
        response.put("header", "some header values");
        response.put("body", "some body values");

        Mockito.when(contractRequestService.sendMessage(Mockito.eq(desc), Mockito.eq(requestAsRdf))).thenReturn(response);

        /* ACT */
        final var result = service.sendContractRequestMessage(recipient, request);

        /* ARRANGE */
        assertEquals(response, result);
    }

    @Test
    public void sendContractRequestMessage_withoutContractRequest_throwsIllegalArgumentException() {
        /* ARRANGE */
        final var recipient = URI.create("https://localhost:8080/api/ids/data");
        final var contractId = URI.create("https://contractRequest");
        final var desc = new ContractRequestMessageDesc(recipient, contractId);

        final var response = new HashMap<String, String>();
        response.put("header", "some header values");
        response.put("body", "some body values");

        Mockito.when(contractRequestService.sendMessage(Mockito.eq(desc), Mockito.eq(null))).thenReturn(response);

        /* ACT & ARRANGE */
        assertThrows(IllegalArgumentException.class, () -> service.sendContractRequestMessage(recipient, null));
    }

    @Test
    public void validateContractRequestResponseMessage_validResponse_returnTrue() {
        /* ARRANGE */
        final var response = new HashMap<String, String>();
        response.put("header", "some valid header");
        response.put("body", "some valid body");

        Mockito.when(contractRequestService.isValidResponseType(Mockito.eq(response))).thenReturn(true);

        /* ACT */
        final var result = service.validateContractRequestResponseMessage(response);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void validateContractRequestResponseMessage_invalidResponse_returnFalse() {
        /* ARRANGE */
        final var response = new HashMap<String, String>();
        response.put("header", "some valid header");
        response.put("body", "some valid body");

        Mockito.when(contractRequestService.isValidResponseType(Mockito.eq(response))).thenReturn(false);

        /* ACT */
        final var result = service.validateContractRequestResponseMessage(response);

        /* ASSERT */
        assertFalse(result);
    }

    @Test
    public void sendContractAgreementMessage_withValidContractAgreement_returnValidResponse() {
        /* ARRANGE */
        final var recipient = URI.create("https://localhost:8080/api/ids/data");
        final var contractId = URI.create("https://contractAgreement");
        final var desc = new ContractAgreementMessageDesc(recipient, contractId);
        final var agreement = new ContractAgreementBuilder(contractId)
                ._contractStart_(getDateAsXMLGregorianCalendar())
                .build();
        final var agreementAsRdf = agreement.toRdf();

        final var response = new HashMap<String, String>();
        response.put("header", "some header values");
        response.put("body", "some body values");

        Mockito.when(contractAgreementService.sendMessage(Mockito.eq(desc), Mockito.eq(agreementAsRdf))).thenReturn(response);

        /* ACT */
        final var result = service.sendContractAgreementMessage(recipient, agreement);

        /* ASSERT */
        assertEquals(response, result);
    }

    @Test
    public void sendContractAgreementMessage_withoutContractAgreement_throwsIllegalArgumentException() {
        /* ARRANGE */
        final var recipient = URI.create("https://localhost:8080/api/ids/data");
        final var contractId = URI.create("https://contractAgreement");
        final var desc = new ContractAgreementMessageDesc(recipient, contractId);

        final var response = new HashMap<String, String>();
        response.put("header", "some header values");
        response.put("body", "some body values");

        Mockito.when(contractAgreementService.sendMessage(Mockito.eq(desc), Mockito.eq(null))).thenReturn(response);

        /* ACT & ARRANGE */
        assertThrows(IllegalArgumentException.class, () -> service.sendContractAgreementMessage(recipient, null));
    }

    @Test
    public void validateContractAgreementResponseMessage_validResponse_returnTrue() {
        /* ARRANGE */
        final var response = new HashMap<String, String>();
        response.put("header", "some valid header");
        response.put("body", "some valid body");

        Mockito.when(contractAgreementService.isValidResponseType(Mockito.eq(response))).thenReturn(true);

        /* ACT */
        final var result = service.validateContractAgreementResponseMessage(response);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void validateContractAgreementResponseMessage_invalidResponse_returnFalse() {
        /* ARRANGE */
        final var response = new HashMap<String, String>();
        response.put("header", "some valid header");
        response.put("body", "some valid body");

        Mockito.when(contractAgreementService.isValidResponseType(Mockito.eq(response))).thenReturn(false);

        /* ACT */
        final var result = service.validateContractAgreementResponseMessage(response);

        /* ASSERT */
        assertFalse(result);
    }

    @Test
    public void sendArtifactRequestMessage_withValidInput_returnValidResponse() {
        /* ARRANGE */
        final var recipient = URI.create("https://localhost:8080/api/ids/data");
        final var elementId = URI.create("https://element");
        final var agreementId = URI.create("https://agreement");
        final var desc = new ArtifactRequestMessageDesc(recipient, elementId, agreementId);

        final var response = new HashMap<String, String>();
        response.put("header", "some header values");
        response.put("body", "some body values");

        Mockito.when(artifactRequestService.sendMessage(Mockito.eq(desc), Mockito.eq(""))).thenReturn(response);

        /* ACT */
        final var result = service.sendArtifactRequestMessage(recipient, elementId, agreementId);

        /* ARRANGE */
        assertEquals(response, result);
    }

    @Test
    public void validateArtifactResponseMessage_validResponse_returnTrue() {
        /* ARRANGE */
        final var response = new HashMap<String, String>();
        response.put("header", "some valid header");
        response.put("body", "some valid body");

        Mockito.when(artifactRequestService.isValidResponseType(Mockito.eq(response))).thenReturn(true);

        /* ACT */
        final var result = service.validateArtifactResponseMessage(response);

        /* ASSERT */
        assertTrue(result);
    }

    @Test
    public void validateArtifactResponseMessage_invalidResponse_returnFalse() {
        /* ARRANGE */
        final var response = new HashMap<String, String>();
        response.put("header", "some valid header");
        response.put("body", "some valid body");

        Mockito.when(artifactRequestService.isValidResponseType(Mockito.eq(response))).thenReturn(false);

        /* ACT */
        final var result = service.validateArtifactResponseMessage(response);

        /* ASSERT */
        assertFalse(result);
    }

    @SneakyThrows
    private XMLGregorianCalendar getDateAsXMLGregorianCalendar() {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(Date.from(date.toInstant()));
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }
}
