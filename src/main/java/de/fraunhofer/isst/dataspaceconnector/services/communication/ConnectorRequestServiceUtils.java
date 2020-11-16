package de.fraunhofer.isst.dataspaceconnector.services.communication;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.isst.dataspaceconnector.model.resource.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.model.resource.ResourceRepresentation;
import de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceService;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import de.fraunhofer.isst.ids.framework.util.MultipartStringParser;
import okhttp3.Response;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class handles received message content and saves the metadata and data to the internal database.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Service
public class ConnectorRequestServiceUtils {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(ConnectorRequestServiceUtils.class);

    private RequestedResourceService requestedResourceService;
    private SerializerProvider serializerProvider;
    private ConnectorRequestServiceImpl requestMessageService;

    @Autowired
    /**
     * <p>Constructor for ConnectorRequestServiceUtils.</p>
     *
     * @param requestedResourceService a {@link de.fraunhofer.isst.dataspaceconnector.services.resource.RequestedResourceService} object.
     * @param serializerProvider a {@link de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider} object.
     */
    public ConnectorRequestServiceUtils(RequestedResourceService requestedResourceService, SerializerProvider serializerProvider, ConnectorRequestServiceImpl requestMessageService) {
        this.requestedResourceService = requestedResourceService;
        this.serializerProvider = serializerProvider;
        this.requestMessageService = requestMessageService;
    }

    /**
     * Saves the metadata in the internal database.
     *
     * @param response The data resource as string.
     * @return The UUID of the created resource.
     * @throws java.lang.Exception if any.
     */
    public UUID saveMetadata(String response) throws Exception {
        Map<String, String> map = MultipartStringParser.stringToMultipart(response);
        String header = map.get("header");
        String payload = map.get("payload");

        try {
            serializerProvider.getSerializer().deserialize(header, DescriptionResponseMessage.class);
        } catch (Exception e) {
            throw new Exception("Wrong message type: " + header);
        }

        Resource resource;
        try {
            resource = serializerProvider.getSerializer().deserialize(payload, ResourceImpl.class);
        } catch (Exception e) {
            throw new Exception("Metadata could not be deserialized: " + payload);
        }

        try {
            return requestedResourceService.addResource(deserializeMetadata(resource));
        } catch (Exception e) {
            throw new Exception("Metadata could not be saved: " + e.getMessage());
        }
    }

    private ResourceMetadata deserializeMetadata(Resource resource) {
        List<String> keywords = new ArrayList<>();
        for(TypedLiteral t : resource.getKeyword()) {
            keywords.add(t.getValue());
        }

        List<ResourceRepresentation> representations = new ArrayList<>();
        for(Representation r : resource.getRepresentation()) {
            Artifact artifact = (Artifact) r.getInstance().get(0);
            ResourceRepresentation representation = new ResourceRepresentation(
                    UUID.randomUUID(),
                    r.getMediaType().getFilenameExtension(),
                    artifact.getByteSize().intValue(),
                    ResourceRepresentation.SourceType.LOCAL,
                    null
            );
            representations.add(representation);
        }

        return new ResourceMetadata(
                resource.getTitle().get(0).getValue(),
                resource.getDescription().get(0).getValue(),
                keywords,
                resource.getContractOffer().get(0).toRdf(),
                resource.getPublisher(),
                resource.getStandardLicense(),
                resource.getVersion(),
                representations
        );
    }

    /**
     * <p>resourceExists.</p>
     *
     * @param resourceId a {@link java.util.UUID} object.
     * @return a boolean.
     */
    public boolean resourceExists(UUID resourceId) {
        return requestedResourceService.getResource(resourceId) != null;
    }

    /**
     *
     * @param responseString
     * @param key
     * @param recipient
     * @param requestedArtifact
     * @return
     * @throws Exception
     */
    public ResponseEntity<Object> checkContractAgreementResponse(String responseString, UUID key, URI recipient, URI requestedArtifact) throws Exception {
        Map<String, String> map = MultipartStringParser.stringToMultipart(responseString);
        String header = map.get("header");
        String payload = map.get("payload");

        ContractAgreementMessage contractAgreementMessage;
        try {
            contractAgreementMessage = serializerProvider.getSerializer().deserialize(header, ContractAgreementMessage.class);
        } catch (Exception e) {
            throw new Exception("Wrong message type: " + header);
        }

        ContractAgreement contract;
        try {
            contract = serializerProvider.getSerializer().deserialize(payload, ContractAgreement.class);
        } catch (Exception e) {
            throw new Exception("Contract could not be deserialized: " + payload);
        }

        try {
            ResourceMetadata metadata = requestedResourceService.getMetadata(key);
            metadata.setPolicy(contract.toRdf());
        } catch (Exception e) {
            throw new Exception("Metadata could not be updated: " + e.getMessage());
        }

        try {
            // send new artifact request message
            Response response = requestMessageService.sendArtifactRequestMessage(recipient, requestedArtifact, contractAgreementMessage.getId(), contractAgreementMessage.getTransferContract());
            String responseAsString = response.body().string();

            // save data response
            saveData(responseAsString, key);

            // return data
            return new ResponseEntity<>("Saved at: " + key + "\n"
                    + String.format("Success: %s", (response != null)) + "\n"
                    + String.format("Body: %s", responseAsString), HttpStatus.OK);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Saves the data string in the internal database.
     *
     * @param response The data resource as string.
     * @param resourceId The resource uuid.
     * @throws java.lang.Exception if any.
     */
    public void saveData(String response, UUID resourceId) throws Exception {
        Map<String, String> map = MultipartStringParser.stringToMultipart(response);
        String header = map.get("header");
        String payload = map.get("payload");

        try {
            serializerProvider.getSerializer().deserialize(header, ArtifactResponseMessage.class);
        } catch (Exception e) {
            throw new Exception("Wrong message type: " + payload);
        }

        try {
            requestedResourceService.addData(resourceId, payload);
        } catch (Exception e) {
            throw new Exception("Data could not be saved: " + e.getMessage());
        }
    }
}
