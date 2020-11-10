package de.fraunhofer.isst.dataspaceconnector.message;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.isst.dataspaceconnector.model.SentMessage;
import de.fraunhofer.isst.dataspaceconnector.services.negotiation.MessageService;
import de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService;
import de.fraunhofer.isst.ids.framework.configuration.ConfigurationContainer;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.MessageHandler;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.SupportedMessageType;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.BodyResponse;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.ErrorResponse;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessagePayload;
import de.fraunhofer.isst.ids.framework.messaging.core.handler.api.model.MessageResponse;
import de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider;
import de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;

/**
 * This @{@link ContractAgreementMessageHandler} handles all incoming messages
 * that have a {@link DescriptionRequestMessageImpl} as part one in the multipart message. This
 * header must have the correct '@type' reference as defined in the {@link DescriptionRequestMessageImpl}
 * JsonTypeName annotation. In this example, the received payload is not defined and will be returned immediately.
 * Usually, the payload would be well defined as well, such that it can be deserialized into a proper Java-Object.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Component
@SupportedMessageType(ContractAgreementMessageImpl.class)
public class ContractAgreementMessageHandler implements MessageHandler<ContractAgreementMessageImpl> {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(ContractAgreementMessageHandler.class);

    private TokenProvider tokenProvider;
    private Connector connector;
    private MessageUtils messageUtils;

    private SerializerProvider serializerProvider;
    private MessageService messageService;

    private URI artifactId;
    private ContractAgreementMessageImpl message;

    @Autowired
    /**
     * <p>Constructor for DescriptionMessageHandler.</p>
     *
     * @param offeredResourceService a {@link OfferedResourceService} object.
     * @param provider a {@link TokenProvider} object.
     * @param connector a {@link Connector} object.
     * @param configProducer a {@link de.fraunhofer.isst.ids.framework.spring.starter.ConfigProducer} object.
     * @param serializerProvider a {@link SerializerProvider} object.
     * @param messageUtils a {@link MessageUtils} object.
     */
    public ContractAgreementMessageHandler(ConfigurationContainer configurationContainer, TokenProvider tokenProvider,
                                           SerializerProvider serializerProvider, MessageUtils messageUtils, MessageService messageService) {
        this.tokenProvider = tokenProvider;
        this.connector = configurationContainer.getConnector();
        this.serializerProvider = serializerProvider;
        this.messageUtils = messageUtils;
        this.messageService = messageService;
    }

    /**
     * {@inheritDoc}
     *
     * This message implements the logic that is needed to handle the message. As it just returns the input as string
     * the messagePayload-InputStream is converted to a String.
     */
    @Override
    public MessageResponse handleMessage(ContractAgreementMessageImpl message, MessagePayload messagePayload) {
        this.message = message;

        if (messageUtils.checkForIncompatibleVersion(message.getModelVersion())) {
            LOGGER.error("Information Model version of requesting connector is not supported.");
            return ErrorResponse.withDefaultHeader(RejectionReason.VERSION_NOT_SUPPORTED, "Outbound model version not supported.", connector.getId(), connector.getOutboundModelVersion());
        }

        // find artifact id of the previous send artifact request message (correlation message of the incoming contract agreement message)
        try {
            URI messageId = message.getCorrelationMessage();
            for (SentMessage m : messageService.getMessages()) {
                ArtifactRequestMessage artifactRequestMessage = serializerProvider.getSerializer().deserialize(m.getMessage(), ArtifactRequestMessage.class);
                if (artifactRequestMessage.getId().equals(messageId)) {
                    artifactId = artifactRequestMessage.getRequestedArtifact();
                }
            }
        } catch (IOException e) {
            LOGGER.error("Deserialization error: {}" + e.getMessage());
            return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR, "INTERNAL RECIPIENT ERROR", connector.getId(), connector.getOutboundModelVersion());
        }

        // send an artifact request message without any payload to the data provider
        return BodyResponse.create(new ArtifactRequestMessageBuilder()
                ._issued_(de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util.getGregorianNow())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._issuerConnector_(connector.getId())
                ._senderAgent_(connector.getId())
                ._requestedArtifact_(artifactId)
                ._securityToken_(tokenProvider.getTokenJWS())
                ._recipientConnector_(de.fraunhofer.iais.eis.util.Util.asList(message.getIssuerConnector()))
                ._transferContract_(message.getTransferContract())
                .build(), null);
    }
}
