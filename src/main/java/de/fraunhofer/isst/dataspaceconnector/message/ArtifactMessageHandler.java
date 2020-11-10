package de.fraunhofer.isst.dataspaceconnector.message;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.model.ResourceContract;
import de.fraunhofer.isst.dataspaceconnector.model.resource.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.services.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.services.communication.RequestService;
import de.fraunhofer.isst.dataspaceconnector.services.negotiation.ContractService;
import de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.exceptions.HttpClientException;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessageResponse;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * This @{@link de.fraunhofer.isst.dataspaceconnector.message.ArtifactMessageHandler} handles all incoming messages that
 * have a {@link de.fraunhofer.iais.eis.ArtifactRequestMessageImpl} as part one in the multipart message. This header
 * must have the correct '@type' reference as defined in the {@link de.fraunhofer.iais.eis.ArtifactRequestMessageImpl}
 * JsonTypeName annotation. In this example, the received payload is not defined and will be returned immediately.
 * Usually, the payload would be well defined as well, such that it can be deserialized into a proper Java-Object.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Component
@SupportedMessageType(ArtifactRequestMessageImpl.class)
public class ArtifactMessageHandler implements MessageHandler<ArtifactRequestMessageImpl> {
    /**
     * Constant <code>LOGGER</code>
     */
    public static final Logger LOGGER = LoggerFactory.getLogger(ArtifactMessageHandler.class);

    private final TokenProvider tokenProvider;
    private final Connector connector;
    private SerializerProvider serializerProvider;

    private OfferedResourceService offeredResourceService;
    private ContractService contractService;
    private PolicyHandler policyHandler;
    private MessageUtils messageUtils;
    private IdsUtils idsUtils;
    private RequestService requestService;

    private ResourceMetadata resourceMetadata;
    private ArtifactRequestMessageImpl message;
    private UUID artifactId, resourceId;
    private URI resourceUri;

