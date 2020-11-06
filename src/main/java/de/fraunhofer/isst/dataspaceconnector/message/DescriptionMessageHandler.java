package de.fraunhofer.isst.dataspaceconnector.message;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.util.Util;
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
import java.util.UUID;

/**
 * This @{@link de.fraunhofer.isst.dataspaceconnector.message.DescriptionMessageHandler} handles all incoming messages
 * that have a {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl} as part one in the multipart message. This
 * header must have the correct '@type' reference as defined in the {@link de.fraunhofer.iais.eis.DescriptionRequestMessageImpl}
 * JsonTypeName annotation. In this example, the received payload is not defined and will be returned immediately.
 * Usually, the payload would be well defined as well, such that it can be deserialized into a proper Java-Object.
 *
 * @author Julia Pampus
 * @version $Id: $Id
 */
@Component
@SupportedMessageType(DescriptionRequestMessageImpl.class)
public class DescriptionMessageHandler implements MessageHandler<DescriptionRequestMessageImpl> {
    /** Constant <code>LOGGER</code> */
    public static final Logger LOGGER = LoggerFactory.getLogger(DescriptionMessageHandler.class);

    private OfferedResourceService offeredResourceService;
    private TokenProvider provider;
    private Connector connector;
    private MessageUtils messageUtils;

    private ConfigurationContainer configurationContainer;
    private SerializerProvider serializerProvider;

    @Autowired
    /**
     * <p>Constructor for DescriptionMessageHandler.</p>
     *
     * @param offeredResourceService a {@link de.fraunhofer.isst.dataspaceconnector.services.resource.OfferedResourceService} object.
     * @param provider a {@link de.fraunhofer.isst.ids.framework.spring.starter.TokenProvider} object.
     * @param connector a {@link de.fraunhofer.iais.eis.Connector} object.
     * @param configProducer a {@link de.fraunhofer.isst.ids.framework.spring.starter.ConfigProducer} object.
     * @param serializerProvider a {@link de.fraunhofer.isst.ids.framework.spring.starter.SerializerProvider} object.
     * @param messageUtils a {@link MessageUtils} object.
     */
    public DescriptionMessageHandler(OfferedResourceService offeredResourceService, ConfigurationContainer configurationContainer,
                                     TokenProvider provider, SerializerProvider serializerProvider, MessageUtils messageUtils) {
        this.offeredResourceService = offeredResourceService;
        this.provider = provider;
        this.connector = configurationContainer.getConnector();
        this.configurationContainer = configurationContainer;
        this.serializerProvider = serializerProvider;
        this.messageUtils = messageUtils;
    }

    /**
     * {@inheritDoc}
     *
     * This message implements the logic that is needed to handle the message. As it just returns the input as string
     * the messagePayload-InputStream is converted to a String.
     */
    @Override
    public MessageResponse handleMessage(DescriptionRequestMessageImpl requestMessage, MessagePayload messagePayload) {
        ResponseMessage responseMessage = new DescriptionResponseMessageBuilder()
                ._securityToken_(provider.getTokenJWS())
                ._correlationMessage_(requestMessage.getId())
                ._issued_(de.fraunhofer.isst.ids.framework.messaging.core.handler.api.util.Util.getGregorianNow())
                ._issuerConnector_(connector.getId())
                ._modelVersion_(connector.getOutboundModelVersion())
                ._senderAgent_(connector.getId())
                ._recipientConnector_(Util.asList(requestMessage.getIssuerConnector()))
                .build();

        if (messageUtils.checkForIncompatibleVersion(requestMessage.getModelVersion())) {
            LOGGER.error("Information Model version of requesting connector is not supported.");
            return ErrorResponse.withDefaultHeader(RejectionReason.VERSION_NOT_SUPPORTED, "Outbound model version not supported.", connector.getId(), connector.getOutboundModelVersion());
        }

        if (requestMessage.getRequestedElement() != null) {
            UUID resourceId = messageUtils.uuidFromUri(requestMessage.getRequestedElement());

            try {
                Resource resource = offeredResourceService.getOfferedResources().get(resourceId);
                return BodyResponse.create(responseMessage, resource.toRdf());
            } catch (Exception e) {
                LOGGER.error("Resource could not be found: {}", e.getMessage());
                return ErrorResponse.withDefaultHeader(RejectionReason.NOT_FOUND, String.valueOf(e.getMessage()), connector.getId(), connector.getOutboundModelVersion());
            }
        } else {
            try {
                BaseConnectorImpl connector = (BaseConnectorImpl) configurationContainer.getConnector();
                connector.setResourceCatalog(Util.asList(new ResourceCatalogBuilder()
                        ._offeredResource_(offeredResourceService.getResourceList())
                        .build()));
                return BodyResponse.create(responseMessage, serializerProvider.getSerializer().serialize(connector));
            } catch (IOException e) {
                LOGGER.error("Self description could not be created: {}", e.getMessage());
                return ErrorResponse.withDefaultHeader(RejectionReason.INTERNAL_RECIPIENT_ERROR, "Self-description could not be created: {}" + e.getMessage(), connector.getId(), connector.getOutboundModelVersion());
            }
        }
    }
}
