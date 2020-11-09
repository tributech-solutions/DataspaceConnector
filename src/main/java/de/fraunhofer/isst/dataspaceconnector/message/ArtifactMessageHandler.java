package de.fraunhofer.isst.dataspaceconnector.message;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.TypedLiteral;
import de.fraunhofer.iais.eis.util.Util;
import de.fraunhofer.isst.dataspaceconnector.model.resource.ResourceMetadata;
import de.fraunhofer.isst.dataspaceconnector.services.IdsUtils;
import de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService;
import de.fraunhofer.isst.dataspaceconnector.services.usagecontrol.PolicyHandler;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
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
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(ArtifactMessageHandler.class);

    private final TokenProvider provider;
    private final Connector connector;
    private SerializerProvider serializerProvider;

    private OfferedResourceService offeredResourceService;
    private PolicyHandler policyHandler;
    private MessageUtils messageUtils;
    private IdsUtils idsUtils;

    private ResourceMetadata resourceMetadata;
    private ArtifactRequestMessageImpl message;
    private UUID artifactId, resourceId;

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
     */
    public ArtifactMessageHandler(OfferedResourceService offeredResourceService, ConfigurationContainer configurationContainer, IdsUtils idsUtils,
                                  TokenProvider provider, PolicyHandler policyHandler, MessageUtils messageUtils, SerializerProvider serializerProvider) {
        this.offeredResourceService = offeredResourceService;
        this.provider = provider;
        this.connector = configurationContainer.getConnector();
        this.idsUtils = idsUtils;
        this.policyHandler = policyHandler;
        this.messageUtils = messageUtils;
        this.serializerProvider = serializerProvider;
    }

    /**
     * {@inheritDoc}
     *
     * This message implements the logic that is needed to handle the message. As it just returns the input as string
     * the messagePayload-InputStream is converted to a String.
     */
    @Override
    public MessageResponse handleMessage(ArtifactRequestMessageImpl message, MessagePayload messagePayload) {
        this.message = message;
        artifactId = messageUtils.uuidFromUri(message.getRequestedArtifact());

        if (messageUtils.checkForIncompatibleVersion(message.getModelVersion())) {
            LOGGER.error("Information Model version of requesting connector is not supported.");
            return ErrorResponse.withDefaultHeader(RejectionReason.VERSION_NOT_SUPPORTED, "Outbound model version not supported.", connector.getId(), connector.getOutboundModelVersion());
        }

        try {
            resourceId = messageUtils.uuidFromUri(getRequestedResource(artifactId));
            resourceMetadata = offeredResourceService.getMetadata(resourceId);

            if (messagePayload == null) {
                return accessControl();
            } else {
                return checkContractOffer(messagePayload);
            }
        } catch (Exception e) {
            LOGGER.error("Resource could not be found.");
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
        URI resourceId = null;
        for (Resource resource : offeredResourceService.getResourceList()) {
            for (Representation representation : resource.getRepresentation()) {
                UUID representationId = messageUtils.uuidFromUri(representation.getId());

                if (representationId.equals(artifactId)) {
                    resourceId = resource.getId();
                }
            }
        }
        return resourceId;
    }

    private MessageResponse checkContractOffer(MessagePayload messagePayload) {
        try {
            // read message payload as string
            String request = IOUtils.toString(messagePayload.getUnderlyingInputStream(), StandardCharsets.UTF_8);
            // deserialize string to infomodel object
            ContractRequest contractRequest = serializerProvider.getSerializer().deserialize(request, ContractRequest.class);
            // load contract offer from metadata
            ContractOffer contractOffer = serializerProvider.getSerializer().deserialize(resourceMetadata.getPolicy(), ContractOffer.class);

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
     * Checks the policy to ensure that the data may be accessed or not.
     *
     * @return The message response to the requesting connector.
     */
    private MessageResponse accessControl() {
        try {
            if (policyHandler.onDataProvision(resourceMetadata.getPolicy())) {
                return BodyResponse.create(new ArtifactResponseMessageBuilder()
                        ._securityToken_(provider.getTokenJWS())
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

    private MessageResponse acceptContract(ContractRequest contractRequest) {
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

        return BodyResponse.create(new ContractAgreementMessageBuilder()
                ._securityToken_(provider.getTokenJWS())
                ._correlationMessage_(message.getId())
                ._issued_(de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util.getGregorianNow())
                ._issuerConnector_(connector.getId())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._senderAgent_(connector.getId())
                ._recipientConnector_(Util.asList(message.getIssuerConnector()))
                .build(), contractAgreement.toRdf());
    }

    /**
     * Builds a contract rejection message with a rejection reason.
     * Future TODO: send new ContractOffer message
     *
     * @return A contract rejection message.
     */
    private MessageResponse rejectContract() {
        return BodyResponse.create(new ContractRejectionMessageBuilder()
                ._securityToken_(provider.getTokenJWS())
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
}