    @Autowired
    /**
     * <p>Constructor for ArtifactMessageHandler.</p>
     *
     * @param configurationContainer a {@link de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer} object.
     * @param offeredResourceService a {@link de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService} object.
     * @param idsUtils a {@link IdsUtils} object.
     * @param tokenProvider a {@link de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider} object.
     * @param policyHandler a {@link de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler} object.
     * @param messageUtils a {@link MessageUtils} object.
     * @param serializeProvider a {@link de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider} object.
     * @param requestService a {@link RequestService} object.
     */
    public ArtifactMessageHandler(OfferedResourceService offeredResourceService, ContractService contractService,
                                  TokenProvider tokenProvider, ConfigurationContainer configurationContainer, PolicyHandler policyHandler,
                                  IdsUtils idsUtils, MessageUtils messageUtils, SerializerProvider serializerProvider, RequestService requestService) {
        this.offeredResourceService = offeredResourceService;
        this.contractService = contractService;
        this.tokenProvider = tokenProvider;
        this.connector = configurationContainer.getConnector();
        this.idsUtils = idsUtils;
        this.policyHandler = policyHandler;
        this.messageUtils = messageUtils;
        this.serializerProvider = serializerProvider;
        this.requestService = requestService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This message implements the logic that is needed to handle the message. As it just returns the input as string
     * the messagePayload-InputStream is converted to a String.
     */
    @Override
    public MessageResponse handleMessage(ArtifactRequestMessageImpl message, MessagePayload messagePayload) {
        this.message = message;
        artifactId = messageUtils.uuidFromUri(message.getRequestedArtifact());

        // return an error if the infomodel version is not compatible
        if (messageUtils.checkForIncompatibleVersion(message.getModelVersion())) {
            LOGGER.error("Information Model version of requesting connector is not supported.");
            return ErrorResponse.withDefaultHeader(RejectionReason.VERSION_NOT_SUPPORTED, "Outbound model version not supported.", connector.getId(), connector.getOutboundModelVersion());
        }

        try {
            resourceId = messageUtils.uuidFromUri(getRequestedResource(artifactId));
            resourceMetadata = offeredResourceService.getMetadata(resourceId);

            // check if a transfer contract was added to the artifact message
            if (message.getTransferContract() == null) {
                // check if the message payload is empty
                if (messagePayload != null) {
                    return checkContractOffer(messagePayload);
                } else {
                    LOGGER.error("Contract is missing.");
                    return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS, "Missing contract request.", connector.getId(), connector.getOutboundModelVersion());
                }
            } else {
                return accessControl(message.getTransferContract());
            }
        } catch (Exception e) {
            LOGGER.error("Resource could not be found: {}" + e.getMessage());
            return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND, "An artifact with the given uuid is not known to the connector: {}" + e.getMessage(), connector.getId(), connector.getOutboundModelVersion());
        }
    }

    /**
     * Gets the resource of the requested artifact.
     *
     * @param artifactId The artifact id.
     * @return The resource uuid.
     */
    private URI getRequestedResource(UUID artifactId) {
        for (Resource resource : offeredResourceService.getResourceList()) {
            for (Representation representation : resource.getRepresentation()) {
                UUID representationId = messageUtils.uuidFromUri(representation.getId());

                if (representationId.equals(artifactId)) {
                    resourceUri = resource.getId();
                }
            }
        }
        return resourceUri;
    }

    /**
     * Checks if the contract request content by the consumer complies with the contract offer by the provider.
     *
     * @param messagePayload The message payload containing a contract request.
     * @return A message response to the requesting connector.
     */
    private MessageResponse checkContractOffer(MessagePayload messagePayload) {
        try {
            // read message payload as string
            String request = IOUtils.toString(messagePayload.getUnderlyingInputStream(), StandardCharsets.UTF_8);
            // if request is empty, return rejection message
            if (request.equals("")) {
                LOGGER.error("Contract is missing.");
                return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS, "Missing contract request.", connector.getId(), connector.getOutboundModelVersion());
            }

            // deserialize string to infomodel object
            ContractRequest contractRequest = serializerProvider.getSerializer().deserialize(request, ContractRequest.class);
            // load contract offer from metadata
            ContractOffer contractOffer = serializerProvider.getSerializer().deserialize(resourceMetadata.getPolicy(), ContractOffer.class);

            // check if the contract request has the same content as the contract offer provided with the resource
            if (contractRequest.getObligation() == contractOffer.getObligation()
                    && contractRequest.getPermission() == contractOffer.getPermission()
                    && contractRequest.getProhibition() == contractOffer.getProhibition()) {
                return acceptContract(contractRequest);
            } else {
                return rejectContract();
            }
        } catch (IOException e) {
            LOGGER.error("Error: {}" + e.getMessage());
            return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS, "Could not read contract offer.", connector.getId(), connector.getOutboundModelVersion());
        }
    }

    /**
     * Accept contract by building a {@link ContractAgreement} and sending it as payload with a {@link ContractAgreementMessage}.
     *
     * @param contractRequest The contract request object from the data consumer.
     * @return The message response to the requesting connector.
     */
    private MessageResponse acceptContract(ContractRequest contractRequest) {
        LOGGER.info("Contract accepted.");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.YEAR, 1);

        ContractAgreement contractAgreement = new ContractAgreementBuilder()
                ._consumer_(contractRequest.getConsumer())
                ._provider_(connector.getMaintainer())
                ._contractDate_(idsUtils.getGregorianOf(new Date()))
                ._contractStart_(idsUtils.getGregorianOf(new Date()))
                ._contractEnd_(idsUtils.getGregorianOf(c.getTime()))
                ._obligation_(contractRequest.getObligation())
                ._permission_(contractRequest.getPermission())
                ._prohibition_(contractRequest.getProhibition())
                .build();

        // send ContractAgreement to the ClearingHouse
        try {
            requestService.sendContractAgreementMessage(contractAgreement);
        } catch (HttpClientException | IOException e) {
            LOGGER.error("Message to clearing house could not be sent: {}" + e.getMessage());
            // TODO try send later
        }

        // save contract to contract repository
        contractService.addContract(new ResourceContract(resourceUri, contractAgreement.toRdf()));
        LOGGER.info("Saved message to database: " + contractAgreement.getId());

        // send response to the data consumer
        return BodyResponse.create(new ContractAgreementMessageBuilder()
                ._securityToken_(tokenProvider.getTokenJWS())
                ._correlationMessage_(message.getId())
                ._issued_(de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util.getGregorianNow())
                ._issuerConnector_(connector.getId())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._senderAgent_(connector.getId())
                ._recipientConnector_(Util.asList(message.getIssuerConnector()))
                ._transferContract_(contractAgreement.getId())
                .build(), contractAgreement.toRdf());
    }

    /**
     * Builds a contract rejection message with a rejection reason.
     *
     * @return A contract rejection message.
     */
    private MessageResponse rejectContract() {
        LOGGER.info("Contract rejected.");
        return BodyResponse.create(new ContractRejectionMessageBuilder()
                ._securityToken_(tokenProvider.getTokenJWS())
                ._correlationMessage_(message.getId())
                ._issued_(de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util.getGregorianNow())
                ._issuerConnector_(connector.getId())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._senderAgent_(connector.getId())
                ._recipientConnector_(Util.asList(message.getIssuerConnector()))
                ._rejectionReason_(RejectionReason.BAD_PARAMETERS)
                ._contractRejectionReason_(new TypedLiteral("Contract not accepted.", "en"))
                .build(), "");
    }

    /**
     * Checks the policy to ensure that the data may be accessed or not.
     *
     * @return A message response to the requesting connector.
     */
    private MessageResponse accessControl(URI contractId) {
        LOGGER.info("Check for resource with contractId " + contractId);
        for (ResourceContract rc : contractService.getContracts()) {
            try {
                ContractAgreement contract = serializerProvider.getSerializer().deserialize(rc.getContract(), ContractAgreement.class);
                if (contract.getId() == contractId) {
                    URI rid = rc.getResourceId();
                    if (messageUtils.uuidFromUri(rid) != resourceId) {
                        LOGGER.error("Wrong contract.");
                        return ErrorResponse.withDefaultHeader(RejectionReason.BAD_PARAMETERS, "The contract agreement does not matches the found resource. " +
                                "Please refer to the right one.", connector.getId(), connector.getOutboundModelVersion());
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Contract could not be deserialized: {}" + e.getMessage());
                return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR, "INTERNAL RECIPIENT ERROR", connector.getId(), connector.getOutboundModelVersion());
            }
        }

        try {
            LOGGER.info("Execute access control...");
            if (policyHandler.onDataProvision(resourceMetadata.getPolicy())) {
                LOGGER.info("Resource access granted: " + resourceId);
                return BodyResponse.create(new ArtifactResponseMessageBuilder()
                        ._securityToken_(tokenProvider.getTokenJWS())
                        ._correlationMessage_(message.getId())
                        ._issued_(de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util.getGregorianNow())
                        ._issuerConnector_(connector.getId())
                        ._modelVersion_(connector.getOutboundModelVersion())
                        ._senderAgent_(connector.getId())
                        ._recipientConnector_(Util.asList(message.getIssuerConnector()))
                        .build(), offeredResourceService.getDataByRepresentation(resourceId, artifactId));
            } else {
                LOGGER.error("Policy restriction detected: " + policyHandler.getPattern(resourceMetadata.getPolicy()));
                return ErrorResponse.withDefaultHeader(RejectionReason.NOT_AUTHORIZED, "Policy restriction detected: You are not authorized to receive this data.", connector.getId(), connector.getOutboundModelVersion());
            }
        } catch (Exception e) {
            LOGGER.error("Policy verification error: {}" + e.getMessage());
            return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR, "INTERNAL RECIPIENT ERROR", connector.getId(), connector.getOutboundModelVersion());
        }
    }
}
